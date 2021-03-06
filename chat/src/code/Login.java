package code;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login {
    private JPanel panel1;
    private JTextField usernameField;
    private JButton submitButton;
    private JLabel usernameLabel;
    private JLabel serverAddressLabel;
    private JTextField serverAddressField;

    public Login() throws Exception {
        submitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    String[] args = {usernameField.getText(), serverAddressField.getText()};
                    Client.main(args);
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
        panel1.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setMaximumSize(new Dimension(600, 450));
        panel1.setMinimumSize(new Dimension(-1, -1));
        panel1.setPreferredSize(new Dimension(300, 150));
        submitButton = new JButton();
        submitButton.setMargin(new Insets(10, 10, 10, 10));
        submitButton.setText("Submit");
        panel1.add(submitButton, new GridConstraints(2, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        usernameLabel = new JLabel();
        usernameLabel.setHorizontalTextPosition(0);
        usernameLabel.setText("Username:");
        panel1.add(usernameLabel, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serverAddressLabel = new JLabel();
        serverAddressLabel.setHorizontalTextPosition(0);
        serverAddressLabel.setText("Server address:");
        panel1.add(serverAddressLabel, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        usernameField = new JTextField();
        usernameField.setHorizontalAlignment(0);
        usernameField.setMargin(new Insets(2, 6, 2, 6));
        panel1.add(usernameField, new GridConstraints(0, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), new Dimension(400, 50), 0, false));
        serverAddressField = new JTextField();
        serverAddressField.setHorizontalAlignment(0);
        serverAddressField.setMargin(new Insets(2, 6, 2, 6));
        panel1.add(serverAddressField, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), new Dimension(400, 50), 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 5, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, 1, null, new Dimension(10, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, 1, null, new Dimension(10, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
