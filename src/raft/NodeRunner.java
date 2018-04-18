package raft;

import raft.Node.NodeType;
import raft.Node.*;

// extends thread, because we will run one of these for each Node in the cluster
public class NodeRunner extends Thread {
    public static void main(String[] args) {
        // initialize the node (all nodes are followers when first initialized)
        Node node = new Follower();
        // initialize based on some configuration
        node.initConfig();

        while (true) {
            // run() will return the type of node we need for the next time it runs
            node = node.run();
        }
    }

}
