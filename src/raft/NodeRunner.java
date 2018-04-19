package raft;

// extends thread, because we will run one of these for each Node in the cluster
public class NodeRunner extends Thread {
    public static void main(String[] args) {
        // initialize the node (all nodes are followers when first initialized)
        // we will probably initialize based on some configuration that we can make asynchronous
        Node node = null;

        while (true) {
            // run() will return the type of node we need for the next time it runs
            node = node.run();
        }
    }
}
