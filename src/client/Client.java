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
        GUI.init();

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
        System.out.println("What would you like to do?");
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("Append string: Type [AS]");
            System.out.println("Delete character from index: Type [DI]");
            System.out.println("Partition node: Type [PN]");
            System.out.println("Restore node: Type [RN]");
            String userInput = in.nextLine();

            if (userInput.equalsIgnoreCase("AS")) {
                System.out.println("Type what you would like to append to the string: ");
                userInput = in.nextLine();
                // addToLog(userInput);
                break;
            } else if (userInput.equalsIgnoreCase("DI")) {
                System.out.println("Type the index that you want to start deleting from: ");
                int deleteIndex = in.nextInt();
                // delete characters from deleteIndex on
                // deleteFromIdx(deleteIndex);
                break;
            } else if (userInput.equalsIgnoreCase("PN") || userInput.equalsIgnoreCase("RN")) {
                // partitionOnly();
                break;
            } else {
                System.out.println("Please enter valid input\n");
            }
        }
    }
}
