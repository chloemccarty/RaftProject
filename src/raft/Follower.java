package raft;

import connect.Network;

import java.io.IOException;

import static raft.Message.MessageType.APPEND_ENTRIES_RESPONSE;
import static raft.Message.MessageType.REQUEST_VOTES_RESPONSE;

public class Follower extends Node {

    // the type of this will depend on how we implement time keeping
    // i decided to make it a long since System.currentTimeMillis() returns a long
    // and I imagine we'll use this method in our implementation
    long startTime;
    long electionTimeout;
    boolean confirm = true;

    public Follower() throws IOException {
        initConfig();
        votedFor = -1;
        System.out.println("Initializing node as follower...");
    }

    public Follower(Node node) {
        super(node);
        startTime = System.currentTimeMillis();

        // used same set up as in the Candidate class so this may need
        // configured as well
        electionTimeout = (long) (Math.random() + 1) * 200 + 500;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.type == Message.MessageType.APPEND_ENTRIES) {
            // TODO (later) we must respond appropriately to the leader
            //Question: Are able to contain an array of log entries in our message? Or are we only implementing
            // a one entry per message system?
            AppendEntries.AppendEntriesMessage ae = (AppendEntries.AppendEntriesMessage) message.message;

            if (ae.getTerm() < this.term) {
                //Ignore the message and return false, decrement nextIndex on the leader server
                confirm = false;
            }

            else if (log.get(ae.getPrevLogIndex()).term != ae.getPrevLogTerm() ) {
                //return false
                confirm = false;
            }

            //This is supposed to check if it is a heartbeat.
            else if (ae.getEntriesCount() == 0) confirm = true;

           /* else if (ae.hasPrevLogIndex() && log.size()>0 && log.size()>ae.getPrevLogIndex()+1){ //Not sure if we need this last term here.
                //Simple check to avoid adding errors with entries that DNE as well as to see if we are dealing with an index
                //in the log that is not updated to the current leader. If not true, continue to
                //the if (confirm) statement where it will simply append all entries.
                //Go through all the entries in the message and check if any entry from the message's term doesn't match
                for (int i=ae.getPrevLogIndex()+1; i<log.size() + ae.getEntriesCount() - 1; i++) {
                    if(log.size() < i) break; //avoid checking out of bounds of the log entry array.
                    else if (log.get(i).term != ae.getTerm()) {
                        //remove existing entry and all that follow, then update the log
                        confirm = true;
                        for (int j=i; j<log.size(); j++) {
                            log.remove(j);
                        }
                        break;
                    }
                }
            }*/


            //If the message was good, proceed to append entries.
            if (confirm) {
                for (int i=log.size()-1; i<log.size() + ae.getEntriesCount() - 1; i++) {
                    LogEntry entry = new LogEntry();
                    entry.term = ae.getTerm();
                    entry.cmd = ae.getEntries(i).getMessage();
                    log.add(entry);
                }
            }
           /* else if (log.get(ae.getPrevLogIndex() + 1).term != ae.getTerm()) {
                //Delete the log's existing entry and all that follow it, then update the log (append entries not already in the log)
                for (int i=ae.getPrevLogIndex(); i<log.size(); i++) {
                    log.remove(i);
                    log.add(ae.)
                }

            }*/

            if (ae.getLeaderCommit() > this.commitIndex) {
                this.commitIndex = ae.getLeaderCommit();
                //this.commitIndex = log.size() - 1;   // This wsa written in the paper; didn't understand it: min(this.commitIndex, log.size()-1);
            }

            //  TODO build a response message to leader
            AppendEntries.Response.Builder builder = AppendEntries.Response.newBuilder();
            builder.setTerm(this.term);
            builder.setSuccess(confirm);
            AppendEntries.Response resp = builder.build();
            String ip = config.get(ae.getLeaderId());
            Network.send(Message.MessageType.APPEND_ENTRIES_RESPONSE, resp, ip);




        } else if (message.type == Message.MessageType.APPEND_ENTRIES_RESPONSE) {
            // TODO (later)
        } else if (message.type == Message.MessageType.REQUEST_VOTES) {
            respondToRequestVote(message);
        } else if (message.type == Message.MessageType.REQUEST_VOTES_RESPONSE) {
            // ignore
        }
    }

    @Override
    public void respondToRequestVote(Message message) {
        RequestVote.RequestVoteMessage rvm = (RequestVote.RequestVoteMessage) message.message;
        // TODO check to see if candidate's log is at least as up-to-date
        // as our own (later)
        RequestVoteRespo.RequestVoteResponse.Builder builder = RequestVoteRespo.RequestVoteResponse.newBuilder();
        if (rvm.getTerm() >= this.term && this.votedFor == -1) {
            // we have not voted for anyone, vote for this candidate
            this.votedFor = rvm.getCandidateId();
            builder.setVoteGranted(true);
            builder.setTerm(this.term);

        } else {
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

        // TODO not sure if anything else needs to be in this while loop
        // followers only (1) respond to AppendEntries or
        // (2) become a candidate if an election timeout occurs
        while (true) {
            Message message = checkForInput();
            if (message != null) {
                System.out.println("Message received by follower");
                handleMessage(message);

                // reset timer because we received an AppendEntries or a RequestVote
                startTime = System.currentTimeMillis();
            }

            //Need to commit and execute log entries after handling the message.

            // no AppendEntries or RequestVotes received and timeout occurred,
            // convert to candidate
            if (timerExpired()) {
                System.out.println("Heart-beat timer expired");
                return new Candidate(this);
            }
        }
    }

    @Override
    public void apply() {
        if (this.commitIndex > this.lastApplied) {
            //execute commands in log from (lastApplied+1) up through commitIndex
            for (int i=lastApplied; i<=commitIndex; i++) {
                LogEntry entry = log.get(i);
                String[] command = new String[2];
                command = entry.cmd.split(",");

                if (command[0] == "1") {
                    //execute append command. Append takes a string input to append to database (which is one big string).
                    database.concat(command[1]);
                }

                else {
                    //execute delete command. Delete takes an int input to tell from which position to begin deleting
                    //delete(entry.cmd.split(",")[1]);
                    StringBuilder sb = new StringBuilder(database);
                    sb.delete(Integer.parseInt(command[1]), database.length());


                }
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
