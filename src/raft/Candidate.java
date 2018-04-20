package raft;

import connect.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static raft.Message.MessageType.REQUEST_VOTES;

public class Candidate extends Node {
    long electionTimer;

    public Candidate(Node node) {
        super(node);
    }




    @Override
    public void respondToRequestVote() {

    }

    @Override
    public void HandleMessage(Message message) {
        // if term number is greater, immediately relinquish leadership

        if (message.type == Message.MessageType.APPEND_ENTRIES) {
            // TODO
        }
        else if (message.type == Message.MessageType.APPEND_ENTRIES_RESPONSE) {
            // TODO
        }
        else if (message.type == Message.MessageType.REQUEST_VOTES) {
            // TODO implement Leader voting logic
            respondToRequestVote();
        }
        if (message.type == Message.MessageType.REQUEST_VOTES_RESPONSE) {
            // shouldn't even get this message. We shouldn't have sent out a ReQuestVotes as a leader
        }
        // will make a call to send response
    }

    @Override
    public Node run() throws IOException {
       apply();

        boolean leader = startElection();
        if (leader) {
            // return a leader Node
            return new Leader(this);
        }
        else {
            // if we received a heartbeat, there's a new leader
            // return follower
            // otherwise, no leader was elected, return a new candidate
        }

        // returns either a leader or a follower
        return null;
    }

    // send out RequestVote RPCs to all other nodes
    private boolean startElection() throws IOException {
        // build message
        term++;
        int votes = 1;

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

        // TODO add an Message object which extends thread. Instantiate one for each node in the configuration
        // it will send out the node and also listen for a response and return.


        return votes >= (numNodes + 1) / 2;
    }
}
