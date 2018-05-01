package connect;

import raft.Message;
import raft.NodeRunner;
import raft.RequestVote;
import raft.RequestVoteRespo;

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
                in = new DataInputStream(socket.getInputStream());
                byte msgType = in.readByte();
            // read in message


                if (msgType == 0) {
                    // TODO appendEntries protobuf

                    NodeRunner.messageQueue.add(new Message(Message.MessageType.APPEND_ENTRIES, null));
                    //need to include the ip address or the node id in the message so that we can update nextIndex and matchIndex
                }
                else if (msgType == 1) {
                    NodeRunner.messageQueue.add(new Message(Message.MessageType.APPEND_ENTRIES_RESPONSE, null));
                }
                else if (msgType == 2) {
                    // TODO read in RequestVotes object from in
                    RequestVote.RequestVoteMessage rv = RequestVote.RequestVoteMessage.parseFrom(in);
                    NodeRunner.messageQueue.add(new Message(Message.MessageType.REQUEST_VOTES, rv));
                }
                else if (msgType == 3) {
                    // TODO read in RequestVotesResponse object from in
                    RequestVoteRespo.RequestVoteResponse rvr = RequestVoteRespo.RequestVoteResponse.parseFrom(in);
                    NodeRunner.messageQueue.add(new Message(Message.MessageType.REQUEST_VOTES_RESPONSE, rvr));
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
