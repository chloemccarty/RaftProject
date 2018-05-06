package raft;

import connect.Network;

import java.io.IOException;

import static raft.Message.MessageType.REQUEST_VOTES_RESPONSE;

public class Follower extends Node {

    long startTime;
    long electionTimeout;
    boolean confirm = true;

    public Follower() throws IOException {
        initConfig();
        votedFor = -1;
        NodeRunner.client.log("Initializing node as follower...");
    }

    public Follower(Node node) {
        super(node);
        startTime = System.currentTimeMillis();
        electionTimeout = (long) (Math.random() + 1) * 200 + 500;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.type == Message.MessageType.APPEND_ENTRIES) {
            // TODO (later) we must respond appropriately to the leader
            // Question: Are able to contain an array of log entries in our message? Or are we only implementing
            // a one entry per message system?
            AppendEntries.AppendEntriesMessage ae = (AppendEntries.AppendEntriesMessage) message.message;

            if (ae.getTerm() < this.term) {
                // Ignore the message and return false, decrement nextIndex on the leader server
                confirm = false;
            }
            else if (ae.getPrevLogIndex() == -1 || ae.getEntriesCount() == 0) {
                // Log is empty and the message must be a heartbeat.
                confirm = true;
            }
            else if (log.get(ae.getPrevLogIndex()).term != ae.getPrevLogTerm() ) {
                // return false
                confirm = false;
            }

            // If the message was good, proceed to append entries.
            if (confirm) {
                for (int i=log.size(); i<log.size() + ae.getEntriesCount() - 1; i++) {
                    LogEntry entry = new LogEntry();
                    entry.term = ae.getTerm();
                    entry.cmd = ae.getEntries(i).getMessage();
                    log.add(entry);
                }
            }


            if (ae.getLeaderCommit() > this.commitIndex) {
                // this.commitIndex = ae.getLeaderCommit();
                // this.commitIndex = log.size() - 1;
                this.commitIndex = Math.min(this.commitIndex, log.size()-1);
            }

            //  build a response message to leader
            AppendEntries.Response.Builder builder = AppendEntries.Response.newBuilder();
            builder.setTerm(this.term);
            builder.setSuccess(confirm);
            AppendEntries.Response resp = builder.build();
            String ip = config.get(ae.getLeaderId());
            Network.send(Message.MessageType.APPEND_ENTRIES_RESPONSE, resp, ip);

            //reset confirm to true at the end of this collection of if statements so it can be re-processed by the preceding if statements
            confirm = true;

        } else if (message.type == Message.MessageType.APPEND_ENTRIES_RESPONSE) {
            // ignore
        } else if (message.type == Message.MessageType.REQUEST_VOTES) {
            respondToRequestVote(message);
        } else if (message.type == Message.MessageType.REQUEST_VOTES_RESPONSE) {
            // ignore
        }
    }

    @Override
    public void respondToRequestVote(Message message) {
        RequestVote.RequestVoteMessage rvm = (RequestVote.RequestVoteMessage) message.message;
        RequestVoteRespo.RequestVoteResponse.Builder builder = RequestVoteRespo.RequestVoteResponse.newBuilder();

        if (rvm.getTerm() >= this.term &&
                this.votedFor == -1 &&
                rvm.getLastLogTerm() >= this.log.get(this.log.size()-1).term &&
                rvm.getLastLogIndex() >= this.lastApplied) {

            // we have not voted for anyone, vote for this candidate
            this.votedFor = rvm.getCandidateId();
            NodeRunner.client.log("Voting for Candidate " + this.votedFor);
            builder.setVoteGranted(true);
            setTerm(rvm.getTerm());
            builder.setTerm(this.term);
        } else if (rvm.getTerm() < this.term) {
            if (this.votedFor == -1) {
                NodeRunner.client.log("Refusing to vote for Candidate. My term: " + this.term +
                        ". Their term: " + rvm.getTerm() + ". Voted for: No one");
            } else {
                NodeRunner.client.log("Refusing to vote for Candidate. My term: " + this.term +
                        ". Their term: " + rvm.getTerm() + ". Voted for: " + this.votedFor);
            }

            // I don't think we need to update our term if theirs is smaller than ours,
            // we need to set their term to ours?
            // setTerm(rvm.getTerm());
            builder.setVoteGranted(false);
            builder.setTerm(this.term);
        } else {
            if (this.votedFor == -1) {
                NodeRunner.client.log("Refusing to vote for Candidate. My term: " + this.term +
                        ". Their term: " + rvm.getTerm() + ". Voted for: No one");
            } else {
                NodeRunner.client.log("Refusing to vote for Candidate. My term: " + this.term +
                        ". Their term: " + rvm.getTerm() + ". Voted for: " + this.votedFor);
            }
            // update our term
            builder.setVoteGranted(false);
            builder.setTerm(this.term);
        }

        RequestVoteRespo.RequestVoteResponse rvr = builder.build();
        String candidateIp = config.get(rvm.getCandidateId());
        Network.send(REQUEST_VOTES_RESPONSE, rvr, candidateIp);
    }

    @Override
    public Node run() {
        apply();

        // Followers only (1) respond to AppendEntries/RequestVotes or
        // (2) become a candidate if an election timeout occurs
        while (true) {
            Message message = checkForInput();
            if (message != null) {
                NodeRunner.client.log("Message received by follower");
                handleMessage(message);

                // reset timer because we received an AppendEntries or a RequestVote
                startTime = System.currentTimeMillis();
            }

            // Need to commit and execute log entries after handling the message.

            // no AppendEntries or RequestVotes received and timeout occurred,
            // convert to candidate
            if (timerExpired()) {
                NodeRunner.client.log("Heart-beat timer expired");
                return new Candidate(this);
            }

        }
    }


    /**
     * Tests whether the election has timed out or not
     *
     * @return true if the electionTimeout is expired
     */
    private boolean timerExpired() {
        if (System.currentTimeMillis() - startTime > electionTimeout) {
            return true;
        }
        return false;
    }


}
