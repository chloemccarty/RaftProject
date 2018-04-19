package raft;

public class Candidate extends Node {
    @Override
    public void respondToRequestVote() {

    }

    @Override
    public Node run() {
        // returns either a leader or a follower, but never another candidate
        return null;
    }
}
