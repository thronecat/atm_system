//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.*;
import java.net.*;
import java.sql.*;

public class AtmServer {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/atmzhanghu"; // 数据库连接URL
    private static final String USER = "root"; // 数据库用户名
    private static final String PASS = "123456"; // 数据库密码


    public static void main(String[] args) {

        String userid = " ";



        LOGATM log = new LOGATM();//日志记录

        int port = 2525;




        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ATM Server is running...");

            while (true) {
                System.out.println("Client connected: " );
                Socket server = serverSocket.accept();
                System.out.println("Client connected: " + server.getRemoteSocketAddress());

                DataInputStream inFromClient = new DataInputStream(server.getInputStream());
                DataOutputStream outToClient = new DataOutputStream(server.getOutputStream());

                String clientRequest;
                while ((clientRequest = inFromClient.readUTF()) != null) {
                    System.out.println("Client says: " + clientRequest);

                    if (clientRequest.startsWith("HELO ")) {

                        try {
                           handleHeloRequest(inFromClient, outToClient, DB_URL, USER, PASS,log);
                            userid = clientRequest.substring(5);//储存id

                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else if (clientRequest.startsWith("PASS ")) {
                        try{
                            handlePassRequest(inFromClient, outToClient, DB_URL, USER, PASS,userid,log);


                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else if (clientRequest.equalsIgnoreCase("BALA")) {
                        try {
                            handleBalaRequest(inFromClient, outToClient, DB_URL, USER, PASS,userid,log);
                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else if (clientRequest.startsWith("WDRA ")) {
                        try {
                            handleWdraRequest(inFromClient, outToClient, DB_URL, USER, PASS, userid,log);
                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else if (clientRequest.equalsIgnoreCase("BYE")) {
                        handleByeRequest(outToClient,log);
                    }else{
                        outToClient.writeUTF("401 ERROR");
                        outToClient.flush();//未知报文
                    }

                    // Break the loop if the client sends BYE
                    if (clientRequest.equalsIgnoreCase("BYE")) {
                        break;
                    }
                }

                // Close the socket connection after handling all client requests
                log.close();
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void handleHeloRequest(DataInputStream inFromClient, DataOutputStream outToClient, String dbUrl, String dbUser, String dbPass,LOGATM log) throws IOException, SQLException {
        // 从客户端接收用户信息（假设用户信息已经以UTF格式发送）
        String fullUserInfo = inFromClient.readUTF();
        String userInfo = fullUserInfo.substring(5);
        System.out.println("Received user info from client: " + userInfo);
        // log.log("Received user info from client: " + userInfo);

        // 建立数据库连接
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);

            // 准备SQL查询语句
            String query = "SELECT * FROM zhanghu WHERE Userid = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userInfo);

            // 执行查询
            resultSet = preparedStatement.executeQuery();

            // 检查查询结果
            if (resultSet.next()) {
                // 发送用户信息回客户端

                outToClient.writeUTF("500 AUTH REQUIRED!");
                outToClient.flush();
                log.log("500 AUTH REQUIRED!");
            } else {
                // 如果用户不存在，发送错误信息
                System.out.println("Received user info from client:11111 ");
                outToClient.writeUTF("401 ERROR");
                outToClient.flush();
                System.out.println("Received user info from client: " + userInfo);
                log.log("401 ERROR");
            }
        } finally {
            // 关闭资源
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }
    }



    public static void handlePassRequest(DataInputStream inFromClient, DataOutputStream outToClient, String dbUrl, String dbUser, String dbPass,String userid,LOGATM log) throws IOException, SQLException {
        // 从客户端接收用户信息（假设用户信息已经以UTF格式发送）
        String fullUserInfo = inFromClient.readUTF();
        String password = fullUserInfo.substring(4);
        System.out.println("Received user info from client: " + userid);

        // 建立数据库连接
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 加载数据库驱动（MySQL为例）
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);

            // 准备SQL查询语句
            String query = "SELECT * FROM zhanghu WHERE Userid = ? and Password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userid);
            preparedStatement.setString(2, password);

            // 执行查询
            resultSet = preparedStatement.executeQuery();

            // 检查查询结果
            if (resultSet.next()) {
                // 发送用户信息回客户端
                System.out.println("密码正确");
                outToClient.writeUTF("525 OK!");
                outToClient.flush();
                log.log("525 OK!");
            } else {
                // 如果用户不存在，发送错误信息
                System.out.println("密码错误");
                outToClient.writeUTF("401 ERROR!");
                outToClient.flush();
                log.log("401 ERROR!");
            }
        } finally {
            // 关闭资源
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }
    }

    public static void handleBalaRequest(DataInputStream inFromClient, DataOutputStream outToClient, String dbUrl, String dbUser, String dbPass,String userid,LOGATM log ) throws IOException, SQLException{
        String fullUserInfo = inFromClient.readUTF();

        System.out.println("Received user info from client: " + userid);


        // 建立数据库连接
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            String query = "SELECT * FROM zhanghu WHERE Userid = ? and Password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userid);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // 发送用户信息回客户端
                String Bala = resultSet.getString("Balance");
                outToClient.writeUTF("AMTN:"+Bala);
                outToClient.flush();
                log.log("AMTN:"+Bala);
            } else {
                // 如果用户不存在，发送错误信息
                outToClient.writeUTF("401 ERROR!");
                outToClient.flush();
                log.log("401 ERROR!");
            }

        }finally {
            // 关闭资源
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }
    }

    public static void handleWdraRequest(DataInputStream inFromClient, DataOutputStream outToClient, String dbUrl, String dbUser, String dbPass,String userid,LOGATM log ) throws IOException, SQLException {
        // Extract the withdrawal amount from the message
        String withdrawalAmountStr = inFromClient.readUTF();
        String withdrawl = withdrawalAmountStr.substring(5);//取款金额
        double withdrawalAmount = Double.parseDouble(withdrawl);


        // 建立数据库连接
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            String query = "SELECT * FROM zhanghu WHERE Userid = ?" ;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userid);
            resultSet = preparedStatement.executeQuery();

            String balance = resultSet.getString("Balance");
            double doubleBalance = Double.parseDouble(balance);//余额
            // Process the withdrawal request...
            // For demonstration purposes, let's assume the withdrawal is successful
            if(doubleBalance>=withdrawalAmount) {
                double balance1 = doubleBalance - withdrawalAmount;

                query = "UPDATA zhanghu SET Balance = " + balance1 +"WHERE Userid = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, userid);//账户里扣除所取金额
                preparedStatement.executeUpdate();

                outToClient.writeUTF("525 OK");
                outToClient.flush();
                log.log("525 OK!");
            }else{
                outToClient.writeUTF("401 ERROR");
                outToClient.flush();
                log.log("401 ERROR!");
            }

        } finally {
            // 关闭资源
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }
    }

    public static void handleByeRequest(DataOutputStream outToClient,LOGATM log) throws IOException {
        outToClient.writeUTF("BYE");
        outToClient.flush();
        log.log("BYE");
    }
}