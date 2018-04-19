package raft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Candidate extends Node {
    long electionTimer;

    public Candidate(Node node) {
        super(node);
    }




    @Override
    public void respondToRequestVote() {

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
        for (Socket socket : config) {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            rvm.writeDelimitedTo(out);
        }

        // TODO add an Message object which extends thread. Instantiate one for each node in the configuration
        // it will send out the node and also listen for a response and return.


        return votes >= (numNodes + 1) / 2;
    }
}
