package raft;

import java.io.IOException;

public class Follower extends Node {

    // the type of this will depend on how we implement time keeping
    // i decided to make it a long since System.currentTimeMillis() returns a long
    // and I imagine we'll use this method in our implementation
    long electionTimer;

    public Follower() throws IOException {
        initConfig();
    }

    public Follower(Node node) {
        super(node);
    }

    @Override
    public void respondToRequestVote() {

    }

    @Override
    public Node run() {
        apply();
        return this;
    }
}
