package com.xwr.cameracodec;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestActivity extends AppCompatActivity {

  private DatagramSocket mSocket = null;
  private InetAddress myAddress = null;
  private DatagramSocket ds = null;
  boolean isConnect = false;
  TextView mTextView;
  String s = "connect";
  Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case 0:
          mTextView.setText("正在呼叫.....");
          break;
        case 1:
          mTextView.setText("连接成功");
          Intent intent = new Intent(TestActivity.this, MainActivity.class);
          startActivity(intent);
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
    mTextView = findViewById(R.id.tip);

  }

  public void onCall(View view) {
    try {
      mSocket = new DatagramSocket();
      myAddress = InetAddress.getByName("192.168.4.210");
      ds = new DatagramSocket(9000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (!isConnect) {
          byte[] data = s.getBytes();
          mHandler.sendEmptyMessage(0);
          DatagramPacket packet = new DatagramPacket(data, data.length, myAddress, 9000);
          try {
            mSocket.send(packet);
          } catch (IOException e) {
            e.printStackTrace();
          }

        }


      }
    }).start();
    recv();
  }


  private void recv() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (!isConnect) {
          Log.d("huahua", "recv message");
          byte[] buf = new byte[1024];
          DatagramPacket dp = new DatagramPacket(buf, 0, buf.length);
          try {
            ds.receive(dp);
            String data = new String(dp.getData(), 0, dp.getLength());
            if(data!=null){
              Log.d("huahua", "data:" + data);
              isConnect = true;
              mHandler.sendEmptyMessage(1);
            }

          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();

  }
}
