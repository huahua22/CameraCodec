package com.xwr.cameracodec;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xwr.videocode.VideoSurfaceView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  private static String TAG = "MainActivity";
  Button btnStart;
  Button btnStop;
  Button btnCall;
  TextView tvTip;
  VideoSurfaceView mVideoView;
  int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
  RelativeLayout mRelativeLayout;
  EditText mEditText;
  private InetAddress myAddress = null;
  private DatagramSocket mSocket = null;
  private boolean isStart;
  DatagramSocket socket = null;
  DatagramSocket receiveSocket = null;
  private DatagramPacket receivePacket;
  private static final int BUFFER_LENGTH = 320;
  private Thread cmdThread;
  private Thread sendThread;
  private boolean isThreadRunning = false;

  @SuppressLint("HandlerLeak")
  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case 1:
          String address = (String) msg.obj;
          mVideoView.initSocket(address);
          mEditText.setVisibility(View.GONE);
          tvTip.setVisibility(View.GONE);
          mVideoView.startRecod();
          break;
        default:
          break;
      }

    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mRelativeLayout = findViewById(R.id.main);
    mVideoView = new VideoSurfaceView(this, mCameraId);
    mRelativeLayout.addView(mVideoView);
    btnStart = (Button) findViewById(R.id.btn_start);
    btnStop = findViewById(R.id.btn_stop);
    btnCall = findViewById(R.id.btn_call);
    tvTip = findViewById(R.id.tvTip);
    mEditText = findViewById(R.id.ip_address);
    btnStart.setOnClickListener(this);
    btnStop.setOnClickListener(this);
    btnCall.setOnClickListener(this);
    startSocket();
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_start:
        tvTip.setVisibility(View.GONE);
        if (mEditText.getText().toString().isEmpty()) {
          Toast.makeText(this, "please input address", Toast.LENGTH_SHORT).show();
        } else {
          mVideoView.initSocket(mEditText.getText().toString());
          Log.d(TAG, "click start btn");
          mEditText.setVisibility(View.GONE);
          mVideoView.startRecod();
          isStart = true;
          btnStart.setClickable(false);
        }
        break;
      case R.id.btn_stop:
        mVideoView.stopRecord();
        isThreadRunning = false;
        if(sendThread!=null){
          sendThread.interrupt();
        }
        tvTip.setVisibility(View.GONE);
        btnStart.setClickable(true);
        btnCall.setClickable(true);
        break;
      case R.id.btn_call:
        if (mEditText.getText().toString().isEmpty()) {
          Toast.makeText(this, "please input address", Toast.LENGTH_SHORT).show();
        } else {
          sendCmd();
          tvTip.setVisibility(View.VISIBLE);
          mEditText.setVisibility(View.GONE);
          btnCall.setClickable(false);
        }
        break;

    }
  }


  private void startSocket() {
    if (socket != null)
      return;
    try {
      socket = new DatagramSocket(9000);
      if (receivePacket == null) {
        byte[] data = new byte[320];
        receivePacket = new DatagramPacket(data, BUFFER_LENGTH);
      }
      startSocketThread();
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  private void receiveMessage() {
    while (isThreadRunning) {
      if (socket != null) {
        try {
          socket.receive(receivePacket);
        } catch (IOException e) {
          Log.e(TAG, "UDP数据包接收失败！线程停止");
          e.printStackTrace();
          return;
        }
      }
      Log.d(TAG,"rec message1");
      if (receivePacket == null || receivePacket.getLength() == 0) {
        Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
        continue;
      }
      Log.d(TAG, " from " + receivePacket.getAddress().getHostAddress() + " length:" + receivePacket.getData().length);
      String recvData = new String(receivePacket.getData());
      if (recvData.indexOf("connect") != -1) {
        String message = "success";
        byte[] info = message.getBytes();
        DatagramPacket sendPacket = null;// 创建发送类型的数据报：  
        try {
          sendPacket = new DatagramPacket(info, info.length, InetAddress.getByName(receivePacket.getAddress().getHostAddress()), 9000);
          socket.send(sendPacket);
        } catch (UnknownHostException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (recvData != null && recvData.length() > 0) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = receivePacket.getAddress().getHostAddress();
        mHandler.sendMessage(msg);
        isThreadRunning = false;
      }
      if (receivePacket != null) {
        receivePacket.setLength(BUFFER_LENGTH);
      }
    }
  }

  private void startSocketThread() {
    isThreadRunning = true;
    cmdThread = new Thread(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "rec thread is running...");
        receiveMessage();
      }
    });
    cmdThread.start();
  }

  void sendCmd() {
    if (socket == null) {
      startSocket();
    }
  sendThread =  new Thread() {
    @Override
    public void run() {
      super.run();
      String message = "connect";
      byte[] configInfo = message.getBytes();
      InetAddress ip = null; //即目的IP
      try {
        ip = InetAddress.getByName(mEditText.getText().toString());
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
      while (isThreadRunning) {
        DatagramPacket sendPacket = new DatagramPacket(configInfo, configInfo.length, ip, 9000);// 创建发送类型的数据报：  
        try {
          socket.send(sendPacket);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      Log.d(TAG, "stop thread");

    }
  };
    sendThread.start();

  }



  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (isStart) {
      mVideoView.stopRecord();
    }
    mVideoView.closeVideo();
    mHandler.removeCallbacks(cmdThread);
    mHandler.removeCallbacks(sendThread);
  }


}

