package Persion;

import java.awt.TextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;

public class Liever extends KeyAdapter {

	private TextArea tfTxt;
	private DataOutputStream dos;
	
	public Liever(TextArea tfTxt, DataOutputStream dos) {
		this.tfTxt = tfTxt;
		this.dos = dos;
	}
	/**
	 * 获取文本内容并发送服务器
	 * */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER ) {
			String str = tfTxt.getText().trim();//获取输入框的内容
			tfTxt.setText("");
			try {
				dos.writeUTF(str);//往流里写，发到服务器
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
