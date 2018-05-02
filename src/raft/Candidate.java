package raft;

import client.Client;
import connect.Network;

import java.io.IOException;

import static raft.Message.MessageType.REQUEST_VOTES;

public class Candidate extends Node {
    long electionStarted;
    long electionTimeout;
    int votesReceived;

    public Candidate(Node node) {
        super(node);
        System.out.println("Initializing node as candidate...");
        electionStarted = System.currentTimeMillis();
        // this will need to be configured to be in a nicer range probably
        electionTimeout = (long) (Math.random() + 1) * 200 + 500;
    }

    @Override
    public Node run() throws IOException {
        apply();
        startElection();

        while (true) {
            Message msg = checkForInput();
            if (msg != null) {
                System.out.println("Message received by candidate.");
                handleMessage(msg);
            }


            if (votesReceived > (numNodes + 1) / 2) {
                // return a leader Node
                NodeRunner.client.log("Votes needed to win: " + (numNodes + 1) / 2);
                NodeRunner.client.log("Election won with " + votesReceived + " votes");
                return new Leader(this);
            }
            else if (forfeit) {
                // if we received a heartbeat, there's a new leader
                return new Follower(this);
            }
            else if (timerExpired()){
                // no leader was elected, return a new candidate
                NodeRunner.client.log("Election timed out.");
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
        NodeRunner.client.log("Starting election...");
        term++;
        votesReceived = 1;
        votedFor = id;

        RequestVote.RequestVoteMessage.Builder builder = RequestVote.RequestVoteMessage.newBuilder();
        builder.setCandidateId(this.id);
        builder.setTerm(this.term);
        // TODO implement with AppendEntries Stuff
        builder.setLastLogIndex(-1);
        builder.setLastLogTerm(-1);
        RequestVote.RequestVoteMessage rvm = builder.build();

        // send to each socket
        for (String ip : config) {
            Network.send(REQUEST_VOTES, rvm, ip);
        }
    }

    /**
     * manipulates instance data that run() uses to determine if we won the election or not
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
            respondToRequestVote(message);
        }
        if (message.type == Message.MessageType.REQUEST_VOTES_RESPONSE) {
            RequestVoteRespo.RequestVoteResponse rvr = (RequestVoteRespo.RequestVoteResponse)message.message;
            if (rvr.getVoteGranted()) {
                votesReceived++;
                NodeRunner.client.log("Votes received: " + votesReceived);
            }
            electionStarted = System.currentTimeMillis();
        }
        // might make a call to send() to respond to some of these as we implement them more
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
