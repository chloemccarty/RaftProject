package client;

public class Client extends Thread {
    public boolean leader;
    public boolean partitioned;

    public Client(boolean isLeader) {
        leader = isLeader;
        // TODO figure out how the SENDER will know if a node is partitioned?
        partitioned = false;
    }

    @Override
    public void run() {
        // TODO only run full menu the leader node
        // TODO only allow partitioning on the followers
        try {
            while (true) {
                if (leader) {
                    leaderMenu();
                }
                else {
                    partitionOnly();
                }
                sleep(1000);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // very important
        }

    }

    /**
     * Allows a user to partition / reconnect a node
     */
    private static void partitionOnly() {
        // set partitioned = true if partitioned
        // else partitioned = false
    }

    /**
     * allows a user to partition / reconnect the node AND concatenate / delete strings
     */
    private static void leaderMenu() {
        // append string
        // delete from idx
        // crash a node
    }
}
