package raft;

import com.google.protobuf.GeneratedMessageV3;

public class Message {
    public enum MessageType {APPEND_ENTRIES, APPEND_ENTRIES_RESPONSE, REQUEST_VOTES, REQUEST_VOTES_RESPONSE}
    MessageType type;
    GeneratedMessageV3 message;
    // TODO: another variable that contains the content and tells us how to proceed

    public Message(MessageType type, GeneratedMessageV3 message) {
        this.type = type;
        //RequestVote.RequestVoteMessage rv = RequestVote.RequestVoteMessage.parseFrom(in);
    }
}
