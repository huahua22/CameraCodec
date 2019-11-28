
package com.xwr.videocode;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 类Util的实现描述：//TODO 类实现描述
 *
 */
public class FileUtil {



  /**
   * 保存数据到本地
   *
   * @param buffer 要保存的数据
   * @param offset 要保存数据的起始位置
   * @param length 要保存数据长度
   * @param path   保存路径
   * @param append 是否追加
   */
  public static void save(byte[] buffer, int offset, int length, String path, boolean append) {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(path, append);
      fos.write(buffer, offset, length);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (fos != null) {
        try {
          fos.flush();
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static String getSDPath() {
    File sdDir = null;
    boolean sdCardExist = Environment.getExternalStorageState().equals(
      Environment.MEDIA_MOUNTED);
    if (sdCardExist) {
      sdDir = Environment.getExternalStorageDirectory();
    }
    return sdDir.toString();
  }

  public static void createFile(String fileName) {
    File file = new File(fileName);
    if (file.exists()) {
      file.delete();
    }
    try {
      file.createNewFile();
    } catch (IOException e) {
      // TODO 自动生成的 catch 块
      e.printStackTrace();
    }
  }



}
