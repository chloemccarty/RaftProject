package raft;

import com.google.protobuf.GeneratedMessageV3;

public class Message {
    public enum MessageType {
        APPEND_ENTRIES(0), APPEND_ENTRIES_RESPONSE(1), REQUEST_VOTES(2), REQUEST_VOTES_RESPONSE(3), CLIENT_APPEND(4), CLIENT_DELETE(5);

        MessageType(int val) {
            this.value = val;
        }

        public int value;
    }
    MessageType type;
    GeneratedMessageV3 message;
    LogEntry clientCmd;
    // TODO: another variable that contains the content and tells us how to proceed

    public Message(MessageType type, GeneratedMessageV3 message) {
        this.type = type;
        this.message = message;

    }

    public Message(MessageType type, LogEntry command) {
        this.type = type;
        this.clientCmd = command;

    }
}
