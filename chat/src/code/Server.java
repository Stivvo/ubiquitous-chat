package code;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;

class ServerThread extends Thread {
    public static MulticastSocket multicastSock;
    public Socket tcpSock;

    public static Semaphore sendMaphore = new Semaphore(0);
    public static ArrayList<Pair<Socket, String>> clients = new ArrayList<Pair<Socket, String>>();

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
        String strReceive = fromClient.readLine();
        String strSend;

        if (strReceive.contains("@"))
            strSend = "ko"; // user conencted to a closed server
        else {
            strSend = "yes";
            //sendMaphore.acquire();
            for (Pair<Socket, String> i : clients) { // search user and port in clients
                if (i.getValue().equals(strReceive)){
                    strSend = "no"; // busy username
                }
            }
            if (strSend.equals("yes")) // fre username, add to list
                clients.add(new Pair<>(tcpSock, strReceive));
            else
                tcpSock.close();

            //sendMaphore.release();
            System.out.println("new client: port = " + tcpSock.getPort() + 
                    ", name: " + strReceive + ", allow: " + strSend);
            toClient.writeBytes(strSend + "\n");
        }

        while (true) { // an already connected cliend sending messages
            System.out.println("in while");
            strReceive = fromClient.readLine();
            if (strReceive.equals("end")) {
                tcpSock.close(); // client wants to be disconnected
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
