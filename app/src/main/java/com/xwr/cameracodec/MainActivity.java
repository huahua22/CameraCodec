package com.xwr.cameracodec;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xwr.videocode.IVideoRecoderListener;
import com.xwr.videocode.VideoSurfaceView;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  private static String TAG = "MainActivity";
  Button btnStart;
  Button btnStop;
  VideoSurfaceView mVideoView;
  int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
  RelativeLayout mRelativeLayout;
  EditText mEditText;
  private InetAddress myAddress = null;
  private DatagramSocket mSocket = null;
  private boolean isStart;
  private HandlerThread mHandlerThread ;
  Handler workHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    requestPermission();
    btnStart = (Button) findViewById(R.id.btn_start);
    btnStop = findViewById(R.id.btn_stop);
    mEditText = findViewById(R.id.ip_address);
    btnStart.setOnClickListener(this);
    btnStop.setOnClickListener(this);
    mRelativeLayout = findViewById(R.id.main);
    mVideoView = new VideoSurfaceView(this, mCameraId, 640, 480);
    mRelativeLayout.addView(mVideoView);
    try {
      mSocket = new DatagramSocket();
      myAddress = InetAddress.getByName("192.168.4.210");
    } catch (Exception e) {
      e.printStackTrace();
    }
//    mHandlerThread = new HandlerThread("handlerThread");
//    mHandlerThread.start();
//    workHandler= new Handler(mHandlerThread.getLooper()){
//      @Override
//      public void handleMessage(Message msg) {
//        super.handleMessage(msg);
//        byte[] data = (byte[]) msg.obj;
//        try {
//          DatagramPacket packet = new DatagramPacket(data, data.length, myAddress, 52100);
//          mSocket.send(packet);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    };
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_start:
        if (mEditText.getText().toString().isEmpty()) {
          Toast.makeText(this, "please input address", Toast.LENGTH_SHORT).show();
        } else {
          mVideoView.initSocket(mEditText.getText().toString());
          Log.d(TAG, "click start btn");
          mEditText.setVisibility(View.GONE);
          mVideoView.startRecod();
         // sendMessage();
          isStart = true;
          btnStart.setClickable(false);
        }
        break;
      case R.id.btn_stop:
        mVideoView.stopRecord();
        btnStart.setClickable(true);
        break;

    }
  }


  void sendMessage() {
    mVideoView.setIVideoRecoderListener(new IVideoRecoderListener() {
      @Override
      public void onRecording(final byte[] data) {
        Message message = new Message();
        message.obj = data;
        workHandler.sendMessage(message);

      }
    });


  }


  void requestPermission() {
    final int REQUEST_CODE = 1;
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{
          Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
        REQUEST_CODE);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (isStart) {
      mVideoView.stopRecord();
    }
    mVideoView.closeVideo();
  }


}

