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
    public Socket tcpSock;

    public static ServerSocket receiveServerSock;
    public static MulticastSocket multicastSock;
    public static Semaphore sendMaphore = new Semaphore(0);
    public static List<String> clients = new ArrayList<>();

    public ServerThread(Socket socket) {
        try {
            this.tcpSock = socket;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void doStuff() throws Exception {
        System.out.println("doing stuff");
        BufferedReader fromClient = new BufferedReader(
                new InputStreamReader(tcpSock.getInputStream()));
        DataOutputStream toClient = new DataOutputStream(
                tcpSock.getOutputStream());
        BufferedReader kybrd = new BufferedReader(
                new InputStreamReader(System.in));

        // before while, new client
        String name = fromClient.readLine();
        String strSend;

        if (name.contains("@"))
            strSend = "ko"; // user connected to a closed server
        else {
            strSend = "yes";
            sendMaphore.acquire();
            for (String i : clients) { // search user and port in clients
                if (i.equals(name)) {
                    strSend = "no"; // busy username
                }
            }
            System.out.println("new client: port = " + tcpSock.getPort() +
                    ", name: " + name + ", allow: " + strSend);
            toClient.writeBytes(strSend + "\n");
            if (strSend.equals("yes")) { // free username, add to list
                clients.add(name);
                sendMaphore.release();
            } else {
                sendMaphore.release();
                tcpSock.close();
                System.out.println(name + " already used");
                return;
            }
        }
        String strReceive;

        try {
            while (true) { // an already connected cliend sending messages
                strReceive = fromClient.readLine();
                if (strReceive.equals("end")) {
                    tcpSock.close();
                    System.out.println(name + " wants to leave the chat");
                    sendMaphore.acquire();
                    clients.remove(name);
                    sendMaphore.release();
                    break;
                }

                System.out.println(strReceive + "\nallow? [Y/n] ");
                if (!kybrd.readLine().equals("n")) { // allow the message?
                    byte[] outBytes = strReceive.getBytes();
                    DatagramPacket sendPack = new DatagramPacket(
                            outBytes, outBytes.length, 
                            InetAddress.getByName("225.4.5.6"), 6786);
                    multicastSock.send(sendPack);
                } else
                    System.out.println("not allowed");
                // remind server address
                System.out.println("server address: " +
                        InetAddress.getLocalHost().getHostAddress());
            }
        } catch (NullPointerException | IOException e) {
            sendMaphore.acquire();
            clients.remove(name);
            sendMaphore.release();
            tcpSock.close();
            System.out.println(name + " left the chat");
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
        ServerThread.multicastSock.joinGroup(
                InetAddress.getByName("225.4.5.6"));
        ServerThread.receiveServerSock = new ServerSocket(6789);
        ServerThread.sendMaphore.release();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(400);
                    ServerThread.multicastSock.close();
                    System.out.println("All socket are closed");
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });
        while (true) {
            System.out.println("main while");
            Socket socket = ServerThread.receiveServerSock.accept();
            ServerThread serverThread = new ServerThread(socket);
            serverThread.start();
        }
    }
}
