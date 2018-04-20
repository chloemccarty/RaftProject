package raft;

import com.google.protobuf.GeneratedMessageV3;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.util.Queue;

import static raft.NodeRunner.messageQueue;

public abstract class Node {
    int id;
    int term;
    int numNodes;
    int votedFor;
    int commitIndex;
    int lastApplied;
    List<LogEntry> log;
    List<String> config;
    final int PORT = 6666;


    public Node(Node that) {
        this.id = that.id;
        this.term = that.term;
        this.log = that.log;
        this.config = that.config;
        this.votedFor = that.votedFor;
        this.commitIndex = that.commitIndex;
        this.lastApplied = that.lastApplied;

    }

    public Node() {
        this.log = new ArrayList<LogEntry>();
    }

    // this will also set votedFor
    public abstract void respondToRequestVote();

    public Message checkForInput() {
        // check for input
        if (!messageQueue.isEmpty()) {
            // pull a message out
            return messageQueue.poll();
        }
        return null;
    }

    public abstract void handleMessage(Message message);


    /**
     *
     * @return An instance of a node that this node becomes after running (e.g. if a change from candidate to leader is necessary)
     */
    public abstract Node run() throws IOException;

    /**
     * All nodes must apply new logs if commitIndex > lastApplied
     */
    public void apply() {

    }

    /**
     * Read IP addresses of nodes from config file.
     */
    public void initConfig() throws IOException {

        // TODO have a loop that continually accepts requests to this socket

        ServerSocket server = new ServerSocket(PORT);

        // be sure not to add self to list of nodes to send to
        String thisIP = InetAddress.getLocalHost().toString();
// TODO don't have a socket that you keep around
        // self contained send message
        List<String> ips = Files.readAllLines(Paths.get("C:\\repos\\Raft\\Config.txt"));
        for (String ip : ips) {
            if (thisIP != ip) {
                config.add(ip);
            }

        }
        numNodes = ips.size();
    }


}
