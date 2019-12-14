package com.xwr.cameracodec;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestActivity extends AppCompatActivity {
  //private UDPSendVideo udpBuild;
  private DatagramSocket mSocket = null;
  private InetAddress myAddress = null;
  String s = "connect";
  boolean isConnect = false;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
 //   udpBuild = UDPSendVideo.getUdpBuild();
//    udpBuild.setUdpReceiveCallback(new UDPSendVideo.OnUDPReceiveCallbackBlock() {
//      @Override
//      public void OnParserComplete(DatagramPacket data) {
//        String strReceive = new String(data.getData(), 0, data.getLength());
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//        Date curDate =  new Date(System.currentTimeMillis());
//        String str = formatter.format(curDate);
//        //在真机上运行需要用handle回到主线程再更新UI，不然会崩。模拟器上不会
//        TextView receive = findViewById(R.id.receive_textView);
//        receive.append(str + ':' + strReceive + '\n');
//      }
//    });
    sendCmd();
  }
  public void sendMessage(View view) {
    EditText editText = findViewById(R.id.send_editText);
    String message = editText.getText().toString();
    //udpBuild.sendMessage(message);

    TextView send = findViewById(R.id.send_textView);
    send.append(message + '\n');
    sendCmd();
  }

  private void sendCmd() {
    try {
      mSocket = new DatagramSocket();
      myAddress = InetAddress.getByName("192.168.4.210");
    } catch (Exception e) {
      e.printStackTrace();
    }
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (!isConnect) {
          byte[] data = s.getBytes();
          DatagramPacket packet = new DatagramPacket(data, data.length, myAddress, 9000);
          try {
            mSocket.send(packet);
          } catch (IOException e) {
            e.printStackTrace();
          }

        }
      }
    }).start();
  }
}
