package raft;

import client.Client;
import connect.*;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

public class NodeRunner {

    public static Queue<Message> messageQueue;
    public static Client client;


    public static void main(String[] args) throws IOException {


        messageQueue = new ConcurrentLinkedQueue<Message>();
        // will a synchronous queue keep things from getting messed up with all the threading?

        // create a listener that will listen and "dispatch" messages
        Network.listen();
        // start the client thread
        client = new Client(false);
        client.start();

        // initialize the node (all nodes are followers when first initialized)
        Node node = new Follower();

        while (true) {
            // run() will return the type of node we need for the next time it runs
            node = node.run();
            // update the client so we know whether to provide the leader or follower menu to the users
            client.setLeader(node.getClass() == Leader.class);



        }
    }

}
