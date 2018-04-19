package raft;
import raft.RequestVote.Candidate;

public abstract class Node {
    int id;
    int term;
    NodeType state;
    // TODO should all nodes have a queue to hold results of votes?
    enum NodeType {FOLLOWER, CANDIDATE, LEADER};
    // log
    // arraylist of int terms and String cmd;
    // class LogEntry {
    // int term;
    // String cmd;
    // }

    Candidate.Builder c = Candidate.newBuilder();
    public abstract void respondToRequestVote();

    /**
     *
     * @return An instance of a node that this node becomes after running (e.g. if a change from candidate to leader is necessary)
     */
    public abstract Node run();


}
