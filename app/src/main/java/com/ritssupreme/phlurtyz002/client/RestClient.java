package com.ritssupreme.phlurtyz002.client;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritssupreme.phlurtyz002.BuildConfig;

import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import rx.Observable;
import rx.Subscriber;

import static android.content.ContentValues.TAG;

/**
 * Created by kibrom on 5/3/17.
 */

public class RestClient {

    protected OkHttpClient client;

    protected Request.Builder builder;

    protected ObjectMapper mapper;

    protected String host = BuildConfig.URL;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public RestClient() {

        client = new OkHttpClient();

        builder = new Request.Builder();

        mapper = new ObjectMapper();
    }

    protected static <T> Observable<T> makeObservable(final Callable<T> func) {

        return Observable.create(

                new Observable.OnSubscribe<T>() {

                    @Override
                    public void call(Subscriber<? super T> subscriber) {

                        try {

                            subscriber.onNext(func.call());

                            subscriber.onCompleted();

                        } catch(Exception ex) {

                            subscriber.onError(ex);

                            Log.e(TAG, "Error reading from the database", ex);
                        }
                    }
                });
    }
}
