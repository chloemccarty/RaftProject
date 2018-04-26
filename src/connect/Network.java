package connect;

import com.google.protobuf.GeneratedMessageV3;
import raft.Message;

public class Network {

    public static void listen() {
        new Listener().start();
    }

    // public static void send(int type, byte[] data, String IP) {
    public static void send(Message.MessageType type, GeneratedMessageV3 data, String IP) {
        System.out.println("Sending message");
        new Sender((byte) type.value, data, IP).start();
    }

}
