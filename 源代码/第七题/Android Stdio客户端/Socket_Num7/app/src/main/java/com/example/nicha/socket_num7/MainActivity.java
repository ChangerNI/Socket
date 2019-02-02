package com.example.nicha.socket_num7;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable {
    private TextView tv_msg = null;
    private EditText ed_msg = null;
    private Button btn_send = null;
    private Button btn_start = null;
    // private Button btn_login = null;
    private static final String HOST = "10.0.2.2";
    private static final int PORT = 9999;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String content = "";
    //接收线程发送过来信息，并用TextView显示
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_msg.setText(content);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_msg = (TextView) findViewById(R.id.textview);
        ed_msg = (EditText) findViewById(R.id.edittext);
        btn_send = (Button) findViewById(R.id.button4);
        btn_start = (Button) findViewById(R.id.button1);
        btn_start.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
               new Thread(){
                   @Override
                   public void run() {
                       try {
                           socket = new Socket(HOST, PORT);
                           in = new BufferedReader(new InputStreamReader(socket
                                   .getInputStream()));
                           out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                                   socket.getOutputStream())), true);
                       } catch (IOException ex) {
                           ex.printStackTrace();
                           ShowDialog("login exception" + ex.getMessage());
                       }
                   }
               }.start();
            }
        });

        btn_send.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String msg = ed_msg.getText().toString();
                if (socket.isConnected()) {
                    if (!socket.isOutputShutdown()) {
                        out.println(msg);
                    }
                }
            }
        });
        //启动线程，接收服务器发送过来的数据
        new Thread(MainActivity.this).start();
    }
    /**
     * 如果连接出现异常，弹出AlertDialog！
     */
    public void ShowDialog(String msg) {
        new AlertDialog.Builder(this).setTitle("notification").setMessage(msg)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    /**
     * 读取服务器发来的信息，并通过Handler发给UI线程
     */
    public void run() {
        try {
            while (true) {
                if (!socket.isClosed()) {
                    if (socket.isConnected()) {
                        if (!socket.isInputShutdown()) {
                            if ((content = in.readLine()) != null) {
                                content += "\n";
                                mHandler.sendMessage(mHandler.obtainMessage());
                            } else {

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}