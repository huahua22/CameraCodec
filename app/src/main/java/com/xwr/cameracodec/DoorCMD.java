package com.xwr.cameracodec;

public enum DoorCMD {
  MONITOR ,//监控
  HANGUPMONITOR, //挂断监控
  OPENDOOR,//开锁
  ANSWER,// 接听门口机呼叫
  HANGUPVIDEO,// 挂断视频
  REFUSE,// 拒接门口机呼叫
  GETTIME,//获取门口机时间,来更新室内机时间
  OK,
  CALL,
  HANGUPCALL,// 挂断呼叫
  BUSY
}
