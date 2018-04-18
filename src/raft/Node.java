package raft;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    int id;
    int term;
    // We might not even need this
    NodeType state;
    int VotedFor;
    enum NodeType {FOLLOWER, CANDIDATE, LEADER};
    List<LogEntry> log;
    List<Node> config;

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

    public Node(Node that) {
        this.id = that.id;
        this.term = that.term;
        this.log = that.log;
        this.config = that.config;
        this.VotedFor = that.VotedFor;
    }

    public Node() {
        this.log = new ArrayList<LogEntry>();
    }

    // this will also set VotedFor
    public abstract void respondToRequestVote();

    /**
     *
     * @return An instance of a node that this node becomes after running (e.g. if a change from candidate to leader is necessary)
     */
    public abstract Node run();

    /**
     * Read nodes in from config file
     */
    public void initConfig() {

    }

}
