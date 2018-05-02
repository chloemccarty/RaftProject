package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class GUI extends Thread {

    public boolean leader;
    public boolean partitioned;
    public JTextField deleteIndex;
    public JButton deleteSubmit;
    public JTextField appendString;
    public JButton appendSubmit;
    public JCheckBox connectedCheck;
    public JPanel leaderButtons;

    public GUI(boolean partitioned) {
        this.partitioned = partitioned;
    }

    private class HelloWorldDisplay extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString("Welcome to Raft!", 30, 30);
            g.drawString("By Chloe, Alex, Joel, Elijah", 40, 45);
        }
    }

    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == appendSubmit) {
                String text = appendString.getText();
                // send
            }
            else if (source == deleteSubmit) {
                String text = deleteIndex.getText();
                // send
            }
            else if (source == connectedCheck) {
                boolean checked = connectedCheck.isSelected();
                partitioned = !checked;
            }
        }
    }


    public void init() {
        HelloWorldDisplay displayPanel = new HelloWorldDisplay();

        // create the display panel

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


        connectedCheck = new JCheckBox("Connected");
        connectedCheck.addActionListener(listener);
        buttonPanel.add(connectedCheck);


        JPanel content = new JPanel();
        content.setLayout(new GridLayout(2,1));
        content.add(displayPanel);
        content.add(buttonPanel);

        JFrame window = new JFrame("Raft Client");
        window.setContentPane(content);
        window.setSize(350, 300);
        window.setLocation(200, 150);
        window.setVisible(true);

        // should be false the first time around
        setLeaderStatus(false);
    }

    public void setLeaderStatus(boolean leader) {
        // avoid race condition of leaderButtons not initialized yet
        if (leaderButtons != null)
            leaderButtons.setVisible(leader);
        // TODO: may need to repaint?
    }


}