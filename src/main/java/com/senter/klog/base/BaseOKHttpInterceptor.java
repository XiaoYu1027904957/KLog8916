package com.senter.klog.base;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by zbmobi on 2016/9/29.
 */

public class BaseOKHttpInterceptor implements Interceptor {

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // 获得Connection，内部有route、socket、handshake、protocol方法
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        // 如: --> POST http://121.40.227.8:8088/api http/1.1
//        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
//        KLog.json("requestStartMessage = " + requestStartMessage);

        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);

        String bodyValue = "NONE";
        if (responseBody.contentLength() != 0) {
            bodyValue = URLDecoder.decode(source.buffer().clone().readString(UTF8), "UTF-8");
        }
//        KLog.json("response = " + bodyValue);
        Response newResponse = response.newBuilder()
                .body(ResponseBody.create(MEDIA_TYPE, bodyValue))
                .build();
        return newResponse;
    }

//    private boolean bodyEncoded(Headers headers) {
//        String contentEncoding = headers.get("Content-Encoding");
//        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
//    }
}