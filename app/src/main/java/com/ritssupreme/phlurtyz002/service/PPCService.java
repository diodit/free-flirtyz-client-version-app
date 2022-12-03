package com.ritssupreme.phlurtyz002.service;


import com.ritssupreme.phlurtyz002.client.RestClient;
import com.ritssupreme.phlurtyz002.model.Advertisment;
import com.ritssupreme.phlurtyz002.utils.ApiEndPoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Created by kibrom on 6/22/17.
 */

public class PPCService extends RestClient {

    private static PPCService instance;

    private PPCService() {
    }

    public static PPCService getInstance() {

        if (instance == null) {

            instance = new PPCService();
        }

        return instance;
    }

    public Observable<Boolean> registerClick(Advertisment advertisment) {

            return makeObservable(registerClickCallable(advertisment))

                    .subscribeOn(Schedulers.computation());

    }

    // Register Click
    Callable<Boolean> registerClickCallable(final Advertisment _advertisment){

        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws JSONException,IOException {

                JSONObject clickJson = new JSONObject();

                clickJson.put("ad_id", _advertisment.getId());

                RequestBody body = RequestBody.create(JSON, clickJson.toString());

                Request request = builder.url(host + ApiEndPoints.PPC + "/registerClick").post(body).build();

                Response response = client.newCall(request).execute();

                if (response.code() == 200) {

                    return true;

                }

                return false;
            }
        };
    }
}
