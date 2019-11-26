package com.xwr.videocode;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Create by xwr on 2019/11/25
 * Describe:
 */
public class SendSocket {
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
          DatagramPacket packet = new DatagramPacket(data, data.length, myAddress, port);
          mSocket.send(packet);
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
