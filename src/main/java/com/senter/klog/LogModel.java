package com.senter.klog;


import android.text.TextUtils;

import com.senter.klog.base.BaseApi;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zbmobi on 2017/3/13.
 */
public class LogModel {

    private final String DEF_LOG_SERVER = "http://log.senterxljk.com.cn:9080";
    private String logServer = DEF_LOG_SERVER;
    private long failedTimes = 0;

    public static LogModel sLogModel;

    public static LogModel getInstance() {
        if (sLogModel == null) {
            synchronized (LogModel.class) {
                if (sLogModel == null) {
                    sLogModel = new LogModel();
                }
            }
        }
        return sLogModel;
    }

    private LogModel() {
    }

    public String getLogServer() {
        logServer = LogCtrl.getLogServer();
        if (TextUtils.isEmpty(logServer)) {
            logServer = DEF_LOG_SERVER;
        }
        return logServer;
    }

    /**
     * 提交日志
     *
     * @param meid
     * @param log
     */
    public void addLog(String meid, String log) {
        String nowTime = Util.getNowTimeByFormat("yyyy-MM-dd HH:mm:ss");
        /**
         * 这里只是一个简单的上传用retrofit，会不会有点浪费,直接一个简单的http请求。
         */
        Call<String> call = BaseApi.retrofitString(getLogServer()).create(LogService.class)
                .postLog(log, nowTime, meid);
        try {
            Response<String> response = call.execute();

            if (!response.isSuccessful()) {
                failedTimes++;
                if (needLog(failedTimes)) {
                    KLog.d("no exeption,failedTimes=>" + failedTimes);
                    // 服务器响应但没能接收
                }
            } else {
                failedTimes = 0;
                long times = SysProp.getLong("persist.sys.stklog_rtup_fails", 0);
                if (times > 0) {
                    SysProp.set("persist.sys.stklog_rtup_fails", "0");
                }
            }
        } catch (Exception e) {
            failedTimes++;
            if (needLog(failedTimes)) {
                KLog.d("exeption, failedTimes=" + failedTimes + ": " + log);
                // 网络不好，network unreachable
                // 网络正常服务器超时不回复 time out
                e.printStackTrace();
                if (failedTimes > 100) {
                    long times = SysProp.getLong("persist.sys.stklog_rtup_fails", 0);
                    times = Math.max(times, failedTimes);
                    SysProp.set("persist.sys.stklog_rtup_fails", times + "");
                }
            }
        }
    }

    private boolean needLog(long i) {
        //log上传失败了，打印太多不好，不打印又不能定位上传失败的原因。
        int N = 10;
//        每10次开始打印一次，超过100次每100次,超过1000次每100次。
        return i % ((int) Math.pow(N, Math.floor(Math.log10(i + N)))) == 0;
//          每10次开始打印一次，超过100次每20次,超过1000次每30次。。。
//        (failedTimes % ((int) (N * Math.floor(Math.log10(failedTimes + N)))) == 0) {
    }

    public interface LogService {
        @FormUrlEncoded
        @POST("reportlog")
        Call<String> postLog(@Field("loginfo") String loginfo, @Field("logdt") String logdt
                , @Field("meid") String meid);
    }

}
