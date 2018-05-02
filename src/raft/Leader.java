package raft;

import client.Client;
import connect.Network;

public class Leader extends Node {

    public Leader(Node node) {
        super(node);
        NodeRunner.client.log("Initializing as leader...");
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
    }

    @Override
    public Node run() {
        while (true) {
            apply();
            // check messages
            // react to messages
            // send response
            Message m = checkForInput();
            if (m != null) {
                NodeRunner.client.log("Message received by leader");
                handleMessage(m);
                // TODO find out why this isn't running
                if (forfeit) {
                    NodeRunner.client.log("Reverting to follower state");
                    return new Follower(this);
                }
            }

        }
    }
}
