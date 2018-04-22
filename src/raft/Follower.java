package raft;

import connect.Network;

import java.io.IOException;

import static raft.Message.MessageType.REQUEST_VOTES_RESPONSE;

public class Follower extends Node {

    // the type of this will depend on how we implement time keeping
    // i decided to make it a long since System.currentTimeMillis() returns a long
    // and I imagine we'll use this method in our implementation
    long startTime;
    long electionTimeout;

    public Follower() throws IOException {
        initConfig();
    }

    public Follower(Node node) {
        super(node);
        startTime = System.currentTimeMillis();

        // used same set up as in the Candidate class so this may need
        // configured as well
        electionTimeout = (long) (Math.random() + 1) * 200 + 300;
    }

    @Override
    public void respondToRequestVote() {

    }

    @Override
    public void handleMessage(Message message) {
        if (message.type == Message.MessageType.APPEND_ENTRIES) {
            // TODO (later) we must respond appropriately to the leader
        } else if (message.type == Message.MessageType.APPEND_ENTRIES_RESPONSE) {
            // TODO (later)
        } else if (message.type == Message.MessageType.REQUEST_VOTES) {
            // TODO not sure if this method is needed and if it is
            // it would need the message as a parameter or else we could not
            // get information from the message in the method
            // TODO respondToRequestVote();

            RequestVote.RequestVoteMessage rv = (RequestVote.RequestVoteMessage) message.message;
            // TODO check to see if candidate's log is at least as up-to-date
            // as our own (later)

            // we have not voted for anyone, vote for this candidate
            if (this.votedFor == 0) {
                this.votedFor = rv.getCandidateId();
                RequestVoteRespo.RequestVoteResponse.Builder builder = RequestVoteRespo.RequestVoteResponse.newBuilder();
                builder.setVoteGranted(true);
                RequestVoteRespo.RequestVoteResponse rvr = builder.build();

                // we need the candidate's ip address
                String candidateIp = "";
                Network.send(REQUEST_VOTES_RESPONSE, rvr, candidateIp);
            }
        } else if (message.type == Message.MessageType.REQUEST_VOTES_RESPONSE) {
            // would followers even get this message if they don't send RequestVote RPCs?
            RequestVoteRespo.RequestVoteResponse r = (RequestVoteRespo.RequestVoteResponse) message.message;
        }
    }

    @Override
    public Node run() {
        apply();

        // TODO not sure if anything else needs to be in this while loop
        // followers only (1) respond to AppendEntries or (2) become a candidate if
        // an election timeout occurs
        while (true) {
            Message message = checkForInput();
            if (message != null) {
                handleMessage(message);

                // reset timer because we received an AppendEntries or a RequestVote
                startTime = System.currentTimeMillis();
            }

            // no AppendEntries or RequestVotes received and timeout occurred,
            // convert to candidate
            if (timerExpired()) {
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
