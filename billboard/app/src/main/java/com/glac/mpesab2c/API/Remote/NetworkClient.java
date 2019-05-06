package com.glac.mpesab2c.API.Remote;

import com.glac.mpesab2c.API.Interceptors.AccessTokenInterceptor;
import com.glac.mpesab2c.API.Interceptors.AuthInterceptor;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.glac.mpesab2c.Utils.CommonUtils.CONNECTION_TIMEOUT;
import static com.glac.mpesab2c.Utils.CommonUtils.FETCH_TIMEOUT;
import static com.glac.mpesab2c.Utils.CommonUtils.INPUT_TIMEOUT;

public class NetworkClient {
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;
    private static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

    public static Retrofit getClient(String baseUrl, OkHttpClient client) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static OkHttpClient getPaymentOkhttpClient(String authToken) {
        OkHttpClient client = null;
        try {
            client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(FETCH_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(INPUT_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .sslSocketFactory(new TLSSocketFactory())
                    .addInterceptor(new AuthInterceptor(authToken))
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return client;
    }

    public static OkHttpClient getSTKPushOkhttpClient(String authToken) {
        try {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(INPUT_TIMEOUT, TimeUnit.SECONDS)
                    .sslSocketFactory(new TLSSocketFactory())
                    .readTimeout(FETCH_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(new AuthInterceptor(authToken))
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return okHttpClient;
    }

    public static OkHttpClient getAuthenticationOkhttpClient(String CONSUMER_KEY, String CONSUMER_SECRET) {
        try {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(INPUT_TIMEOUT, TimeUnit.SECONDS)
                    .sslSocketFactory(new TLSSocketFactory())
                    .readTimeout(FETCH_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(new AccessTokenInterceptor(CONSUMER_KEY, CONSUMER_SECRET))
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return okHttpClient;
    }
}