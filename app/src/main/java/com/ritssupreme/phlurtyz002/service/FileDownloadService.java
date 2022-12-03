package com.ritssupreme.phlurtyz002.service;


import android.os.Environment;
import android.util.Log;

import com.ritssupreme.phlurtyz002.client.RestClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import rx.Observable;
import rx.Subscriber;


/**
 * Created by kibrom on 6/22/17.
 */

public class FileDownloadService extends RestClient {

    private static FileDownloadService instance;

    private FileDownloadService() {
    }

    public static FileDownloadService getInstance() {

        if (instance == null) {

            instance = new FileDownloadService();
        }

        return instance;
    }

    public Observable<Integer> downloadApk() {


        return Observable.create(

                new Observable.OnSubscribe<Integer>() {

                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {

                        int count;

                        try {

                            URL url = new URL(host + "/app.apk");

                            URLConnection conection = url.openConnection();

                            conection.connect();

                            // getting file length
                            int lenghtOfFile = conection.getContentLength();

                            // input stream to read file - with 8k buffer
                            InputStream input = new BufferedInputStream(url.openStream(), 8192);

                            File root = Environment.getExternalStorageDirectory();

                            File dir = new File(root.getAbsolutePath() + "/download/");

                            if(dir.exists() == false){

                                dir.mkdirs();
                            }

                            File file = new File(dir,"app.apk");
                            // Output stream to write file
                            OutputStream output = new FileOutputStream(file);

                            byte data[] = new byte[1024];

                            while ((count = input.read(data)) != -1) {

                                // writing data to file
                                output.write(data, 0, count);
                            }

                            subscriber.onCompleted();

                            // flushing output
                            output.flush();

                            // closing streams
                            output.close();

                            input.close();

                        } catch (Exception e) {

                            subscriber.onError(e);

                            Log.e("Error: ", e.getMessage());
                        }
                    }

                });
    }

}
