package raft;

public class Leader extends Node {
    @Override
    public void respondToRequestVote() {

    }

    @Override
    public Node run() {
        return this;
    }
}
