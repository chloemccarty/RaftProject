package connect;

import com.google.protobuf.GeneratedMessageV3;
import raft.Message;

public class Network {

    public static Listener listen() {
        return new Listener();
    }

    // public static void send(int type, byte[] data, String IP) {
    public static void send(Message.MessageType type, GeneratedMessageV3 data, String IP) {
                // TODO convert type to int!!!
        new Sender((byte) type.value, data, IP);
    }

}
