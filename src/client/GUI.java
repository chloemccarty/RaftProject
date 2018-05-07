package client;

import raft.Message;
import raft.NodeRunner;
import raft.LogEntry;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;

public class GUI extends Thread {

    private boolean leader;
    public boolean partitioned;
    public JTextField deleteIndex;
    public JButton deleteSubmit;
    public JTextField appendString;
    public JButton appendSubmit;
    public JCheckBox connectedCheck;
    public JCheckBox leaderCheck;
    public JPanel leaderButtons;
    public JPanel content;

    public GUI(boolean partitioned) {
        this.partitioned = partitioned;
        try {
            SwingUtilities.invokeAndWait(() -> init());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void updateDisplay() {
        content.repaint();
    }

    private class HelloWorldDisplay extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString("Welcome to Raft!", 30, 30);
            g.drawString("By Chloe, Alex, Joel, Elijah", 40, 45);
            if (leader)
                g.drawString("Current Status: Leader", 30, 60);
            else
                g.drawString("Current Status: Not Leader", 30, 60);
            g.drawString("Database: " + NodeRunner.node.database, 30, 75);
        }
    }

    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == appendSubmit) {
                String text = appendString.getText();
                LogEntry le = new LogEntry();
                le.cmd = "1," + text;
                NodeRunner.messageQueue.add(new Message(Message.MessageType.CLIENT_APPEND, le));
                appendString.setText("");
            }
            else if (source == deleteSubmit) {
                String text = deleteIndex.getText();
                LogEntry le = new LogEntry();
                le.cmd = "0," + text;
                NodeRunner.messageQueue.add(new Message(Message.MessageType.CLIENT_DELETE, le));
                deleteIndex.setText("");
            }
            else if (source == connectedCheck) {
                boolean checked = connectedCheck.isSelected();
                partitioned = !checked;
            }
            else if (source == leaderCheck) {
                leader = leaderCheck.isSelected();
                leaderButtons.setVisible(leader);
                content.repaint();
            }
        }
    }


    /**
     * Create objects
     */
    public void init() {
        HelloWorldDisplay displayPanel = new HelloWorldDisplay();

        appendString = new JTextField(15);
        appendString.setMargin( new Insets(5,5,5,5) );
        appendSubmit = new JButton("Submit String");
        deleteIndex = new JTextField(5);
        deleteIndex.setMargin( new Insets(5,5,5,5) );
        deleteSubmit = new JButton("Delete from Index");

        ButtonHandler listener = new ButtonHandler();
        appendSubmit.addActionListener(listener);
        deleteSubmit.addActionListener(listener);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1));
        leaderButtons = new JPanel();
        leaderButtons.setLayout(new GridLayout(2, 1));
        JPanel bp1 = new JPanel();
        bp1.add(appendString);
        bp1.add(appendSubmit);
        leaderButtons.add(bp1);
        JPanel bp2 = new JPanel();
        bp2.add(deleteIndex);
        bp2.add(deleteSubmit);
        leaderButtons.add(bp2);
        buttonPanel.add(leaderButtons);

        JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new GridLayout(1,2));
        connectedCheck = new JCheckBox("Connected");
        connectedCheck.addActionListener(listener);
        connectedCheck.setSelected(true);

        leaderCheck = new JCheckBox("Leader");
        leaderCheck.addActionListener(listener);
        leaderCheck.setSelected(leader);
        leaderCheck.setEnabled(false);

        checkPanel.add(connectedCheck);
        checkPanel.add(leaderCheck);
        buttonPanel.add(checkPanel);



        content = new JPanel();
        content.setLayout(new GridLayout(2,1));
        content.add(displayPanel);
        content.add(buttonPanel);



    }

    /**
     * Shows the window
     */
    public void display() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame window = new JFrame("Raft Client");
                window.setContentPane(content);
                window.setSize(350, 300);
                window.setLocation(200, 150);
                window.setVisible(true);
                setLeaderStatus(leader);

            }
        });
    }

    public void setLeaderStatus(boolean leader) {
        this.leader = leader;
        leaderCheck.setSelected(leader);
        leaderButtons.setVisible(leader);
        content.repaint();

        // TODO: may need to repaint?
    }


}