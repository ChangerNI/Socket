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
 * �������ڽ��ս�ʦ�˵���Ļ��Ϣ����������꣬���Ż�
 */
public  class ReceiveImages extends  Thread{
    public BorderInit frame ;
    public Socket socket;
    public String IP;
//     
//    public static void main(String[] args){
//    	JFrame frame1 = new JFrame();
//        frame1.setBounds (100, 100, 500, 400);
//        frame1.setTitle ("��ʦ�Ͽι���ϵͳBy201505010315�����153���߳�");
//        frame1.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
//        JButton button1 = new JButton("��ʦ����Ļ����");
//        button1.setBounds(20, 50, 20, 50);
//        JButton button2 = new JButton("��ʦ���ļ�����");
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
                 
                imgZip.getNextEntry();             //��Zip�ļ����Ŀ�ʼ��
                Image img = ImageIO.read(imgZip);  //�����ֽڶ�ȡZipͼƬ�������ͼƬ
                frame.jlbImg.setIcon(new ImageIcon(img));
                System.out.println("���ӵ�"+(System.currentTimeMillis()/1000)%24%60+"��");
                frame.validate();
                TimeUnit.MILLISECONDS.sleep(10);// ����ͼƬ���ʱ��
                imgZip.close();
                 
            } catch (IOException | InterruptedException e) {
                System.out.println("���ӶϿ�");
            }finally{
                try {
                    socket.close();
                } catch (IOException e) {}  
            }       
        }   
    }
}
 
//Client�˴��ڸ����࣬ר��������ʾ�ӽ�ʦ���յ�����Ļ��Ϣ
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
        this.setTitle("��ʦ����Ļ����" );
        this.setSize(800, 600);
        //this.setUndecorated(true);  //ȫ����ʾ������ʱ���ע�͵�
        //this.setAlwaysOnTop(true);  //��ʾ����ʼ������ǰ��
        this.add(jlbImg);
        this.setLocationRelativeTo(null);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.validate();
     
        //���ڹر��¼�
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                flag=false;
                BorderInit.this.dispose();
                System.out.println("����ر�");
                System.gc();    //��������
            }
        });
    }
}