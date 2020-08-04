package com.senter.klog.type;

import android.util.Log;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Random;

/**
 * Created by zhaokaiqiang on 15/11/18.
 */
public class FileLog {

    private static FileChannel fcin;
    private static FileLock flin;
    private static RandomAccessFile fis;


    public static void printFile(String tag, File targetDirectory, String fileName, String headString, String msg) {
        fileName = (fileName == null) ? getFileName() : fileName;
        if (save(targetDirectory, fileName, msg)) {
//            Log.d(TAG, headString + " save log success ! location is >>>" + targetDirectory.getAbsolutePath() + "/" + fileName);
        } else {
            Log.e(tag, headString + "save log fails !");
        }
    }

    private static boolean save(File dic, String fileName, String msg) {
        File file = new File(dic, fileName);
        //给该文件加锁 ,写前加锁写完后释放锁。
        OutputStream os = null;
        OutputStreamWriter osw = null;
        try {
            fis = new RandomAccessFile(file, "rw");
             fcin=fis.getChannel();
             flin=fcin.tryLock();

            os = new FileOutputStream(file, true);
            osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(msg);
            osw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                flin.release();
                fcin.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                flin.release();
                fcin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private static String getFileName() {
        Random random = new Random();
        return "KLog_" + Long.toString(System.currentTimeMillis() + random.nextInt(10000)).substring(4) + ".txt";
    }

}
