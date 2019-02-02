package com.nichang.socket;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
/*
 * 该类用于接收教师端的屏幕信息，不包括鼠标，待优化
 */
public  class ReceiveImages extends  Thread{
    public BorderInit frame ;
    public Socket socket;
    public String IP;
//     
//    public static void main(String[] args){
//    	JFrame frame1 = new JFrame();
//        frame1.setBounds (100, 100, 500, 400);
//        frame1.setTitle ("教师上课管理系统By201505010315计算机153班倪畅");
//        frame1.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
//        JButton button1 = new JButton("教师端屏幕分享");
//        button1.setBounds(20, 50, 20, 50);
//        JButton button2 = new JButton("教师端文件发送");
//        button2.setBounds(20, 50, 20, 50);
//        //frame1.add (button1);
//        JPanel panel=new JPanel();  
//        frame1.add (panel);
//        button1.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				// TODO Auto-generated method stub
//				button1.setVisible(true);
//				 ReceiveImages receiveImages = new ReceiveImages(new BorderInit(), "127.0.0.1");
//			        receiveImages.start();
//			}
//		});
//        panel.add(button1);
//       // panel.setLayout (null);
//        frame1.setVisible (true);
//       
//    }
    public ReceiveImages(BorderInit frame,String IP)
    {
        this.frame = frame;
        this.IP=IP;
    }
     
    public void run() {
        while(frame.getFlag()){
            try {
                socket = new Socket(IP,8000);
                DataInputStream ImgInput = new DataInputStream(socket.getInputStream());
                ZipInputStream imgZip = new ZipInputStream(ImgInput);
                 
                imgZip.getNextEntry();             //到Zip文件流的开始处
                Image img = ImageIO.read(imgZip);  //按照字节读取Zip图片流里面的图片
                frame.jlbImg.setIcon(new ImageIcon(img));
                System.out.println("连接第"+(System.currentTimeMillis()/1000)%24%60+"秒");
                frame.validate();
                TimeUnit.MILLISECONDS.sleep(10);// 接收图片间隔时间
                imgZip.close();
                 
            } catch (IOException | InterruptedException e) {
                System.out.println("连接断开");
            }finally{
                try {
                    socket.close();
                } catch (IOException e) {}  
            }       
        }   
    }
}
 
//Client端窗口辅助类，专门用来显示从教师端收到的屏幕信息
class BorderInit extends JFrame
{
    private static final long serialVersionUID = 1L;
    public JLabel jlbImg;
    private boolean flag;
    public boolean getFlag(){
        return this.flag;
    }
    public BorderInit()
    {
        this.flag=true;
        this.jlbImg = new JLabel();
        this.setTitle("教师端屏幕分享" );
        this.setSize(800, 600);
        //this.setUndecorated(true);  //全屏显示，测试时最好注释掉
        //this.setAlwaysOnTop(true);  //显示窗口始终在最前面
        this.add(jlbImg);
        this.setLocationRelativeTo(null);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.validate();
     
        //窗口关闭事件
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                flag=false;
                BorderInit.this.dispose();
                System.out.println("窗体关闭");
                System.gc();    //垃圾回收
            }
        });
    }
}