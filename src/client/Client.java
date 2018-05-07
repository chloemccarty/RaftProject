package client;

import javax.swing.*;
import java.util.Scanner;

public class Client extends Thread {
    public GUI guiClient;
    private boolean leader;

    /**
     * To be called for printing "logs" rather than using syso print
     * @param s
     */
    public void log(String s) {
        System.out.println(s);
        if (guiClient.content != null)
            guiClient.content.repaint();
    }

    public void setLeader(boolean lead) {
        this.leader = lead;
        SwingUtilities.invokeLater(() -> guiClient.setLeaderStatus(leader));
    }

    public Client(boolean isLeader) {
        guiClient = new GUI(false);
    }

    public boolean partitioned() {
        while (guiClient == null) {
            ;
        }
        return guiClient.partitioned;
    }

    @Override
    public void run() {
        guiClient.display();
    }

}
