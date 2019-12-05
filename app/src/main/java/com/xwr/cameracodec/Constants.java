package com.xwr.cameracodec;

/**
 * Create by xwr on 2019/11/29
 * Describe:
 */
public class Constants {
  static enum DoorCMD {
    MONITOR,//监控
    HANGUPMONITOR, //挂断监控
    OPENDOOR,//开锁
    ANSWER,// 接听门口机呼叫
    HANGUPVIDEO,// 挂断视频
    REFUSE,// 拒接门口机呼叫
    GETTIME,//获取门口机时间,来更新室内机时间
    GET,
    HANGUPCALL,// 挂断呼叫
  }
}
