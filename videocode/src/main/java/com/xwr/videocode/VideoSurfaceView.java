package com.xwr.videocode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.List;

import static com.xwr.videocode.CameraFormat.determineMaximumSupportedFramerate;
import static com.xwr.videocode.CameraFormat.getDgree;
import static com.xwr.videocode.TypeConUtil.intToByteArray;


/**
 * Create by xwr on 2019/11/23
 * Describe:
 */
@SuppressLint("NewApi")
public class VideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
  private static String TAG = "MyVideoView";
  private SurfaceHolder mSurfaceHolder;
  NV21Convertor mConvertor;
  MediaCodec mMediaCodec;
  private Camera mCamera;
  private IVideoRecoderListener mIVideoRecoderListener;
  private int mCameraId;
  private SendSocket mSendSocket;
  private SendSocket mSendSocket2;
  int width;
  int height;
  int framerate;
  int bitrate;
  Context mContext;
  //定义摄像机
  private Camera camera;


  //构造函数
  public VideoSurfaceView(Context context, int cameraId, int width, int height, int framerate) {
    super(context);
    //获取Holder
    mContext = context;
    mCameraId = cameraId;
    this.width = width;
    this.height = height;
    this.framerate = framerate;
    Log.d(TAG, "camera:" + cameraId);
    mSurfaceHolder = getHolder();
    initMediaCodec();
    mSurfaceHolder.addCallback(this);
    mSurfaceHolder.setFixedSize(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);


  }
public void initSocket(String address){
  mSendSocket = new SendSocket(address, 52100);
  mSendSocket2 = new SendSocket(address, 52000);
}

