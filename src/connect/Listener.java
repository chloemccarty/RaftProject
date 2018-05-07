package connect;

import raft.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {
    static int PORT = 6666;

    public class Handler extends Thread {
        Socket socket;

        public Handler(Socket socket) throws IOException {
            this.socket = socket;
            System.out.println("Adding message to queue");
        }

        @Override
        public void run() {
            DataInputStream in = null;
            try {
                NodeRunner.client.log("NOTICE: message received in listener");
                in = new DataInputStream(socket.getInputStream());
                byte msgType = in.readByte();
                NodeRunner.client.log("NOTICE: message received of type " + (int)msgType);

                boolean partitioned = NodeRunner.client.partitioned();

                if (msgType == 0) {
                    AppendEntries.AppendEntriesMessage ae = AppendEntries.AppendEntriesMessage.parseFrom(in);
                    // if it's not partitioned, add it to the queue
                    // That is, we're ignoring incoming messages if we are currently partitioned
                    if (!partitioned) {
                        NodeRunner.messageQueue.add(new Message(Message.MessageType.APPEND_ENTRIES, ae));
                    }
                    else {
                        NodeRunner.client.log("WARNING: partitioned");
                    }
                }
                else if (msgType == 1) {
                    AppendEntries.Response aer = AppendEntries.Response.parseFrom(in);
                    if (!partitioned) {
                        NodeRunner.messageQueue.add(new Message(Message.MessageType.APPEND_ENTRIES_RESPONSE, aer));
                    }
                }
                else if (msgType == 2) {
                    RequestVote.RequestVoteMessage rv = RequestVote.RequestVoteMessage.parseFrom(in);
                    if (!partitioned) {
                        NodeRunner.messageQueue.add(new Message(Message.MessageType.REQUEST_VOTES, rv));
                    }
                }
                else if (msgType == 3) {
                    RequestVoteRespo.RequestVoteResponse rvr = RequestVoteRespo.RequestVoteResponse.parseFrom(in);
                    if (!partitioned) {
                        NodeRunner.messageQueue.add(new Message(Message.MessageType.REQUEST_VOTES_RESPONSE, rvr));
                    }
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        // create server socket that listens to requests and then puts them in our Node queue
        try {
            ServerSocket sock = new ServerSocket(PORT);

            while (true) {
                new Handler(sock.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
