package com.ritssupreme.phlurtyz002.service;

import com.ritssupreme.phlurtyz002.client.RestClient;
import com.ritssupreme.phlurtyz002.model.Advertisment;
import com.ritssupreme.phlurtyz002.utils.ApiEndPoints;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by kibrom on 6/21/17.
 */

public class AdvertismentService extends RestClient {

    private static AdvertismentService instance;

    private AdvertismentService(){}

    public static AdvertismentService getInstance(){

        if(instance == null){

            instance = new AdvertismentService();
        }

        return instance;
    }

    //Get all advertisments
    public Observable<List<Advertisment>> getAllAdvertisments() {

        return makeObservable(getAllAdvertismentCallable())

                .subscribeOn(Schedulers.computation());
    }


    // Register Click
    Callable<List<Advertisment>> getAllAdvertismentCallable(){

        return new Callable<List<Advertisment>>() {
            @Override
            public List<Advertisment> call() throws JSONException,IOException {

                List<Advertisment> advertisments = new ArrayList<>();

                Request request = builder.url(host + ApiEndPoints.ADVERTISMENT +"/advertisments").build();

                Response response = client.newCall(request).execute();

                if(response.code() == 200){

                    JSONArray jsonArray = new JSONArray(response.body().string());

                    for (int i = 0;i < jsonArray.length();i++) {

                        Advertisment advertisment = mapper.readValue(jsonArray.getJSONObject(i).toString(),Advertisment.class);

                        advertisments.add(advertisment);

                    }

                }

                return advertisments;
            }
        };
    }

}
