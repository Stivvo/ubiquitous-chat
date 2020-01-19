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

class SendThread extends Thread {
    private static List<Socket> clients = new ArrayList<>();
    private static ServerSocket sendServerSocket;

    static {
        try {
            sendServerSocket = new ServerSocket(6790);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String msg;

    public SendThread(String msg) {
        this.msg = msg;
    }

    public static void addClient() throws IOException {
        clients.add(sendServerSocket.accept());
    }

    public static void close() throws IOException {
        for (Socket i : clients) {
            try {
                i.close();
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
        clients.clear();
        sendServerSocket.close();
    }

    public void run() {
        for (Socket i : clients) {
            try {
                DataOutputStream toClient = new DataOutputStream(
                        i.getOutputStream());
                toClient.writeBytes(msg + "\n");
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    i.close();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                break;
            }
        }
    }
}

class ServerThread extends Thread {
    private Socket tcpSock;

    private static List<String> clients = new ArrayList<>();
    private static List<Socket> clientsSocket = new ArrayList<>();
    private static Semaphore sendMaphore = new Semaphore(1);
    private static ServerSocket receiveServerSock;

    static {
        try {
            receiveServerSock = new ServerSocket(6789);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerThread() {
        try {
            this.tcpSock = receiveServerSock.accept();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void close() throws IOException {
        for (Socket i : clientsSocket)
            i.close();
        receiveServerSock.close();
    }

    public void doStuff() throws Exception {
        System.out.println("doing stuff");
        DataOutputStream toClient = new DataOutputStream(
                tcpSock.getOutputStream());
        BufferedReader fromClient = new BufferedReader(
                new InputStreamReader(tcpSock.getInputStream()));
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
                clientsSocket.add(tcpSock);
                SendThread.addClient();
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
                    SendThread sendThread = new SendThread(strReceive);
                    sendThread.start();
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
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(400);
                    SendThread.close();
                    ServerThread.close();
                    System.out.println("All socket are closed");
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });
        while (true) {
            System.out.println("main while");
            ServerThread serverThread = new ServerThread();
            serverThread.start();
        }
    }
}
