package raft;

import java.util.List;

public abstract class Node {
    int id;
    int term;
    // We might not even need this
    NodeType state;
    enum NodeType {FOLLOWER, CANDIDATE, LEADER};
    List<LogEntry> log;

    // We might also not even need this
    public static Node NodeFactory(NodeType state) {
        switch (state) {
            case FOLLOWER:
                return new Follower();
            case LEADER:
                return new Leader();
            case CANDIDATE:
                return new Candidate();
            default:
                return new Follower();
        }
    }

    public Node(List<LogEntry> log) {
        this.log = log;
    }

    public abstract void respondToRequestVote();

    /**
     *
     * @return An instance of a node that this node becomes after running (e.g. if a change from candidate to leader is necessary)
     */
    public abstract Node run();


}
