package com.nichang.socket;

import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.io.PrintWriter;  
import java.net.DatagramPacket;  
import java.net.DatagramSocket;  
import java.net.InetAddress;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  

public class Server extends Thread{  
    //存储所有的用户IP与端口  
    public static List<Map> userList=new ArrayList<Map>();  
    //在线用户IP  
    public static Map users=new HashMap();  
      
    public static int index=1;  
      
    private DatagramSocket server;  
      
    public Server(DatagramSocket server)  
    {  
        this.server=server;  
    }  
    //线程负责给在线用户发送当前所有在线用户的信息  
    public void run()  
    {  
        try  
        {  
            DatagramPacket sendPacket;  
            StringBuffer msg;  
            while(true)  
            {  
                for(Map user:Server.userList)  
                {  
                    //服务器数据，标记server:  
                    msg=new StringBuffer("server:");  
                    for(Map map:Server.userList)  
                    {  
                        if(!map.get("id").toString().equals(user.get("id").toString()))  
                        {  
                            msg.append(map.get("id")+"#"+map.get("ip")+":"+map.get("port"));  
                            msg.append(",");  
                        }  
                    }  
                    if(!msg.toString().equals("server:"))  
                    {  
                        byte[] data=msg.toString().getBytes();  
                        //构造发送报文  
                        sendPacket = new DatagramPacket(data, data.length, (InetAddress)user.get("ip"), (Integer)user.get("port"));    
                        server.send(sendPacket);  
                    }  
                }  
                Thread.sleep(2000);  
                  
            }  
        }catch(Exception e){}  
    }  
      
      
    public static void main(String args[])throws Exception  
    {  
  
        int port=20000;  
          
        //创建一个UDPsocket  
        DatagramSocket server = new DatagramSocket(port);  
        byte[] buf = new byte[1024];    
        //接收数据的udp包  
        DatagramPacket packet = new DatagramPacket(buf, buf.length);  
         
        //开启服务  
        new Server(server).start();  
  
        String msg;  
        //循环接收数据  
        while(true)  
        {  
              
            server.receive(packet);  
              
            msg=new String(packet.getData(),0,packet.getLength());  
              
            if(msg!=null&&msg.equals("bye"))  
                break;  
              
            if(msg.length()>0)  
            {  
                System.out.println("收到数据来自：("+packet.getAddress()+":"+packet.getPort()+")="+msg);  
                if(!users.containsKey(packet.getAddress()+":"+packet.getPort()))  
                {  
                    Map map=new HashMap();  
                    map.put("id", index);  
                    map.put("ip", packet.getAddress());  
                    map.put("port", packet.getPort());  
                    userList.add(map);  
                      
                    users.put(packet.getAddress()+":"+packet.getPort(), index);  
                    index++;  
                }  
                  
            }  
        }  
        server.close();  
    }  
  
}  