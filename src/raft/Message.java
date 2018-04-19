package raft;

public class Message {
    enum MessageType {APPEND_ENTRIES, APPEND_ENTRIES_RESPONSE, REQUEST_VOTES, REQUEST_VOTES_RESPONSE}
    // TODO: another variable that contains the content and tells us how to proceed
}
