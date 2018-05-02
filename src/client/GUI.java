package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI {

    public static JTextField deleteIndex;
    public static JButton deleteSubmit;
    public static JTextField appendString;
    public static JButton appendSubmit;
    public static JCheckBox connectedCheck;

    private static class HelloWorldDisplay extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString("Welcome to Raft!", 30, 30);
            g.drawString("By Chloe, Alex, Joel, Elijah", 40, 45);
        }
    }

    private static class ButtonHandler implements ActionListener {
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
                if (!checked)
                    partition();
                else
                    ;
            }
        }

        private void partition() {
        }
    }


    public static void main(String[] args) {

        HelloWorldDisplay displayPanel = new HelloWorldDisplay();

        // create the display panel

        appendString = new JTextField(15);
        appendString.setMargin( new Insets(5,5,5,5) );
        appendSubmit = new JButton("Submit String");  // TODO add a listener
        deleteIndex = new JTextField(5);
        deleteIndex.setMargin( new Insets(5,5,5,5) );
        deleteSubmit = new JButton("Delete from Index");  // TODO add a listener

        ButtonHandler listener = new ButtonHandler();
        appendSubmit.addActionListener(listener);
        deleteSubmit.addActionListener(listener);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1));
        JPanel bp1 = new JPanel();
        bp1.add(appendString);
        bp1.add(appendSubmit);
        buttonPanel.add(bp1);
        JPanel bp2 = new JPanel();
        bp2.add(deleteIndex);
        bp2.add(deleteSubmit);
        buttonPanel.add(bp2);


        connectedCheck = new JCheckBox("Connected");
        connectedCheck.addActionListener(listener);
        buttonPanel.add(connectedCheck);

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(2,1));
        content.add(displayPanel);
        content.add(buttonPanel);

        JFrame window = new JFrame("GUI Gang");
        window.setContentPane(content);
        window.setSize(350, 300);
        window.setLocation(200, 150);
        window.setVisible(true);

    }
}