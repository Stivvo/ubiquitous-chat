package code;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) throws IOException {
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

            boolean foundPort = false, foundName = false;
            for (Pair<DatagramPacket, String> i : clients) { // search user and port in clients
                if (i.getKey().getPort() == receivePack.getPort())
                    foundPort = true;
                if (i.getValue().equals(strReceive))
                    foundName = true;
            }
            String strSend;
            if (!foundPort) { // new client, allow it?
                if (foundName) // already used username
                    strSend = "no";
                else if (strReceive.contains("@")) // user conencted to a closed server
                    strSend = "ko";
                else { // free username
                    strSend = "yes";
                    clients.add(new Pair<>(receivePack, strReceive));
                }
                System.out.println("new client: port = " + receivePack.getPort() + ", name: " + strReceive +
                        "already used username = " + foundName + ", allow: " + strSend);
            } else { // an already connected cliend sent a message
                System.out.println(strReceive + "\nallow? [Y/n] ");
                if (!kybrd.readLine().equals("n")) { // allow the message?
                    byte[] outBytes = strReceive.getBytes();
                    DatagramPacket sendPack = new DatagramPacket(
                            outBytes, outBytes.length, InetAddress.getByName("225.4.5.6"), 6786);
                    sendSock.send(sendPack);
                } else
                    System.out.println("not allowed");
                strSend = "yes";
            }
            // confirm to Client 
            byte[] outBytes = strSend.getBytes();
            DatagramPacket sendPack = new DatagramPacket(
                    outBytes, outBytes.length, receivePack.getAddress(), receivePack.getPort());
            receiveSock.send(sendPack);
            System.out.println("server address: " + InetAddress.getLocalHost().getHostAddress());
        }
    }
}
