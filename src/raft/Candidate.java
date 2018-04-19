package raft;

public class Candidate extends Node {
    @Override
    public void respondToRequestVote() {

    }

    @Override
    public Node run() {
        // returns either a leader or a follower, but never another candidate
        term++;

        // vote for self

        // reset election timer

        // requestVote() RPC

        // if (votesReceived >= (n/2)+1
        //  state = leader
        // else if appendEntries received
        //  state = follower
        // else if electionTimeout
        //  ???
        // return state
        return null;
    }
}
