package connect;

import com.google.protobuf.GeneratedMessageV3;
import raft.Message;
import raft.NodeRunner;

public class Network {

    public static void listen() {
        new Listener().start();
    }

    // public static void send(int type, byte[] data, String IP) {
    public static void send(Message.MessageType type, GeneratedMessageV3 data, String IP) {
        NodeRunner.client.log("Sending message");
        if (!IP.equalsIgnoreCase(NodeRunner.node.config.get(NodeRunner.node.id)))
            new Sender((byte) type.value, data, IP).start();
    }

}
