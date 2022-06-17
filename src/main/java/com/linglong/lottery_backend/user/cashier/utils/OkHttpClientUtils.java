package com.linglong.lottery_backend.user.cashier.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/8/1
 */
@Component
@Slf4j
public class OkHttpClientUtils {

    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 1000L))
    public  Response requestData(String requestUrl, RequestBody body) throws IOException {
        System.out.println("调用接口"+requestUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestUrl).post(body).build();
        client.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
        Response response = client.newCall(request).execute();
        return response;
    }
}
