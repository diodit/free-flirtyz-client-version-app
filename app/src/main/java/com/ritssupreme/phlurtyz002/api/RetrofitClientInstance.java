package com.ritssupreme.phlurtyz002.api;

import com.ritssupreme.phlurtyz002.utils.Helpers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitFlirtyInstance() {
//        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(250, TimeUnit.SECONDS)
//                .writeTimeout(250, TimeUnit.SECONDS)
//                .readTimeout(250, TimeUnit.SECONDS)
//                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Helpers.FLIRTY_SERVICE_URL)
                    .client(HttpClientService.getUnsafeOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getRetrofitFlirtyInstanceTwo() {
//        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(250, TimeUnit.SECONDS)
//                .writeTimeout(250, TimeUnit.SECONDS)
//                .readTimeout(250, TimeUnit.SECONDS)
//                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Helpers.ADVERTISMENT_URL)
                    .client(HttpClientService.getUnsafeOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
