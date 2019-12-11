package code;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Login {
    private JPanel panel1;
    private JTextField textField1;
    private JButton submitButton;

    private void submit(String username) throws Exception {
        String[] args = {username};
        Client.main(args);
    }

    public Login() throws Exception {
        submitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    submit(textField1.getText());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("login");
        Login login = new Login();
        frame.setContentPane(login.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
