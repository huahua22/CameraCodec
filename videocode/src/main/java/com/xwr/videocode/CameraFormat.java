package com.xwr.videocode;

import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

import java.util.Iterator;
import java.util.List;

/**
 * Create by xwr  on 2019/11/22.
 * Describe:
 */
public class CameraFormat {
  public static int[] determineMaximumSupportedFramerate(Camera.Parameters parameters) {
    int[] maxFps = new int[]{0, 0};
    List<int[]> supportedFpsRanges = parameters.getSupportedPreviewFpsRange();
    for (Iterator<int[]> it = supportedFpsRanges.iterator(); it.hasNext(); ) {
      int[] interval = it.next();
      if (interval[1] > maxFps[1] || (interval[0] > maxFps[0] && interval[1] == maxFps[1])) {
        maxFps = interval;
      }
    }
    return maxFps;
  }

  public static int getDgree(Context mContext) {
    WindowManager mWindowManager= (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    int rotation = mWindowManager.getDefaultDisplay().getRotation();
    int degrees = 0;
    switch (rotation) {
      case Surface.ROTATION_0:
        degrees = 0;
        break; // Natural orientation
      case Surface.ROTATION_90:
        degrees = 90;
        break; // Landscape left
      case Surface.ROTATION_180:
        degrees = 180;
        break;// Upside down
      case Surface.ROTATION_270:
        degrees = 270;
        break;// Landscape right
    }
    return degrees;
  }

  /**
   * 将YUV420SP数据顺时针旋转90度
   *
   * @param data        要旋转的数据
   * @param imageWidth  要旋转的图片宽度
   * @param imageHeight 要旋转的图片高度
   * @return 旋转后的数据
   */
  public static byte[] rotateNV21Degree90(byte[] data, int imageWidth, int imageHeight) {
    byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
    // Rotate the Y luma
    int i = 0;
    for (int x = 0; x < imageWidth; x++) {
      for (int y = imageHeight - 1; y >= 0; y--) {
        yuv[i] = data[y * imageWidth + x];
        i++;
      }
    }
    // Rotate the U and V color components
    i = imageWidth * imageHeight * 3 / 2 - 1;
    for (int x = imageWidth - 1; x > 0; x = x - 2) {
      for (int y = 0; y < imageHeight / 2; y++) {
        yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
        i--;
        yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
        i--;
      }
    }
    return yuv;
  }
}
