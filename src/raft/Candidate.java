package raft;

import java.util.ArrayList;

public class Candidate extends Node {

    public Candidate() {
        log = new ArrayList<LogEntry>();
    }

    @Override
    public void respondToRequestVote() {

    }

    @Override
    public Node run() {
        // returns either a leader or a follower, but never another candidate
        return null;
    }
}
