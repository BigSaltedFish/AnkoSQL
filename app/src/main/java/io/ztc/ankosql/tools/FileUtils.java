package io.ztc.ankosql.tools;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import io.ztc.appkit.tools.ImgUtils;


public class FileUtils {
    /**
     * 保存相机的图片
     **/
    public static String saveCameraImage(Intent data,String FileName) {
        // 检查sd card是否存在
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.i("TAG", "sd card is not avaiable/writeable right now.");
            return null;
        }
        // 为图片命名啊
        String name = new DateFormat().format(FileName,
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";
        Bitmap bmp = (Bitmap) data.getExtras().get("data");// 解析返回的图片成bitmap

        // 保存文件
        FileOutputStream fos = null;
        @SuppressLint("SdCardPath")
        File file = new File("/mnt/sdcard/test/");
        file.mkdirs();// 创建文件夹
        @SuppressLint("SdCardPath")
        String fileName = "/mnt/sdcard/test/" + name;// 保存路径

        try {// 写入SD card
            fos = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
                return fileName;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }


    public static String saveBitmap(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapByUrl(String fileName){
        File file = new File(new File(Environment.getExternalStorageDirectory(), "Boohee"),fileName);
        return ImgUtils.getBitmapByPath(file.getPath());
    }
}
