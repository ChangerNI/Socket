package com.nichang.socket;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame frame1 = new JFrame();
        frame1.setBounds (100, 100, 500, 100);
        frame1.setTitle ("教师上课管理系统By201505010315计算机153班倪畅");
        frame1.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        JButton button1 = new JButton("教师端屏幕分享");
       // button1.setBounds(17, 50, 20, 50);
        JButton button2 = new JButton("教师端文件发送");
        //button2.setBounds(17, 50, 20, 50);
        //frame1.add (button1);
        JLabel label=new JLabel("教师端文件传送为默认文件：“C:\\Users\\nicha\\workspace\\Socket_Num1\\test.txt”。");  
        
        JPanel panel=new JPanel();  
        panel.add(label, BorderLayout.NORTH);
        frame1.add (panel);
        button1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				button1.setVisible(true);
				 ReceiveImages receiveImages = new ReceiveImages(new BorderInit(), "127.0.0.1");
			        receiveImages.start();
			}
		});
        button2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int length = 0;
		        byte[] sendByte = null;
		        Socket socket = null;
		        DataOutputStream dout = null;
		        FileInputStream fin = null;
		        try {
		          try {
		            socket = new Socket();
		            socket.connect(new InetSocketAddress("127.0.0.1", 33456),10 * 1000);
		            dout = new DataOutputStream(socket.getOutputStream());
		            File file = new File("C:\\Users\\nicha\\workspace\\Socket_Num1\\test.txt");
		            fin = new FileInputStream(file);
		            sendByte = new byte[1024];
		            dout.writeUTF(file.getName());
		            while((length = fin.read(sendByte, 0, sendByte.length))>0){
		                dout.write(sendByte,0,length);
		                dout.flush();
		            }
		            } catch (Exception ee) {

		            } finally{
		                if (dout != null)
		                    dout.close();
		                if (fin != null)
		                    fin.close();
		                if (socket != null)
		                    socket.close();
		        }
		        } catch (Exception ee) {
		            ee.printStackTrace();
		        }
			}
		});
        panel.add(button1);
        panel.add(button2);
        //panel.setLayout(null);
        frame1.setVisible (true);
	}

}
