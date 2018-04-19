package raft;

public abstract class Node {
    int id;
    int term;
    NodeType state;
    enum NodeType {FOLLOWER, CANDIDATE, LEADER};
    // log

    public abstract void respondToRequestVote();

    /**
     *
     * @return An instance of a node that this node becomes after running (e.g. if a change from candidate to leader is necessary)
     */
    public abstract Node run();


}
