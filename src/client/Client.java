package client;

import java.util.Scanner;

public class Client extends Thread {
    public GUI guiClient;
    private boolean leader;

    public void setLeader(boolean leader) {
        this.leader = leader;
        guiClient.setLeaderStatus(leader);
    }

    public Client(boolean isLeader) {
        guiClient = new GUI(false);
    }

    public boolean partitioned() {
        return guiClient.partitioned;
    }

    @Override
    public void run() {
        guiClient.init();
    }

}
