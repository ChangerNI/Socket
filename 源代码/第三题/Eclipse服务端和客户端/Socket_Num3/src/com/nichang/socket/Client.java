package com.nichang.socket;
import java.awt.BorderLayout;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.awt.event.WindowAdapter;  
import java.awt.event.WindowEvent;  
import java.io.BufferedReader;  
import java.io.FileInputStream;  
import java.io.InputStreamReader;  
import java.net.DatagramPacket;  
import java.net.DatagramSocket;  
import java.net.InetSocketAddress;  
import java.net.SocketAddress;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
import java.util.Map.Entry;  
import java.util.Set;  
  
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  
import javax.swing.JScrollBar;  
import javax.swing.JTextArea;  
import javax.swing.JTextField;  
  
public class Client extends Thread implements ActionListener{  
    //�Ƿ�ֹͣ  
    public static int STOP=0;  
      
    //�����û��б�  
    public static Map<String,SocketAddress> userMap=new HashMap();  
      
    private DatagramSocket client;  
      
    private JFrame frame;  
    //������Ϣ  
    private JTextArea info;  
    //�����û�  
    private JTextArea onlineUser;  
    private JTextField msgText;  
      
    private JButton sendButton;  
      
    public Client(DatagramSocket client)throws Exception  
    {  
  
        this.client=client;  
        this.frame=new JFrame("201505010315�����153���߳���P2P�������1.0");  
        frame.setSize(800, 300);  
          
        sendButton=new JButton("����");  
        JScrollBar scroll=new JScrollBar();  
        this.info=new JTextArea(10,40);  
      //�����Զ����й���   
        info.setLineWrap(true);  
        info.setWrapStyleWord(true);  
        info.setEditable(false);  
        scroll.add(info);  
          
        onlineUser=new JTextArea(10,20);  
        onlineUser.setLineWrap(true);  
        onlineUser.setWrapStyleWord(true);  
        onlineUser.setEditable(false);  
          
          
        JPanel infopanel=new JPanel();  
        infopanel.add(info,BorderLayout.WEST);  
        JPanel infopanel1=new JPanel();  
        JLabel label=new JLabel("��ǰ�����û�:");  
        infopanel1.add(label, BorderLayout.NORTH);  
        infopanel1.add(onlineUser, BorderLayout.SOUTH);  
        infopanel.add(infopanel1,BorderLayout.EAST);  
  
        JPanel panel=new JPanel();  
          
        msgText=new JTextField(30);  
      
        panel.add(msgText);  
        panel.add(sendButton);  
        frame.add(infopanel,BorderLayout.NORTH);  
        frame.add(panel,BorderLayout.SOUTH);  
        frame.setVisible(true);  
          
        sendButton.addActionListener(this);  
          
        frame.addWindowListener(new   WindowAdapter(){   
            public   void   windowClosing(WindowEvent   e){   
                System.exit(0);  
            }   
         });   
          
  
    }  
      
    /** 
     * �����������û��������� ����session��Ч 
     */  
    private void sendSkip()  
    {  
        new Thread(){  
            public void run()  
            {  
                try  
                {  
                    String msg="skip";  
                    while(true)  
                    {  
                        if(STOP==1)  
                            break;  
                        if(userMap.size()>0)  
                        {  
                             for (Entry<String, SocketAddress> entry : userMap.entrySet()) {  
                                 DatagramPacket data=new DatagramPacket(msg.getBytes(),msg.getBytes().length,entry.getValue());  
                                client.send(data);  
                            }  
                        }  
                        //ÿ10s����һ������  
                        Thread.sleep(10*1000);  
                    }  
                }catch(Exception e){}  
                  
            }  
        }.start();  
    }  
      
    //��Ҫ�����ǽ�������  
    //�����������û���������Ϣ��Ҳ�����Ƿ����������������û�����  
    public void run()  
    {  
        try  
        {  
              
            String msg;  
            DatagramPacket data;  
              
            //ִ������  
            sendSkip();  
              
            while(true)  
            {  
                if(STOP==1)  
                    break;  
                byte[] buf=new byte[1024];  
                DatagramPacket packet = new DatagramPacket(buf, buf.length);  
                client.receive(packet);  
                msg=new String(packet.getData(),0,packet.getLength());  
                if(msg.length()>0)  
                {  
                    if(msg.indexOf("server:")>-1)  
                    {  
                        //���������� ��ʽserver:ID#IP��PORT������  
                        String userdata=msg.substring(msg.indexOf(":")+1,msg.length());  
                        String[] user=userdata.split(",");  
                        for(String u:user)  
                        {  
                            if(u!=null&&u.length()>0)  
                            {  
                                String[] udata=u.split("#");  
                                String ip=udata[1].split(":")[0];  
                                int port=Integer.parseInt(udata[1].split(":")[1]);  
                                  
                                ip=ip.substring(1,ip.length());  
                                  
                                SocketAddress adds=new InetSocketAddress(ip,port);  
                                userMap.put(udata[0], adds);  
                                //���Է��� ���Ϳհױ���  
                                data=new DatagramPacket(new byte[0],0,adds);  
                                client.send(data);  
                                  
                            }  
                              
                        }  
                        //���������û��б�  
                        this.onlineUser.setText("");  
                        for (Map.Entry<String, SocketAddress> entry : userMap.entrySet()) {  
                            this.onlineUser.append("�û�"+entry.getKey()+"("+entry.getValue()+")\n");  
                        }  
  
                    }  
                    else if(msg.indexOf("skip")>-1);  
                    else  
                    {  
                        //��ͨ��Ϣ  
                        this.info.append(packet.getAddress().toString()+packet.getPort()+" ˵��"+msg);  
                        this.info.append("\n");  
                    }  
                }  
            }  
        }  
        catch(Exception e){}  
    }  
      
    public static void main(String args[])throws Exception  
    {  
          
        String serverIP="127.0.0.1";///122.225.99.40  
        int port=20000;  
          
        //����һ��Ŀ���ַ  
        SocketAddress target = new InetSocketAddress(serverIP, port);   
          
        DatagramSocket client = new DatagramSocket();  
        String msg="����������棡";  
        byte[] buf=msg.getBytes();  
        //�������������������  
        DatagramPacket packet=new DatagramPacket(buf,buf.length,target);  
        client.send(packet);  
        new Client(client).start();  
          
    }  
  
      
    //��ť�¼�  
    @Override  
    public void actionPerformed(ActionEvent e) {  
        if(e.getSource()==this.sendButton)  
        {  
            try{  
                String msg=this.msgText.getText();  
                if(msg.length()>0)  
                {  
                    this.info.append("��˵��"+msg);  
                    this.info.append("\n");  
                    for (Map.Entry<String, SocketAddress> entry : userMap.entrySet()) {  
                        DatagramPacket data=new DatagramPacket(msg.getBytes(),msg.getBytes().length,entry.getValue());  
                        client.send(data);  
                    }  
                      
                    this.msgText.setText("");  
                }  
            }  
            catch(Exception ee){}  
        }  
          
    }  
}  