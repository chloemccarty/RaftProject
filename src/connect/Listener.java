package connect;

import raft.Message;
import raft.NodeRunner;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {
    static int PORT = 6666;

    public class Handler extends Thread {

        public Handler(Socket socket) throws IOException {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            // read in message... they should be self describing
            byte msgType = in.readByte();
            if (msgType == 1) {
                NodeRunner.messageQueue.add(new Message(Message.MessageType.APPEND_ENTRIES));
            }
            else if (msgType == 2) {
                NodeRunner.messageQueue.add(new Message(Message.MessageType.APPEND_ENTRIES_RESPONSE));
            }
            else if (msgType == 3) {
                NodeRunner.messageQueue.add(new Message(Message.MessageType.REQUEST_VOTES));
            }
            else if (msgType == 4) {
                NodeRunner.messageQueue.add(new Message(Message.MessageType.REQUEST_VOTES_RESPONSE));
            }

        }
    }

    @Override
    public void run() {
        // create server socket that listens to requests and then puts them in our Node queue
        try {
            ServerSocket sock = new ServerSocket(PORT);

            while (true) {
                new Handler(sock.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
