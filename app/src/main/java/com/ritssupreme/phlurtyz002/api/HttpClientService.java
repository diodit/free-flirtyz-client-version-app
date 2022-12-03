package com.ritssupreme.phlurtyz002.api;

import android.annotation.SuppressLint;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

//for ssl timeout issues
public class HttpClientService {
        public static OkHttpClient getUnsafeOkHttpClient() {

            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] chain,
                                    String authType) {
                                //Do nothing
                            }

                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] chain,
                                    String authType) {
                                //Do nothing
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[0];
                            }
                        }};
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts,
                        new java.security.SecureRandom());

                final SSLSocketFactory sslSocketFactory = sslContext
                        .getSocketFactory();

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory)
                        .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .build();
                return okHttpClient;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
