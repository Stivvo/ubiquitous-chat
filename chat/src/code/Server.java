package code;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;

public class Server {

    private static int maxClients = 50;

    public static
    void main(String[] args) throws IOException {
        DatagramSocket receiveSock = new DatagramSocket(6787);
        MulticastSocket sendSock = new MulticastSocket();
        sendSock.joinGroup(InetAddress.getByName("225.4.5.6"));

        ArrayList<Pair<DatagramPacket, String>> clients = new ArrayList<Pair<DatagramPacket, String>>();
        BufferedReader kybrd = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            byte[] inBytes = new byte[1024];
            DatagramPacket receivePack = new DatagramPacket(inBytes, inBytes.length);
            receiveSock.receive(receivePack);
            String strReceive = new String(receivePack.getData());
            strReceive = strReceive.substring(0, receivePack.getLength());

            boolean foundPort, foundName;
            foundPort = foundName = false;
            for (Pair<DatagramPacket, String> i : clients) { // ricerca user e porta in clients
                if (i.getKey().getPort() == receivePack.getPort()) {
                    foundPort = true;
                }
                if (i.getValue().equals(strReceive)) {
                    foundName = true;
                }
            }
            String strSend;
            System.out.println("sender: port = " + receivePack.getPort() + ", name = " + foundName);
            if (!foundPort) { // nuovo client
                if (foundName) { // username già in uso
                    strSend = "no";
                } else { // username libero
                    strSend = "yes";
                    clients.add(new Pair<>(receivePack, strReceive));
                }
                System.out.println(strSend);
                byte[] outBytes = strSend.getBytes();
                DatagramPacket sendPack = new DatagramPacket(
                        outBytes, outBytes.length, receivePack.getAddress(), receivePack.getPort());
                receiveSock.send(sendPack);
            } else { // client già connesso, è arrivato un messaggio
                System.out.println("receved:");
                System.out.print(strReceive);
                System.out.println("allow? [Y/n] ");

                if (!kybrd.readLine().equals("n")) {
                    byte[] outBytes = strReceive.getBytes();
                    DatagramPacket sendPack = new DatagramPacket(
                            outBytes, outBytes.length, InetAddress.getByName("225.4.5.6"), 6786);
                    sendSock.send(sendPack);
                    System.out.println("send: " + strReceive);
                } else
                    System.out.println("not allowed");
            }
        }
    }
}
