package raft;

import client.Client;
import com.google.protobuf.GeneratedMessageV3;
import connect.Network;

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
    boolean forfeit;


    public Node(Node that) {
        this.id = that.id;
        this.term = that.term;
        this.log = that.log;
        this.config = that.config;
        this.votedFor = -1;
        this.commitIndex = that.commitIndex;
        this.lastApplied = that.lastApplied;
        this.numNodes = that.numNodes;
    }

    public Node() {
        this.log = new ArrayList<LogEntry>();
    }


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
        System.out.println("Configuring cluster...");

        // be sure not to add self to list of nodes to send to
        String thisIP = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Local IP address: " + thisIP);

        List<String> ips = Files.readAllLines(Paths.get("Config.txt"));
        config = new ArrayList<>();
        for (String ip : ips) {
            // TODO change to .equals()
            if (!thisIP.equals(ip)) {
                System.out.println("adding node at ip " + ip);
                config.add(ip);
            }
        }
        numNodes = config.size() + 1; // include ourself in the count
        System.out.println(numNodes + " nodes in cluster");
    }

    public void respondToRequestVote(Message message) {
        RequestVote.RequestVoteMessage rvm = (RequestVote.RequestVoteMessage) message.message;

        // candidate's log is greater than the leader
        if (rvm.getTerm() > this.term) {
            forfeit = true;
            this.term = rvm.getTerm();
        }

        // respond to sender
        RequestVoteRespo.RequestVoteResponse.Builder builder = RequestVoteRespo.RequestVoteResponse.newBuilder();
        if (forfeit) {
            builder.setVoteGranted(true);
            System.out.println("Voting for candidate: " + rvm.getCandidateId());
        } else {
            builder.setVoteGranted(false);
        }
        builder.setTerm(this.term);
        RequestVoteRespo.RequestVoteResponse rvr = builder.build();
        String ip = config.get(rvm.getCandidateId());
        Network.send(Message.MessageType.REQUEST_VOTES_RESPONSE, rvr, ip);
    }
}
