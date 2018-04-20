package raft;

import com.google.protobuf.GeneratedMessageV3;

import static raft.NodeRunner.messageQueue;

public class Leader extends Node {

    public Leader(Node node) {
        super(node);
    }

    @Override
    public void respondToRequestVote() {

    }

    @Override
    public void HandleMessage(Message message) {
        // if term number is greater, immediately relinquish leadership

        if (message.type == Message.MessageType.APPEND_ENTRIES) {
            // TODO
        }
        else if (message.type == Message.MessageType.APPEND_ENTRIES_RESPONSE) {
            // TODO
        }
        else if (message.type == Message.MessageType.REQUEST_VOTES) {
            // TODO implement Leader voting logic
            respondToRequestVote();
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
        Message m = getMessageFromQueue();
        HandleMessage(m);


        return this;
    }
}
