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
        send(name);
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

    public void send(String text) throws IOException {
        byte[] outBytes = (text + "\n").getBytes();
        DatagramPacket sendPack = new DatagramPacket(
                outBytes, outBytes.length, InetAddress.getByName("localhost"), 6787);
        socket.send(sendPack);
    }

    public boolean request() throws IOException {
        byte[] inBytes = new byte[1024];
        DatagramPacket receivePack = new DatagramPacket(inBytes, inBytes.length);
        socket.receive(receivePack);
        String strReceive = new String(receivePack.getData());
        return strReceive.contains("yes");
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame(args[0]);
        Client client = new Client(args[0]);
        frame.setContentPane(client.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        if (client.request()) {
            Receive receive = new Receive(client.receiveArea);
            frame.pack();
            frame.setVisible(true);
        } else {
            frame.dispose();
            JOptionPane.showInputDialog("username già in uso");
        }
    }
}