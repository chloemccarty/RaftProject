package raft;

import connect.*;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

public class NodeRunner {

    public static Queue<Message> messageQueue;

    public static void main(String[] args) throws IOException {


        messageQueue = new ConcurrentLinkedQueue<Message>();
        // will a synchronous queue keep things from getting messed up with all the threading?

        // create a listener that will listen and "dispatch" messages
        Network.listen();

        // initialize the node (all nodes are followers when first initialized)
        Node node = new Follower();


        while (true) {
            // run() will return the type of node we need for the next time it runs

            node = node.run();
        }
    }

}
