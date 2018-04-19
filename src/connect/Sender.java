package connect;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Sender extends Thread {
    byte type;
    byte[] data;
    String ip;
    static int PORT = 6666;


    public Sender(byte type, byte[] data, String ip) {
        this.type = type;
        this.data = data;
        this.ip = ip;
    }

    @Override
    public void run() {
        // send message and data to other node
        try {
            Socket socket = new Socket(ip, PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeByte(type);
            out.write(data);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
