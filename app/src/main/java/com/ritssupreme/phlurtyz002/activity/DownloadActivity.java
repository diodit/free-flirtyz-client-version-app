package com.ritssupreme.phlurtyz002.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.utils.ApiEndPoints;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {

    private Button install;

    private Button cancel;

    private ProgressBar progressBar;

    private Activity activity;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_download);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();

        addClickListener();

    }

    private void initView(){

        install = (Button)findViewById(R.id.download_download);

        cancel = (Button)findViewById(R.id.download_cancel);

        progressBar = (ProgressBar)findViewById(R.id.download_progressBar);

    }

    private void addClickListener(){

//        install.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                progressBar.setIndeterminate(true);
//
//                verifyStoragePermissions(activity);
//
//                FileDownloadService.getInstance().downloadApk()
//
//                        .subscribeOn(Schedulers.io())
//
//                        .onBackpressureDrop()
//
//                        .observeOn(AndroidSchedulers.mainThread())
//
//                        .subscribe(new Observer<Integer>() {
//                            @Override
//                            public void onCompleted() {
//
//                                progressBar.setIndeterminate(false);
//
//                                progressBar.setProgress(100);
//
//                                installApk();
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                                Log.d("Downloading Error",e.toString());
//                            }
//
//                            @Override
//                            public void onNext(Integer progress) {
//
//                            }
//                        });
//            }
//        });

        // new login for online site payment
       install.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ApiEndPoints.PaymentURL)));
           }
       });

       cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(DownloadActivity.this, MainModActivity.class));
           }
       });
    }

    private void installApk(){

        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "app.apk")), "application/vnd.android.package-archive");

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

    }

    public static void verifyStoragePermissions(Activity activity) {

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // save file
            } else {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();

        return true;
    }

}
