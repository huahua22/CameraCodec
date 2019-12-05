package com.xwr.videocode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Create by xwr on 2019/11/29
 * Describe:
 */
public class RecvSocket {
  private DatagramSocket socket;
  private DatagramPacket packet;
  private static final int PCM_PORT = 52000;
  private boolean stopReceiver = false;

  public RecvSocket() {
    try {
      socket = new DatagramSocket(PCM_PORT);
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  public void recvMessge() {
    new Thread() {
      public void run() {
        try {
          socket = new DatagramSocket(PCM_PORT);
        } catch (SocketException e) {
          e.printStackTrace();
        }
        byte[] receBuf = new byte[320];
        packet = new DatagramPacket(receBuf, receBuf.length);
        stopReceiver = true;
        while (stopReceiver) {
          try {
            socket.receive(packet);
            AudioTrackManager.getInstance().startPlay(packet.getData());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
  }

  public void close() {
    stopReceiver = false;
    socket.close();
    AudioTrackManager.getInstance().stopPlay();
  }


}
