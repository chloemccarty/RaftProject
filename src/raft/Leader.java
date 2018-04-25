package raft;

import connect.Network;

public class Leader extends Node {
    boolean forfeit = false;

    public Leader(Node node) {
        super(node);
    }

    @Override
    public void handleMessage(Message message) {
        // if term number is greater, immediately relinquish leadership
        if (message == null)
            return;
        if (message.type == Message.MessageType.APPEND_ENTRIES) {
            // TODO
        }
        else if (message.type == Message.MessageType.APPEND_ENTRIES_RESPONSE) {
            // TODO
        }
        else if (message.type == Message.MessageType.REQUEST_VOTES) {
            respondToRequestVote(message);
        }
        if (message.type == Message.MessageType.REQUEST_VOTES_RESPONSE) {
            // shouldn't even get this message. We shouldn't have sent out a ReQuestVotes as a leader
        }
        // will make a call to send response
    }

    @Override
    public Node run() {
        apply();
        // check messages
        // react to messages
        // send response
        Message m = checkForInput();
        if (m != null) {
            handleMessage(m);
            if (forfeit)
                return new Follower(this);
        }


        return this;
    }
}
