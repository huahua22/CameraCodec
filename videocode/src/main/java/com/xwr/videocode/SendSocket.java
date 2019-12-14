package com.xwr.videocode;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Create by xwr on 2019/11/25
 * Describe:udp send socket
 */
public class SendSocket  {
  private DatagramSocket mSocket = null;
  private InetAddress myAddress = null;
  private static String TAG = "sendSocket";
  private int port;

  public SendSocket(String address, int port) {
    try {
      mSocket = new DatagramSocket();
      //对方的ip
      myAddress = InetAddress.getByName(address);
      this.port = port;
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void sendMessage(final byte[] data) {

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Log.d(TAG,"send bitmap0");
          DatagramPacket packet = new DatagramPacket(data, data.length, myAddress, port);
          Log.d(TAG,"send bitmap1");
          mSocket.send(packet);
          Log.d(TAG,"send bitmap2");
//          mSocket.receive(packet);
//          Log.d(TAG,"send bitmap3");
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    }).start();
  }

  public void close() {
    mSocket.close();
  }
}
