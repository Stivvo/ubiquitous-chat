package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.*;

//due main per il client, una che permette di dare in input i messaggi,
//l'altra che mostra quello che arriva
//così evito un thread o un timer

//una lista per accodare i messaggi,
//il server lo riceve e lo mette nella lista, in attesa che venga approvato,
//altrimenti tutto si blocca.
//un thread che riceve le cose e le mette nella lista, il main permette all'amministratore di approvare

class Receive extends Thread {
    private MulticastSocket multicast;
    private JTextArea area;

    public Receive(JTextArea area) throws IOException {
        this.area = area;
        multicast = new MulticastSocket(6786);
        multicast.joinGroup(InetAddress.getByName("225.4.5.6"));
        start();
    }
    public void run() {
        while (true) {
            byte[] inBytes = new byte[1024];
            DatagramPacket receivePack = new DatagramPacket(inBytes, inBytes.length);
            try {
                multicast.receive(receivePack);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String receiveString = new String(receivePack.getData());
            System.out.println("received: " + receiveString);
            area.setText(area.getText() + receiveString);
        }
    }
}

public class Client {
    private JPanel panel1;
    private JTextArea receiveArea;
    private JTextArea sendArea;
    private JButton sendButton;

    private DatagramSocket socket;
    private String name;

    public Client(String name) throws IOException {
        socket = new DatagramSocket();
        this.name = name;
        send(name); // send Server this Client's name, to ask is is already used
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    send(name + " @ " + sendArea.getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                sendArea.setText("");
            }
        });
    }

    public void send(String text) throws IOException { // send text to Server
        byte[] outBytes = (text + "\n").getBytes();
        DatagramPacket sendPack = new DatagramPacket(
                outBytes, outBytes.length, InetAddress.getByName("localhost"), 6787);
        socket.send(sendPack);
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame(args[0]);
        Client client = new Client(args[0]);
        frame.setContentPane(client.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        byte[] inBytes = new byte[1024];
        DatagramPacket receivePack = new DatagramPacket(inBytes, inBytes.length);
        client.socket.receive(receivePack); // read server response
        String strReceive = new String(receivePack.getData());

        if (strReceive.contains("yes")) { // free username
            Receive receive = new Receive(client.receiveArea);
            frame.pack();
            frame.setVisible(true);
        } else { // username already used
            frame.dispose();
            JOptionPane.showMessageDialog(frame, "username already used");
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 6, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(300, 150), null, 0, false));
        receiveArea = new JTextArea();
        scrollPane1.setViewportView(receiveArea);
        sendButton = new JButton();
        sendButton.setText("Send");
        panel1.add(sendButton, new com.intellij.uiDesigner.core.GridConstraints(3, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 50), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(5, 5), null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(5, 5), null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 3, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(5, 5), null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 4, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(5, 5), null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(5, 5), null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(3, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(5, 5), null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 50), null, 0, false));
        sendArea = new JTextArea();
        sendArea.setLineWrap(true);
        scrollPane2.setViewportView(sendArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}