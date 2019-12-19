package com.xwr.videocode;
/**
 * Create by xwr  on 2019/11/28.
 * Describe:data type conversion util
 */
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

  public static short[] toShortArray(byte[] src) {

    int count = src.length >> 1;
    short[] dest = new short[count];
    for (int i = 0; i < count; i++) {
      dest[i] = (short) (src[i * 2] << 8 | src[2 * i + 1] & 0xff);
    }
    return dest;
  }

  public static byte[] toByteArray(short[] src) {

    int count = src.length;
    byte[] dest = new byte[count << 1];
    for (int i = 0; i < count; i++) {
      dest[i * 2] = (byte) (src[i] >> 8);
      dest[i * 2 + 1] = (byte) (src[i] >> 0);
    }

    return dest;
  }
}