//  public void initSocket() {
//   // String address = NetWorkUtil.getIpAddressString();
//    String address = "194.168.4.210";
//    Log.d(TAG,address);
//    mSendSocket = new SendSocket(address, 52100);
//    mSendSocket2 = new SendSocket(address, 52000);
//  }

  public void startRecordVoice() {
    AudioManager.getInstance().startRecording(new AudioManager.OnAudioRecordListener() {
      @Override
      public void onVoiceRecord(byte[] data, int size) {
        if (mSendSocket2 == null) {
          Log.e(TAG, "please init socket");
        }
        mSendSocket2.sendMessage(data);
      }
    });
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    //开启摄像机
    boolean isCreate = createCamera(surfaceHolder);
    Log.d(TAG, "isCreate:" + isCreate);
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    //关闭摄像机
    stopCamera();
  }


  //关闭摄像机
  private void stopCamera() {
    if (camera != null) {
      camera.setPreviewCallback(null);
      camera.stopPreview();
      camera.release();
      camera = null;
    }
    mSendSocket2.close();
    mSendSocket.close();
    AudioManager.getInstance().stopRecording();

  }

  private void initMediaCodec() {
    Log.d(TAG, "init media codec");
    int dgree = getDgree(mContext);
    bitrate = 2 * width * height * framerate / 20;
    EncoderDebugger debugger = EncoderDebugger.debug(mContext, width, height);
    mConvertor = debugger.getNV21Convertor();
    try {
      mMediaCodec = MediaCodec.createByCodecName(debugger.getEncoderName());
      MediaFormat mediaFormat;
      if (dgree == 0) {
        mediaFormat = MediaFormat.createVideoFormat("video/avc", height, width);
      } else {
        mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
      }
      mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
      mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
      mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
        debugger.getEncoderColorFormat());
      mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
      mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
      mMediaCodec.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private boolean createCamera(SurfaceHolder surfaceHolder) {
    if (mCamera != null) {
      mCamera.release();
      mCamera = null;
    }
    try {
      Log.d(TAG, "create camera");
      mCamera = Camera.open(mCameraId);
      Camera.Parameters parameters = mCamera.getParameters();
      int[] max = determineMaximumSupportedFramerate(parameters);
      Camera.CameraInfo camInfo = new Camera.CameraInfo();
      Camera.getCameraInfo(mCameraId, camInfo);
      int cameraRotationOffset = camInfo.orientation;
      int rotate = (360 + cameraRotationOffset - getDgree(mContext)) % 360;
      parameters.setRotation(rotate);
      parameters.setPreviewFormat(ImageFormat.NV21);
      List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
      parameters.setPreviewSize(width, height);
      parameters.setPreviewFpsRange(max[0], max[1]);
      mCamera.setParameters(parameters);
      // mCamera.autoFocus(null);
      int displayRotation;
      displayRotation = (cameraRotationOffset - getDgree(mContext) + 360) % 360;
      mCamera.setDisplayOrientation(displayRotation);
      mCamera.setPreviewDisplay(surfaceHolder);
      return true;
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String stack = sw.toString();
      Log.e(TAG, stack);
      destroyCamera();
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 销毁Camera
   */
  protected synchronized void destroyCamera() {
    if (mCamera != null) {
      mCamera.stopPreview();
      try {
        mCamera.release();
      } catch (Exception e) {

      }
      mCamera = null;
    }
  }


  /**
   * 停止预览
   */
  public synchronized void stopRecord() {
    if (mCamera != null) {
      mCamera.setPreviewCallback(null);
      mCamera.setPreviewCallbackWithBuffer(null);
      mCamera.stopPreview();
    }
  }


  /**
   * 开启
   */
  public synchronized void startRecod() {
    if (mCamera != null) {
      mCamera.startPreview();
      int previewFormat = mCamera.getParameters().getPreviewFormat();
      Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
      int size = previewSize.width * previewSize.height
        * ImageFormat.getBitsPerPixel(previewFormat)
        / 8;
      mCamera.addCallbackBuffer(new byte[size]);
      mCamera.setPreviewCallbackWithBuffer(previewCallback);
    } else {
      Log.d(TAG, "start fail");
    }
    startRecordVoice();
  }

  //定义Camera的回调方法
  private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
    byte[] mPpsSps = new byte[0];

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
      synchronized (this) {
        if (data == null) {
          return;
        }
        ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
        byte[] dst = new byte[data.length];
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        if (getDgree(mContext) == 0) {
          dst = CameraFormat.rotateNV21Degree90(data, previewSize.width, previewSize.height);
        } else {
          dst = data;
        }
        try {
          int bufferIndex = mMediaCodec.dequeueInputBuffer(5000000);//等待的时间（ms)
          if (bufferIndex >= 0) {
            inputBuffers[bufferIndex].clear();
            mConvertor.convert(dst, inputBuffers[bufferIndex]);
            mMediaCodec.queueInputBuffer(bufferIndex, 0,
              inputBuffers[bufferIndex].position(),
              System.nanoTime() / 1000, 0);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            while (outputBufferIndex >= 0) {
              ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
              byte[] outData = new byte[bufferInfo.size];
              outputBuffer.get(outData);
              //记录pps和sps
              if (outData[0] == 0 && outData[1] == 0 && outData[2] == 0 && outData[3] == 1 && outData[4] == 103) {
                mPpsSps = outData;
              } else if (outData[0] == 0 && outData[1] == 0 && outData[2] == 0 && outData[3] == 1 && outData[4] == 101) {
                //在关键帧前面加上pps和sps数据
                byte[] iframeData = new byte[mPpsSps.length + outData.length];
                System.arraycopy(mPpsSps, 0, iframeData, 0, mPpsSps.length);
                System.arraycopy(outData, 0, iframeData, mPpsSps.length, outData.length);
                outData = iframeData;
              }
              byte[] length = intToByteArray(outData.length);
              byte[] testdata = new byte[length.length + outData.length];
              System.arraycopy(length, 0, testdata, 0, length.length);
              System.arraycopy(outData, 0, testdata, length.length, outData.length);
              //  mIVideoRecoderListener.onRecording(testdata);
              if (mSendSocket == null) {
                Log.e(TAG, "please init socket");
              }
              mSendSocket.sendMessage(testdata);
              mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
              outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
          } else {
            Log.e("easypusher", "No buffer available !");
          }
        } catch (Exception e) {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          e.printStackTrace(pw);
          String stack = sw.toString();
          Log.e("save_log", stack);
          e.printStackTrace();
        } finally {
          mCamera.addCallbackBuffer(dst);
        }
      }
    }
  };

  public void setIVideoRecoderListener(IVideoRecoderListener iVideoRecoderListener) {
    mIVideoRecoderListener = iVideoRecoderListener;
  }
}
