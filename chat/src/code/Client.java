package code;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//una lista per accodare i messaggi,
//il server lo riceve e lo mette nella lista, in attesa che venga approvato,
//altrimenti tutto si blocca.
//un thread che riceve le cose e le mette nella lista,
//il main permette all'amministratore di approvare

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
    InetAddress serverAddress;

    static DateFormat dateFormat = new SimpleDateFormat("HH:mm");

    int i = 17; // ctrl
    int j = 10; // enter
    boolean s1 = false;
    boolean s2 = false;

    public void presSend() throws IOException {
        send(name + " @ " + dateFormat.format(new Date()) + " $ " + sendArea.getText());

        byte[] inBytes = new byte[1024];
        DatagramPacket receivePack = new DatagramPacket(inBytes, inBytes.length);
        String receiveString = "";
        try {
            socket.receive(receivePack);
            receiveString = new String(receivePack.getData());
        } catch (SocketTimeoutException e) {
            JOptionPane.showMessageDialog(panel1, "server not connected");
        } catch (IOException i) {
            i.printStackTrace();
        }
        if (receiveString.contains("yes"))
            sendArea.setText("");
        else if (receiveString.contains("ko")) // client connected to a closed server
            JOptionPane.showMessageDialog(panel1, "client disconnected");
    }

    public Client(String name, String serverAddressString) throws IOException {
        socket = new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverAddressString);
        this.name = name;
        send(name); // send Server this Client's name, to ask if is already used

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    presSend();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        sendArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //                System.out.println("pre" + e.getKeyCode());
                if (i == e.getKeyCode())
                    s1 = true;

                if (j == e.getKeyCode())
                    s2 = true;

                if (s1 == true && s2 == true) {
                    try {
                        presSend();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            public void keyReleased(KeyEvent e) {
                if (i == e.getKeyCode())
                    s1 = false;

                if (j == e.getKeyCode())
                    s2 = false;
            }
        });
    }

    public void send(String text) throws IOException { // send text to Server
        byte[] outBytes = (text + "\n").getBytes();
        DatagramPacket sendPack = new DatagramPacket(
                outBytes, outBytes.length, serverAddress, 6787);
        System.out.println("send:\n" + text);
        socket.send(sendPack);
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame(args[0]);
        Client client = new Client(args[0], args[1]);
        frame.setContentPane(client.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        byte[] inBytes = new byte[1024];
        DatagramPacket receivePack = new DatagramPacket(inBytes, inBytes.length);

        try {
            client.socket.receive(receivePack);
        } catch (SocketTimeoutException e) {
            JOptionPane.showMessageDialog(client.panel1, "server unavailable");
        }
        String receiveString = new String(receivePack.getData());

        if (receiveString.contains("yes")) { // free username
            new Receive(client.receiveArea);
            frame.pack();
            frame.setVisible(true);

            DateFormat yearDay = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            client.receiveArea.setText("--- " + yearDay.format(new Date()) + " ---\n");
        } else { // username already used
            JOptionPane.showMessageDialog(client.panel1, "username already used");
            frame.dispose();
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
    panel1.setLayout(new GridLayoutManager(5, 6, new Insets(0, 0, 0, 0), -1, -1));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel1.add(scrollPane1, new GridConstraints(1, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(300, 150), null, 0, false));
    receiveArea = new JTextArea();
    receiveArea.setEditable(false);
    receiveArea.setLineWrap(true);
    scrollPane1.setViewportView(receiveArea);
    sendButton = new JButton();
    sendButton.setText("Send");
    panel1.add(sendButton, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 50), null, 0, false));
    final Spacer spacer1 = new Spacer();
    panel1.add(spacer1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(5, 5), null, null, 0, false));
    final Spacer spacer2 = new Spacer();
    panel1.add(spacer2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(5, 5), null, null, 0, false));
    final Spacer spacer3 = new Spacer();
    panel1.add(spacer3, new GridConstraints(1, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(5, 5), null, null, 0, false));
    final Spacer spacer4 = new Spacer();
    panel1.add(spacer4, new GridConstraints(0, 5, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(5, 5), null, null, 0, false));
    final Spacer spacer5 = new Spacer();
    panel1.add(spacer5, new GridConstraints(4, 1, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(5, 5), null, null, 0, false));
    final Spacer spacer6 = new Spacer();
    panel1.add(spacer6, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(5, 5), null, null, 0, false));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel1.add(scrollPane2, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 50), null, 0, false));
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
