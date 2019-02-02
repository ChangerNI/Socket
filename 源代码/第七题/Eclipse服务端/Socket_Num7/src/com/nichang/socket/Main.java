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
            System.out.println("������������...");
            Socket client = null;
            while(true) {
                client = server.accept();
                //�ѿͻ��˷���ͻ��˼�����
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
                    //�ͻ���ֻҪһ����������������ͻ��˷����������Ϣ��
                    msg = "��������ַ��" +this.socket.getInetAddress() + "come toal:"
                        +mList.size()+"�����������ͣ�";
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
                            //���ͻ��˷��͵���ϢΪ��exitʱ���ر�����
                            if(msg.equals("exit")) {
                                System.out.println("�����ر����ӣ�����");
                                mList.remove(socket);
                                in.close();
                                msg = "user:" + socket.getInetAddress()
                                    + "exit total:" + mList.size();
                                socket.close();
                                this.sendmsg();
                                break;
                                //���տͻ��˷���������Ϣmsg��Ȼ���͸��ͻ��ˡ�
                            } 
                            if ("shutdown".equals(msg)) {  
                                shutdown();  
                                // ������Ϣ��Android��  
                                 System.out.println("60���ػ� "); 
                            }
                            if ("reboot".equals(msg)) {  
                                reboot();  
                                System.out.println("60�������");  
                            }
                            if ("cancel".equals(msg)) {  
                                cancel();  
                                System.out.println("ȡ���ػ�������");  
                            } else {
                                msg = socket.getInetAddress() + ":" + msg+"�����������ͣ�";
                                this.sendmsg();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
          /**
           * ѭ�������ͻ��˼��ϣ���ÿ���ͻ��˶�������Ϣ��
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
 // �ػ�  
    private static void shutdown() throws IOException {  
        Runtime.getRuntime().exec("shutdown -s -t 60");  
        System.out.println("���Խ���60���ػ�������");  
    }  
  
    // ����  
    private static void reboot() throws IOException {  
        Runtime.getRuntime().exec("shutdown -r -t 60");  
        System.out.println("reboot ,60 seconds later ");  
    }  
  
    // ȡ���ػ�������  
    private static void cancel() throws IOException {  
        Runtime.getRuntime().exec("shutdown -a");  
        System.out.println("cancel shutdown or restart");  
    }  
}