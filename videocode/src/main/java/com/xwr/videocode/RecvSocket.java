package com.xwr.videocode;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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
  private static final int BUFFER_LENGTH = 1024;
  public RecvSocket() {
    if(socket == null){
      Log.d("xwr","init");
      try {
        socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(PCM_PORT));
      } catch (SocketException e) {
        e.printStackTrace();
      }
    }
//    try {
//      socket = new DatagramSocket(PCM_PORT);
//    } catch (SocketException e) {
//      e.printStackTrace();
//    }
  }

  public void recvMessge() {
    new Thread() {
      public void run() {
//        try {
//          socket = new DatagramSocket(PCM_PORT);
//        } catch (SocketException e) {
//          e.printStackTrace();
//        }

        stopReceiver = true;
        Log.d("xwr","start recv");
        while (stopReceiver) {
          byte[] receBuf = new byte[1024];
          packet = new DatagramPacket(receBuf, receBuf.length);
          Log.d("xwr"," recv");
          try {
            socket.receive(packet);
            Log.d("xwr","length:"+packet.getAddress()+"port:"+packet.getLength());
            AudioTrackManager.getInstance().startPlay(packet.getData());
            if (packet != null) {
              packet.setLength(BUFFER_LENGTH);
            }
          } catch (IOException e) {
            e.printStackTrace();
            Log.e("xwr",e.getMessage());
          }
        }
      }
    }.start();
  }

  public void close() {
    stopReceiver = false;
    socket.close();
//    AudioTrackManager.getInstance().stopPlay();
  }


}
