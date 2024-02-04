package Servercliet;



import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerGUI extends JFrame {
    public static int PORT = 6666;//设置服务器端口号
    private boolean started=false;//判断服务器启动状态
    private ServerSocket ss=null;//服务器的套接字
    private DataInputStream inFromClient = null; //从上面的TCP接口打开一个输入流
    private DataOutputStream outToClient = null;//从TCP接口打开一个输出流
    private Map<String, String> users = new HashMap<String, String>();//用于存放用户的帐号密码
    private List<Test> clients = new ArrayList<Test>();//用于存放用户的链表

    private Socket client;
    //定义构造器
    public ServerGUI() {
        this.setLayout(null);    //设置布局管理器
        JButton connectButton = new JButton("连接");
        JButton disconnectButton = new JButton("断开");
        JTextField portField = new JTextField();

        JTextArea userTextArea = new JTextArea();
        userTextArea.setLineWrap(true);
        JScrollPane userScrollPane = new JScrollPane(userTextArea);
        userScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        userScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        JTextArea messageTextArea = new JTextArea();
        messageTextArea.setLineWrap(true);
        JScrollPane messageScrollPane = new JScrollPane(messageTextArea);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        JScrollPane messageScrollPane = new JScrollPane(messageTextArea);

        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("发送");

        JLabel portLabel = new JLabel("端口:");
        portLabel.setBounds(20, 20, 40, 20);

        portField.setBounds(70, 20, 80, 20);

        this.add(portLabel);

        // 设置位置和大小
        portField.setBounds(50, 20, 100, 20);
        connectButton.setBounds(170, 20, 70, 20);
        disconnectButton.setBounds(250, 20, 70, 20);
        userTextArea.setBounds(20, 50, 100, 200);
        messageScrollPane.setBounds(130, 50, 350, 200);
        inputField.setBounds(20, 270, 380, 20);
        sendButton.setBounds(410, 270, 70, 20);

        connectButton.addActionListener(e->{
            new Thread(()->{
//                String portStr = portField.getText();
//                 PORT = Integer.parseInt(portStr);
                try{
                    ss=new ServerSocket(OutPut.PORT);//打开套接字
                    started =true;//设置启动标志
                    messageTextArea.append("服务器连接成功！\n");
                    while(started){//循环等待用户信息
                        // 根据Socket实例创建输入输出流
                        Socket s = ss.accept();//等待一个客户上线，并且读取登陆信息
                        //获取输入输出流
                        inFromClient = new DataInputStream(s.getInputStream());
                        outToClient = new DataOutputStream(s.getOutputStream());
                        String message = inFromClient.readUTF();
                        // 使用分隔符%将帐号和密码信息分离
                        // 获取帐号信息和密码信息
                        String[] userpassword = message.split("%");//分离帐号和密码信息

                        String username = userpassword[0].substring(OutPut.USERNAME_MARK.length());
                        String password = userpassword[1].substring(OutPut.PASSWORD_MARK.length());


                        String passwd = users.get(username);
                        userTextArea.append(username+"\n");
                        if (passwd != null && !password.equals(passwd)) {
                            outToClient.writeUTF("passwordfail");
                            s.close();
                        } else {
                            outToClient.writeUTF("成功登陆.....");
                            users.put(username, password);
                            Test c=new Test(s, inFromClient, outToClient, clients);
                            c.setName(username);
                            new Thread(c).start();
                            clients.add(c);
                        }
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();
        });
        disconnectButton.addActionListener(e -> {
            try {
                messageTextArea.append("服务器已经断开！"+"\n");
                System.exit(0);
                System.out.println("连接已断开"+"\n");
            } catch (Exception ex) {
                System.out.println("断开连接失败，原因是：" + ex.getMessage());
                ex.printStackTrace();
            }
        });
        sendButton.addActionListener(e->{
            String msg = inputField.getText();

            inputField.setText("");

//            messageTextArea.append(msg+"\n");

            try {
                DataOutputStream dos = new DataOutputStream(client.getOutputStream()); // 获取客户端的输出流
                dos.writeUTF(msg+"\n"); // 往流里写，发送到客户端
                dos.flush();
            } catch (IOException eh) {
                eh.printStackTrace();
            }

        });

        // 将组件添加到界面中
        this.add(portField);
        this.add(connectButton);
        this.add(disconnectButton);
        this.add(userTextArea);
        this.add(messageScrollPane);
        this.add(inputField);
        this.add(sendButton);

        // 设置窗口的大小和位置
        this.setBounds(200, 250, 500, 350);
        this.setVisible(true);
    }

    public static void main(String[] args) {
//        if (args.length != 0) {
//            OutPut.PORT = Integer.valueOf(args[0]);
//        }
//        new ServerCliet().start();
        new ServerGUI();
    }
}