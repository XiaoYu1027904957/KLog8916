package com.senter.klog.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zbmobi on 16/9/9.
 */
public class BaseApi {

    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 30 * 1000;
    //写超时长，单位：毫秒
    //一次连接最大持续120秒。1次log最大200KB，按最差网络2G网2KB/S算，持续100秒。
    public static final int WRITE_TIME_OUT = 120 * 1000;
    //连接超时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = Math.max(READ_TIME_OUT, WRITE_TIME_OUT);

    public static Retrofit retrofit(String baseUrl, Converter.Factory factory) {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //增加头部信息
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request build = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(build);
            }
        };
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new BaseOKHttpInterceptor())
                .addInterceptor(headerInterceptor)
                .retryOnConnectionFailure(true)
                .build();
        //获取IP
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(factory)
                .baseUrl(baseUrl)
                .build();
        return retrofit;
    }

    public static Retrofit retrofit(String baseUrl) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        return retrofit(baseUrl, GsonConverterFactory.create(gson));
    }

    public static Retrofit retrofitString(String baseUrl) {
        return retrofit(baseUrl, StringConverterFactory.create());
    }

}

