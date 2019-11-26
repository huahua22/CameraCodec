package com.xwr.videocode;

public class TypeConUtil {
  /**
   * int到byte[] 由高位到低位
   * @param i 需要转换为byte数组的整行值。
   * @return byte数组
   */
  public static byte[] intToByteArray(int i) {
    byte[] result = new byte[4];
    result[0] = (byte)((i >> 24) & 0xFF);
    result[1] = (byte)((i >> 16) & 0xFF);
    result[2] = (byte)((i >> 8) & 0xFF);
    result[3] = (byte)(i & 0xFF);
    return result;
  }
  public static byte[] charToByte(char c) {
    byte[] b = new byte[2];
    b[0] = (byte) ((c & 0xFF00) >> 8);
    b[1] = (byte) (c & 0xFF);
    return b;
  }
  public static byte[] shortToByteArray(short s) {
    byte[] shortBuf = new byte[2];
    for(int i=0;i<2;i++) {
      int offset = (shortBuf.length - 1 -i)*8;
      shortBuf[i] = (byte)((s>>>offset)&0xff);
    }
    return shortBuf;
  }
}
