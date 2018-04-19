package raft;

import java.io.IOException;

public class NodeRunner {
    public static void main(String[] args) throws IOException {
        // initialize the node (all nodes are followers when first initialized)
        // the follower constructor also initializes based on some configuration file
        Node node = new Follower();

        while (true) {
            // run() will return the type of node we need for the next time it runs
            node = node.run();
        }
    }

}
