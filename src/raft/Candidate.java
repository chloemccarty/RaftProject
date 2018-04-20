package raft;

import connect.Network;

import java.io.IOException;

import static raft.Message.MessageType.REQUEST_VOTES;

public class Candidate extends Node {
    long electionStarted;
    long electionTimeout;
    int votesReceived;
    boolean forfeit;

    public Candidate(Node node) {
        super(node);
        electionStarted = System.currentTimeMillis();
        // this will need to be configured to be in a nicer range probably
        electionTimeout = (long) (Math.random() + 1) * 200 + 300;
    }

    @Override
    public Node run() throws IOException {
        apply();
        startElection();

        while (true) {
            Message msg = checkForInput();
            handleMessage(msg);

            if (votesReceived > (numNodes + 1) / 2) {
                // return a leader Node
                return new Leader(this);
            }
            else if (forfeit) {
                // if we received a heartbeat, there's a new leader
                return new Follower(this);
            }
            else if (timerExpired()){
                // no leader was elected, return a new candidate
                return new Candidate(this);
            }
            // else just keep running until one of these happens
        }
    }

    /**
     *  send out RequestVote RPCs to all other nodes
     * @throws IOException
     */
    private void startElection() throws IOException {
        term++;
        votesReceived = 1;
        votedFor = id;

        RequestVote.RequestVoteMessage.Builder builder = RequestVote.RequestVoteMessage.newBuilder();
        builder.setCandidateId(this.id);
        builder.setTerm(this.term);
        RequestVote.RequestVoteMessage rvm = builder.build();

        // TODO implement with AppendEntries Stuff
        // builder.setLastLogIndex();
        // builder.setLastLogTerm();

        // send to each socket
        for (String ip : config) {
            Network.send(REQUEST_VOTES, rvm, ip);
        }
    }

    /**
     * manipulates instance data that run() uses to determine if we won the election or not
     * I'm not a huge fan of this organization, so we can change it later, but it's just kind of what I did
     * @param message
     */
    @Override
    public void handleMessage(Message message) {
        if (message.type == Message.MessageType.APPEND_ENTRIES) {
            // TODO (later)
            // if we received valid append_Entries, forfeit candidacy
            forfeit = true;
        }
        else if (message.type == Message.MessageType.APPEND_ENTRIES_RESPONSE) {
            // TODO (later)
        }
        else if (message.type == Message.MessageType.REQUEST_VOTES) {
            respondToRequestVote();
        }
        if (message.type == Message.MessageType.REQUEST_VOTES_RESPONSE) {
            RequestVoteRespo.RequestVoteResponse r = (RequestVoteRespo.RequestVoteResponse)message.message;
            if (r.getVoteGranted())
                votesReceived++;
            electionStarted = System.currentTimeMillis();
        }
        // might make a call to send() to respond to some of these as we implement them more
    }

    @Override
    public void respondToRequestVote() {
        // TODO implement Candidate voting logic
    }

    /**
     * Tests whether the election has timed out or not
     * @return true if the electionTimeout is expired
     */
    private boolean timerExpired() {

        if (System.currentTimeMillis() - electionStarted > electionTimeout) {
            return true;
        }
        return false;
    }
}
