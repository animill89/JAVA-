package Persion;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Servercliet.OutPut;

public class LoginOK extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	//登陆窗口的组建
	private JLabel UserName = new JLabel("登陆帐号：");
	private JTextField  Name = new JTextField();
	private JLabel UserPaw = new JLabel ("登陆密码：");
	private JPasswordField Paw = new JPasswordField();
	private JLabel ServerHost = new JLabel("服务器地址：");
	private JTextField Host = new JTextField();
	private JLabel ServerPort = new JLabel("服务器端口：");
	private JTextField Port = new JTextField();
	private JButton Load = new JButton("登陆");
	private JButton Quit = new JButton("退出");
	
	//连接参数
	private Socket s=null;
	private DataOutputStream dos=null;
	private DataInputStream dis=null;
	private boolean bconnected=false;
	
	private String message;
	private String host;
	private int port;

	/**
	 * 登陆窗口，启动的入口
	 */
	public void loadFrame() {
		this.setTitle("聊天室登陆窗口");
		Container c = this.getContentPane();
		this.setLayout(null);
		this.setBounds(400, 300, 350, 350);
		getContentPane().setBackground(Color.GREEN);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		this.setVisible(true);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		UserName.setBounds(50, 40, 100, 20);
		c.add(UserName);
		Name.setBounds(150, 40, 120, 20);
		c.add(Name);

		UserPaw.setBounds(50, 90, 100, 20);
		c.add(UserPaw);
		Paw.setBounds(150, 90, 120, 20);
		c.add(Paw);
		
		ServerHost.setBounds(50, 140, 100, 20);
		c.add(ServerHost);
		Host.setBounds(150, 140, 120, 20);
		c.add(Host);
		
		ServerPort.setBounds(50, 190, 100, 20);
		c.add(ServerPort);
		Port.setBounds(150, 190, 120, 20);
		c.add(Port);
		
		Load.setBounds(50,250,80,40);
		c.add(Load);
		Quit.setBounds(190,250,80,40);
		c.add(Quit);
		Load.addActionListener(this);
		Quit.addActionListener(this);
	}

	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Load) {// 如果事件源是Load按钮
			String name = Name.getText();
			name.trim();
			String passwd = new String(Paw.getPassword());
			passwd.trim();

			// 判断用户名和密码是否为空

			if (name.length() == 0 || passwd.length() == 0) {
				this.showReturnMessage("帐号密码不能为空，请重新输入....");
			} else {
				// 否则，拼接消息字符串

				message = "username:" + name + "%" + "password:" + passwd;
				try {
					host = Host.getText();
//					System.out.println(host);
					port = Integer.valueOf(Port.getText());

					boolean connect = this.connect(host, port);//与服务器进行连接
					if (connect) {
						this.setVisible(false);//隐藏登陆窗口
						OpenTest mc = new OpenTest(dis, dos);
						mc.launchFrame();//打开聊天主窗口
						new Thread(mc).start();//启动线程，获取聊天内容和在线用户列表
					}
				} catch (NumberFormatException e1) {
					this.showReturnMessage("输入非法端口，请重新输入1024-65535之间的任意一个端口....");
				}
			}
 		} else if (e.getSource() == Quit) {
//			System.out.println(Quit+","+e.getSource());
			System.exit(0);
		}
	}
	
	/**
	 *与服务器进行连接
	 */
	public boolean connect(String host, int port){

		try {
			if (host == null || host.equals("")) {
				host = "119.86.239.186";
			}


			s=new Socket(host, port);// 创建一个与服务器端建立连接的Socket对象
			dos=new DataOutputStream(s.getOutputStream());// 获取与服务器端关联的输出流
			dis=new DataInputStream(s.getInputStream());// 获取与服务器端关联的输入流
			System.out.println("连接成功");// 连接成功提示
			dos.writeUTF(message);//发送帐号密码信息
			String conmsg = dis.readUTF();


			if (conmsg.equals("passwordfail")) {
				this.showReturnMessage("登陆失败，你的密码错误，请重新登陆.....");
				bconnected = false;
			} else {
			System.out.println("成功登陆.....");
				bconnected = true;
			}


		} catch(IllegalArgumentException e) {
			this.showReturnMessage("输入非法端口，请重新输入1024-65535之间的任意一个端口....");
		} catch (UnknownHostException e) {
			this.showReturnMessage("未知服务器地址，请重新填写后再次登陆.....");
		} catch(SocketException e) {
			this.showReturnMessage("端口输入错误，请重新输入....");
		} catch (IOException e) {
			this.showReturnMessage("未知IO错误，请重新再试试，不行马上与管理员联系！");
			System.exit(0);
		}
		return bconnected;
	}
	
	/**
	 *  显示反馈信息
	 */
	public void showReturnMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "错误提示", JOptionPane.ERROR_MESSAGE);
	}

	}