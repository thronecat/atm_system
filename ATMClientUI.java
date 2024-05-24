//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ATMClientUI extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JTextArea displayArea; // Add displayArea

    private Socket client;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;
    //public String serverName ="10.242.228.98";
    public ATMClientUI() {
        super("ATM Client");

        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setBounds(10, 30, 80, 25);
        add(usernameLabel);

        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setBounds(10, 80, 80, 25);
        add(passwordLabel);

        userIdField = new JTextField(20);
        userIdField.setBounds(80, 30, 180, 25);
        add(userIdField);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(80, 80, 180, 25);
        add(passwordField);

        loginButton = new JButton("登录");
        loginButton.setBounds(100, 130, 80, 25);
        add(loginButton);

        displayArea = new JTextArea(); // Initialize displayArea
        JScrollPane scrollPane = new JScrollPane(displayArea); // Add displayArea to a scroll pane
        scrollPane.setBounds(10, 180, 260, 80); // Set bounds for the scroll pane
        add(scrollPane); // Add the scroll pane to the frame

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String serverName = "192.168.0.146";//10.242.228.98
                int port = 2525;

                try {
                    client = new Socket(serverName, port);
                    outToServer = new DataOutputStream(client.getOutputStream());
                    inFromServer = new DataInputStream(client.getInputStream());

                    // 发送HELO消息
                    outToServer.writeUTF("HELO "+ userIdField.getText());
                    // 接收来自服务器的消息
                    String serverResponse = inFromServer.readUTF();
                    displayArea.append("Server says: " + serverResponse + "\n");
                    //System.out.println("Server says: " + serverResponse + "\n");
                    // If server requests for password
                    if (serverResponse.startsWith("500 AUTH REQUIRE")) {
                        // Retrieve password from the password field
                        char[] passwordChars = passwordField.getPassword();
                        String password = new String(passwordChars);

                        // Send the password to the server
                        outToServer.writeUTF("PASS " + password);

                        // Receive response from the server
                        serverResponse = inFromServer.readUTF();
                        displayArea.append("Server says: " + serverResponse + "\n");

                        // Other interaction logic...
                    }
                    if (serverResponse.startsWith("525 OK!")) {
                        displayArea.append("Server says: " + serverResponse + "\n");
                        Mainpage mainpage = new Mainpage();
                        mainpage.setVisible(true);
                        dispose(); // 关闭原窗口
                        // New page for withdrawal operation
                        // Add your logic to create a new window or perform other actions
                    } else if (serverResponse.startsWith("401 ERROR!")) {
                        // Show password error popup and clear input fields
                        JOptionPane.showMessageDialog(null, "密码错误", "错误", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText(""); // Clear the password field
                        client.close();
                    }

                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300); // Adjusted size to accommodate the displayArea
        setLocationRelativeTo(null);
        setLayout(null); // Use null layout
        setVisible(true);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            new ATMClientUI();
        });
    }
}
class Mainpage extends JFrame{
    private JButton balaButton;
    private JButton withdrawButton;
    private JButton exitButton;
    private JTextField userInputField;
    private Socket client;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;
    private JTextArea displayArea; // Add displayArea
    public Mainpage() {
        super("ATM");

        balaButton = new JButton("查询");
        balaButton.setBounds(90, 30, 100, 25);
        add(balaButton);

        withdrawButton = new JButton("取款");
        withdrawButton.setBounds(90, 70, 100, 25);
        add(withdrawButton);

        exitButton = new JButton("退出");
        exitButton.setBounds(90, 140, 100, 25);
        add(exitButton);

        userInputField = new JTextField();
        userInputField.setBounds(90, 100, 100, 25);
        add(userInputField);

        displayArea = new JTextArea(); // Initialize displayArea
        JScrollPane scrollPane = new JScrollPane(displayArea); // Add displayArea to a scroll pane
        scrollPane.setBounds(10, 180, 260, 80); // Set bounds for the scroll pane
        add(scrollPane); // Add the scroll pane to the frame


        balaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String serverName = "192.168.0.146";
                int port = 2525;

                try {
                    client = new Socket(serverName, port);
                    outToServer = new DataOutputStream(client.getOutputStream());
                    inFromServer = new DataInputStream(client.getInputStream());

                    // 发送BALA消息
                    outToServer.writeUTF("BALA");

                    // 接收来自服务器的消息
                    String serverResponse = inFromServer.readUTF();
                    displayArea.append("Server says: " + serverResponse + "\n");
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String serverName = "192.168.0.146";
                int port = 2525;

                try {
                    client = new Socket(serverName, port);
                    outToServer = new DataOutputStream(client.getOutputStream());
                    inFromServer = new DataInputStream(client.getInputStream());
                    int amount = Integer.parseInt(userInputField.getText());
                    // 发送BALA消息
                    outToServer.writeUTF("WDRA "+amount);

                    // 接收来自服务器的消息
                    String serverResponse = inFromServer.readUTF();
                    if (serverResponse.startsWith("525 OK")) {
                        JOptionPane.showMessageDialog(null, "取款成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    } else if (serverResponse.startsWith("401 ERROR")) {
                        JOptionPane.showMessageDialog(null, "余额不足，取款失败", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String serverName = "192.168.0.146";
                int port = 2525;

                try {
                    client = new Socket(serverName, port);
                    outToServer = new DataOutputStream(client.getOutputStream());
                    inFromServer = new DataInputStream(client.getInputStream());

                    // 发送BALA消息
                    outToServer.writeUTF("BYE");

                    // 接收来自服务器的消息
                    String serverResponse = inFromServer.readUTF();
                    if (serverResponse.startsWith("BYE")) {
                        displayArea.append("Server says: " + serverResponse + "\n");
                        client.close();
                        System.exit(0);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
    }
}
