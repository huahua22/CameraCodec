package com.xwr.videocode;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Create by xwr on 2019/11/25
 * Describe:net util
 */
public class NetWorkUtil {

  /**
   * 将ip的整数形式转换成ip形式
   *
   * @param ipInt
   * @return
   */
  public static String int2ip(int ipInt) {
    StringBuilder sb = new StringBuilder();
    sb.append(ipInt & 0xFF).append(".");
    sb.append((ipInt >> 8) & 0xFF).append(".");
    sb.append((ipInt >> 16) & 0xFF).append(".");
    sb.append((ipInt >> 24) & 0xFF);
    return sb.toString();
  }

  public static String getIpAddressString() {
    try {
      for (Enumeration<NetworkInterface> enNetI = NetworkInterface
        .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
        NetworkInterface netI = enNetI.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = netI
          .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
            return inetAddress.getHostAddress();
          }
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return "0.0.0.0";

  }
}
