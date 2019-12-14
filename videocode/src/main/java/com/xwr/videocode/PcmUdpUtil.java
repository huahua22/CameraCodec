package com.xwr.videocode;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create by xwr on 2019/12/14
 * Describe:
 */
public class PcmUdpUtil {
  private static final String TAG = "UDPBuild";
  //    单个CPU线程池大小
  private static final int POOL_SIZE = 5;
  private static final int BUFFER_LENGTH = 320;
  private boolean isThreadRunning = false;
  private ExecutorService mThreadPool;
  private Thread clientThread;

  private static PcmUdpUtil udpBuild;

  private DatagramSocket client;
  private DatagramPacket receivePacket;
  private OnUDPReceiveCallbackBlock udpReceiveCallback;

  //    构造函数私有化
  private PcmUdpUtil() {
    super();
    int cpuNumbers = Runtime.getRuntime().availableProcessors();
    // 根据CPU数目初始化线程池
    mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);
  }

  //    提供一个全局的静态方法
  public static PcmUdpUtil getUdpBuild() {
    if (udpBuild == null) {
      synchronized (PcmUdpUtil.class) {
        if (udpBuild == null) {
          udpBuild = new PcmUdpUtil();
        }
      }
    }
    return udpBuild;
  }

  public void startUDPSocket() {
    if (client != null)
      return;
    try {
      // 表明这个 Socket 在设置的端口上监听数据。
      client = new DatagramSocket(52000);
      if (receivePacket == null) {
        byte[] data = new byte[320];
        receivePacket = new DatagramPacket(data, BUFFER_LENGTH);
      }
      startSocketThread();
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  /**
   * 处理接受到的消息
   **/
  private void receiveMessage() {
    while (isThreadRunning) {
      if (client != null) {
        try {
          client.receive(receivePacket);
        } catch (IOException e) {
          Log.e(TAG, "UDP数据包接收失败！线程停止");
          e.printStackTrace();
          return;
        }
      }

      if (receivePacket == null || receivePacket.getLength() == 0) {
        Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
        continue;
      }
      AudioTrackManager.getInstance().startPlay(receivePacket.getData());
      Log.d(TAG,  " from " + receivePacket.getAddress().getHostAddress()+" length:"+receivePacket.getData().length);
      //            每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
      if (receivePacket != null) {
        receivePacket.setLength(BUFFER_LENGTH);
      }
    }
  }

  /**
   * 开启发送数据的线程
   **/
  private void startSocketThread() {
    clientThread = new Thread(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "clientThread is running...");
         receiveMessage();
      }
    });
    isThreadRunning = true;
    clientThread.start();
  }

  /**
   * 发送信息
   **/
  public void sendMessage(final byte[] data, final String address) {
    if (client == null) {
      startUDPSocket();
    }
    mThreadPool.execute(new Runnable() {
      @Override
      public void run() {
        try {
          InetAddress targetAddress = InetAddress.getByName(address);
          DatagramPacket packet = new DatagramPacket(data, data.length, targetAddress, 52000);
          client.send(packet);
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * 停止UDP
   **/
  public void stopUDPSocket() {
    isThreadRunning = false;
    receivePacket = null;
    if (clientThread != null) {
      clientThread.interrupt();
    }
    if (client != null) {
      client.close();
      client = null;
    }
    AudioTrackManager.getInstance().stopPlay();
    removeCallback();
  }

  public interface OnUDPReceiveCallbackBlock {
    void OnParserComplete(byte[] data);
  }

  public void setUdpReceiveCallback(OnUDPReceiveCallbackBlock callback) {
    this.udpReceiveCallback = callback;
  }

  public void removeCallback() {
    udpReceiveCallback = null;
  }
}
