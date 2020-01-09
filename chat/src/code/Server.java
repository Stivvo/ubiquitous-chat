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
    public static Semaphore sendMaphore = new Semaphore(0);
    public static MulticastSocket sendSock;

    static {
        try {
            sendSock = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Pair<Socket, String>> clients = new ArrayList<Pair<Socket, String>>();
    public Socket receiveSock;

    public ServerThread(Socket socket) {
        try {
            this.receiveSock = socket;
            start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void doStuff() throws Exception {
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(receiveSock.getInputStream()));
        DataOutputStream toClient = new DataOutputStream(receiveSock.getOutputStream());
        BufferedReader kybrd = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String strReceive = fromClient.readLine();
            if (strReceive.equals("end")) // client disconnected
                break;

            sendMaphore.acquire();
            boolean foundPort = false, foundName = false;
            for (Pair<Socket, String> i : clients) { // search user and port in clients
                if (i.getKey().getPort() == receiveSock.getPort())
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
                    clients.add(new Pair<>(receiveSock, strReceive));
                }
                System.out.println("new client: port = " + receiveSock.getPort() + ", name: " + strReceive +
                        "already used username = " + foundName + ", allow: " + strSend);
                sendMaphore.release();
            } else { // an already connected cliend sent a message
                sendMaphore.release();
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
        ServerThread.sendSock.joinGroup(InetAddress.getByName("225.4.5.6"));
        ServerThread.sendMaphore.release();
        ServerSocket receiveServerSock = new ServerSocket(6789);

        while (true) {
            Socket socket = receiveServerSock.accept();
            ServerThread serverThread = new ServerThread(socket);
        }
    }
}
