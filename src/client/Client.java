package client;

import java.util.Scanner;

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
    private void partitionOnly() {
        // set partitioned = true if partitioned
        // else partitioned = false
        System.out.println("Currently connected: " + !partitioned);
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Would you like to toggle the connection status? Enter yes if yes, do nothing if no");

            String input = in.nextLine();
            if (input.equalsIgnoreCase("yes")) {
                if (partitioned) {
                    System.out.println("Reconnecting...");
                } else {
                    System.out.println("Partitioning the current server.");
                }
                partitioned = !partitioned;
                break;
            } else {
                System.out.println("Please enter valid input");
            }
        }

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
