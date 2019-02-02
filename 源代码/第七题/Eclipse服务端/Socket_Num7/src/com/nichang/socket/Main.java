package com.nichang.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    private static final int PORT = 9999;
    private List<Socket> mList = new ArrayList<Socket>();
    private ServerSocket server = null;
    private ExecutorService mExecutorService = null; //thread pool
    
    public static void main(String[] args) {
        new Main();
        
    }
    public Main() {
        try {
            server = new ServerSocket(PORT);
            mExecutorService = Executors.newCachedThreadPool();  //create a thread pool
            System.out.println("服务器已启动...");
            Socket client = null;
            while(true) {
                client = server.accept();
                //把客户端放入客户端集合中
                mList.add(client);
                mExecutorService.execute(new Service(client)); //start a new thread to handle the connection
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    class Service implements Runnable {
            private Socket socket;
            private BufferedReader in = null;
            private String msg = "";
            
            public Service(Socket socket) {
                this.socket = socket;
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //客户端只要一连到服务器，便向客户端发送下面的信息。
                    msg = "服务器地址：" +this.socket.getInetAddress() + "come toal:"
                        +mList.size()+"（服务器发送）";
                    this.sendmsg();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }

            @Override
            public void run() {
                try {
                    while(true) {
                        if((msg = in.readLine())!= null) {
                            //当客户端发送的信息为：exit时，关闭连接
                            if(msg.equals("exit")) {
                                System.out.println("即将关闭连接！！！");
                                mList.remove(socket);
                                in.close();
                                msg = "user:" + socket.getInetAddress()
                                    + "exit total:" + mList.size();
                                socket.close();
                                this.sendmsg();
                                break;
                                //接收客户端发过来的信息msg，然后发送给客户端。
                            } 
                            if ("shutdown".equals(msg)) {  
                                shutdown();  
                                // 发送消息回Android端  
                                 System.out.println("60秒后关机 "); 
                            }
                            if ("reboot".equals(msg)) {  
                                reboot();  
                                System.out.println("60秒后重启");  
                            }
                            if ("cancel".equals(msg)) {  
                                cancel();  
                                System.out.println("取消关机或重启");  
                            } else {
                                msg = socket.getInetAddress() + ":" + msg+"（服务器发送）";
                                this.sendmsg();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
          /**
           * 循环遍历客户端集合，给每个客户端都发送信息。
           */
           public void sendmsg() {
               System.out.println(msg);
               int num =mList.size();
               for (int index = 0; index < num; index ++) {
                   Socket mSocket = mList.get(index);
                   PrintWriter pout = null;
                   try {
                       pout = new PrintWriter(new BufferedWriter(
                               new OutputStreamWriter(mSocket.getOutputStream())),true);
                       pout.println(msg);
                   }catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
        }    
 // 关机  
    private static void shutdown() throws IOException {  
        Runtime.getRuntime().exec("shutdown -s -t 60");  
        System.out.println("电脑将在60秒后关机！！！");  
    }  
  
    // 重启  
    private static void reboot() throws IOException {  
        Runtime.getRuntime().exec("shutdown -r -t 60");  
        System.out.println("reboot ,60 seconds later ");  
    }  
  
    // 取消关机或重启  
    private static void cancel() throws IOException {  
        Runtime.getRuntime().exec("shutdown -a");  
        System.out.println("cancel shutdown or restart");  
    }  
}