package com.xwr.cameracodec;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xwr.videocode.VideoSurfaceView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  private static String TAG = "huahua";
  Button btnStart;
  Button btnStop;
  VideoSurfaceView mVideoView;
  int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
  RelativeLayout mRelativeLayout;
  //  EditText mEditText;
  private boolean isStart;
  private DatagramSocket mSocket = null;
  private InetAddress myAddress = null;
  String s = "connect";
  boolean isConnect = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    requestPermission();
    btnStart = (Button) findViewById(R.id.btn_start);
    btnStop = findViewById(R.id.btn_stop);
    //    mEditText = findViewById(R.id.address);
    btnStart.setOnClickListener(this);
    btnStop.setOnClickListener(this);
    mRelativeLayout = findViewById(R.id.main);
    mVideoView = new VideoSurfaceView(this, mCameraId, 640, 480, 10);
    mRelativeLayout.addView(mVideoView);

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


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_start:
        sendCmd();
        mVideoView.initSocket("192.168.4.210");
        mVideoView.startRecod();
        isStart = true;
        btnStart.setClickable(false);
        //        if (mEditText.getText().toString().isEmpty()) {
        //          Toast.makeText(this, "please input address", Toast.LENGTH_SHORT).show();
        //        } else {
        //          mVideoView.initSocket(mEditText.getText().toString());
        //          Log.d(TAG, "click start btn");
        //          mEditText.setVisibility(View.GONE);
        //
        //        }
        break;
      case R.id.btn_stop:
        mVideoView.stopRecord();
        btnStart.setClickable(true);
        break;

    }
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
