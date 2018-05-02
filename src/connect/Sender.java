package connect;

import com.google.protobuf.GeneratedMessageV3;
import raft.NodeRunner;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Sender extends Thread {
    byte type;
    GeneratedMessageV3 data;
    String ip;
    static int PORT = 6666;


    public Sender(byte type, GeneratedMessageV3 data, String ip) {
        this.type = type;
        this.data = data;
        this.ip = ip;
    }

    @Override
    public void run() {
        // send message and data to other node
        try {
            if (!NodeRunner.client.partitioned()) {
                Socket socket = new Socket(ip, PORT);

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeByte(type);
                // send length of data
                data.writeTo(out);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
