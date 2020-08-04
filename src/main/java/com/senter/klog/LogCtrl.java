package com.senter.klog;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;


//  每个应用，调用KLog
// 判断prop persist.sys.stklog  0 不实时上传 >1实时上传。 0仅系统log自动上传，1同时上传，2不上传仅内网上传，3系统不上传。
// 不再单独把log写文件
// 上传时，如果当前有上传请求，log尽量合并上传
// 初始化传参:subTag,meid
// 服务器地址 persist.sys.stklog_server 未配置时，默认http://log.senterxljk.com.cn:9080，不提供接口修改。
// meid提供接口修改
public class LogCtrl {
    public static final String ROOT_TAG = "ST2303_";
    private static String sDeviceId = "";
    public static String DEF_LOG_DIR = Environment.getExternalStorageDirectory() + "/ST2303_LOG/";

    private static String sLogDir = DEF_LOG_DIR;
    private static String sLogFileName;
    private static int reallog  =0;
    private static String logserver;

    public static synchronized String getDeviceId() {
        return sDeviceId;
    }

    public static synchronized void setDeviceId(String sMeid) {
        LogCtrl.sDeviceId = sMeid;
    }

    /**
     * Initialize the KLog module.
     * 请在代码的最开始初始化。
     *
     * @param context
     * @param deviceId   imei meid or other prefix for the log file. if null will use imei0 or imei for tmp.
     *                   设备串号不能为空
     * @param tag    log tag, cant be null.模块名不能为空。
     */
    public static void init(final Context context, final String deviceId, final String tag, final int isRealLog) {


       logserver =  SharedPreferencesUtils.getStringData(context,"logip","");
        if (TextUtils.isEmpty(deviceId)) {
            throw new RuntimeException("args eror:deviceId must be set!设备串号不能为空！");
        }
        sDeviceId = deviceId;
        if (TextUtils.isEmpty(tag)) {
            throw new RuntimeException("args eror:tag must be set!模块标志不能为空！");
        }

        if (!TextUtils.isEmpty(DEF_LOG_DIR)) {
            if (tag.indexOf(Environment.getExternalStorageDirectory().getPath()) > 0) {
                sLogDir = DEF_LOG_DIR;
            } else {
                sLogDir = DEF_LOG_DIR;
            }
        }


        KLog.init(true, "#" + ROOT_TAG + tag + "#", new CallBack() {
            @Override
            public void onLogPrint(int type, String tags, String log) {
                if (!"sti1".equals(deviceId)){   //当没获取到设备id的时候不存日志。
                    final String saveLog = String.format("%s %s %s:%s%s: %s</br>\n",
                            Util.getNowTimeByFormat("yyyy-MM-dd HH:mm:ss.SSS"),
                            null, null,
                            KLog.typeStr(type),
                            tags.replace(ROOT_TAG,""),
                            log);
                    File logFileDir = new File(sLogDir);
                    if (!logFileDir.exists()) {
                        logFileDir.mkdirs();
                    }
                    LogCtrl.sLogFileName = String.format("%s_%s.log", sDeviceId, Util.getNowTimeByFormat("yyyyMMdd"));
                    final String saveLogWithTime = String.format("[%s] %s", Util.getNowTimeByFormat("yyyy-MM-dd HH:mm:ss.SSS"), saveLog);

                    KLog.file(tag, logFileDir, LogCtrl.sLogFileName, saveLogWithTime);
                    String path = sLogDir+"loglist.txt";
                    File file = new File(path);
                    if(!file.exists()){
                        file.getParentFile().mkdirs();
                    }
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    writeLog(path, LogCtrl.sLogFileName );

                    String pathk = sLogDir+"kloglist.txt";
                    File filek = new File(pathk);
                    if(!filek.exists()){
                        filek.getParentFile().mkdirs();
                    }
                    try {
                        filek.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (type > KLog.V) {//V级别不上传
                        if (isRealLog==1 || isRealLog==2){  //0 翌日  1 翌日实时 2 实时
                            LogUploader.getInstance().addLog(saveLog);
                        }
                    }
                }

            }

        });
    }

    public static String readFromXML(String filePath) {
        FileInputStream fileInputStream;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(filePath);
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return stringBuilder.toString();
    }

    public static void writeLog( String path,String message) {

        if (!readFromXML(path).contains(message)){
            File file = new File(path);

            File logfile = new File(sLogDir+message);
            if (!logfile.exists()){
                BufferedWriter fout;
                try {
                    fout = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(file, true)));
                    fout.write(message+"/" + "\n");
                    fout.flush();
                    fout.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static boolean isDebugMode() {
        return false;
    }

    public static String getLogServer() {
        return logserver;
    }

 }
