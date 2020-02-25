package com.xwr.videocode;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Create by xwr on 2019/11/28
 * Describe:
 */
public class AudioTrackManager {
  private static AudioTrackManager instance;
  private AudioManager audioManager;
  private static String TAG = "AudioTrackManager";
  AudioTrack mAudioTrack = null;

  int bufferSize = 0;//最小缓冲区大小

  int sampleRateInHz = 8000;//采样率

  int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
  //int channelConfig = AudioFormat.CHANNEL_IN_MONO;
  int audioFormat = AudioFormat.ENCODING_PCM_16BIT; //量化位数

  public AudioTrackManager() {
    bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    bufferSize = bufferSize > 320 ? 320 : bufferSize;//20ms
    mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
    mAudioTrack.play();//启动音频设备
  }

  public static AudioTrackManager getInstance() {
    if (instance == null) {
      synchronized (AudioRecordManager.class) {
        if (instance == null) {
          instance = new AudioTrackManager();
        }
      }
    }
    return instance;
  }


  public void startPlay(byte[] data) {
    if (mAudioTrack == null) {
      mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRateInHz, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
      mAudioTrack.play();//启动音频设备
    }
    //播放时，状态校验
    if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
      Log.e(TAG, "不能播放，当前播放器未处于初始化状态..");
      return;
    }
    int result = mAudioTrack.write(data, 0, data.length);
    Log.d(TAG,"write:"+result);
  }

  public void stopPlay() {
    if (mAudioTrack != null) {
      if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {//初始化成功
        mAudioTrack.stop();//停止播放
      }
      if (mAudioTrack != null) {
        mAudioTrack.release();//释放audioTrack资源
      }
    }
  }

  public void pausePlay() {
    mAudioTrack.pause();
  }

  /**
   * 关闭扬声器
   */
  public void closeSpeaker(Context mContext) {
    audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    audioManager.setSpeakerphoneOn(false);
    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0,
      AudioManager.STREAM_VOICE_CALL);
    audioManager.setMode(AudioManager.MODE_IN_CALL);
  }

}
