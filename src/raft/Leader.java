package raft;

public class Leader extends Node {

    public Leader(Node node) {
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
