package com.xwr.cameracodec;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xwr.videocode.VideoSurfaceView;

public class TestActivity extends AppCompatActivity {
 VideoSurfaceView mVideoView;
  int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
    mVideoView = new VideoSurfaceView(this, mCameraId, 640, 480, 10);
    mVideoView = findViewById(R.id.video_view);
    mVideoView.initSocket("192.168.4.210");
    mVideoView.startRecod();
  }
}
