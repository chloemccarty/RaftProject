package raft;

public class Message {
    public enum MessageType {APPEND_ENTRIES, APPEND_ENTRIES_RESPONSE, REQUEST_VOTES, REQUEST_VOTES_RESPONSE}
    MessageType type;
    // TODO: another variable that contains the content and tells us how to proceed

    public Message(MessageType type) {
        this.type = type;
    }
}
