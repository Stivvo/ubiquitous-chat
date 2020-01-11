package code;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class ServerThread extends Thread {
    public static MulticastSocket multicastSock;
    public Socket tcpSock;

    public static Semaphore sendMaphore = new Semaphore(0);
    public static List<String> clients = new ArrayList<>();

    public ServerThread(Socket socket) {
        try {
            this.tcpSock = socket;
            this.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void doStuff() throws Exception {
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
        DataOutputStream toClient = new DataOutputStream(tcpSock.getOutputStream());
        BufferedReader kybrd = new BufferedReader(new InputStreamReader(System.in));

        // before while, new client
        String name = fromClient.readLine();
        String strSend;

        if (name.contains("@"))
            strSend = "ko"; // user connected to a closed server
        else {
            strSend = "yes";
            for (String i : clients) {
                System.out.println("client: " + i + "\n");
            }
            //sendMaphore.acquire();
            for (String i : clients) { // search user and port in clients
                if (i.equals(name)) {
                    strSend = "no"; // busy username
                }
            }
            System.out.println("new client: port = " + tcpSock.getPort() +
                    ", name: " + name + ", allow: " + strSend);
            toClient.writeBytes(strSend + "\n");
            if (strSend.equals("yes")) // free username, add to list
                clients.add(name);
            else
                tcpSock.close();
            //sendMaphore.release();
        }

        while (true) { // an already connected cliend sending messages
            System.out.println("in while");
            String strReceive = fromClient.readLine();
            if (strReceive.equals("end")) {
                tcpSock.close(); // client wants to be disconnected
                System.out.println(name + " wants to be disconnected");
                clients.remove(name);
                break;
            }

            System.out.println(strReceive + "\nallow? [Y/n] ");
            if (!kybrd.readLine().equals("n")) { // allow the message?
                byte[] outBytes = strReceive.getBytes();
                DatagramPacket sendPack = new DatagramPacket(
                        outBytes, outBytes.length, InetAddress.getByName("225.4.5.6"), 6786);
                multicastSock.send(sendPack);
            } else
                System.out.println("not allowed");
            strSend = "yes";
            toClient.writeBytes(strSend + "\n"); // confirm to Client
            // remind server address
            System.out.println("server address: " + InetAddress.getLocalHost().getHostAddress());
        }
    }

    public void run() {
        try {
            doStuff();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Server {
    public static void main(String[] args) throws Exception {
        ServerThread.multicastSock = new MulticastSocket();
        ServerThread.multicastSock.joinGroup(InetAddress.getByName("225.4.5.6"));
        //ServerThread.sendMaphore.release();
        ServerSocket receiveServerSock = new ServerSocket(6789);

        while (true) {
            System.out.println("main while");
            Socket socket = receiveServerSock.accept();
            ServerThread serverThread = new ServerThread(socket);
        }
    }
}
