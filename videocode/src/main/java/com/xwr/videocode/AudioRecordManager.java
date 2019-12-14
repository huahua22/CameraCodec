package com.xwr.videocode;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
/**
 * Create by xwr  on 2019/11/28.
 * Describe:录音
 */
public class AudioRecordManager {
  private static AudioRecordManager instance;

  boolean isRecording = false; //true表示正在录音

  AudioRecord audioRecord = null;

  int bufferSize = 0;//最小缓冲区大小

  int sampleRateInHz = 8000;//采样率

  int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道

  int audioFormat = AudioFormat.ENCODING_PCM_16BIT; //量化位数

  private static String TAG = " AudioManager";
  int count = 0;

  private AudioRecordManager() {
    //计算最小缓冲区
    bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    bufferSize = bufferSize > 320 ? 320 : bufferSize;//20ms
    Log.d(TAG, "bufferSize:" + bufferSize);
    audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRateInHz, channelConfig, audioFormat, bufferSize);
  }

  public static AudioRecordManager getInstance() {
    if (instance == null) {
      synchronized (AudioRecordManager.class) {
        if (instance == null) {
          instance = new AudioRecordManager();
        }
      }
    }
    return instance;
  }

  /**
   * 开始音频采集
   *
   * @param onAudioRecordListener
   */
  public void startRecording(OnAudioRecordListener onAudioRecordListener) {
    setOnAudioRecordListener(onAudioRecordListener);

    if (audioRecord == null) {
      audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, bufferSize);//创建AudioRecorder对象
    }

    new Thread(new Runnable() {
      @Override
      public void run() {
        isRecording = true;
        try {
          byte[] buffer = new byte[bufferSize];
          audioRecord.startRecording();//开始录音
          while (isRecording) {
            int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
            count++;
            if (getOnAudioRecordListener() != null) {
              getOnAudioRecordListener().onVoiceRecord(buffer, bufferReadResult);
            }
          Thread.sleep(3);
          }
          audioRecord.stop();//停止录音
        } catch (Throwable t) {
          Log.e(TAG, "Recording Failed");
        }
      }
    }).start();
  }

  /**
   * 停止音频采集
   */
  public void stopRecording() {
    isRecording = false;
    setOnAudioRecordListener(null);
  }

  public void onDestroy() {
    audioRecord.release();
    audioRecord = null;
  }

  public interface OnAudioRecordListener {

    /**
     * 采集到的音频信息数据回调到上层，通过IdeaVideoView 传送到设备端
     *
     * @param data
     */
    void onVoiceRecord(byte[] data, int size);

  }

  private OnAudioRecordListener onAudioRecordListener;

  public OnAudioRecordListener getOnAudioRecordListener() {
    return onAudioRecordListener;
  }

  public void setOnAudioRecordListener(OnAudioRecordListener onAudioRecordListener) {
    this.onAudioRecordListener = onAudioRecordListener;
  }

}
