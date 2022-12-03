package com.ritssupreme.phlurtyz002.utils;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public final class Utils {

    private static final String TAG = "Flirtyz";
    //Fragments Tags
    public static final String Main_Fragment = "MainFragment";


    public static void d(Object source, String message) {
        String name = source.getClass().getSimpleName();
        String output = name + " : " + message;
        Log.d(TAG, output);
    }

    public static void d(String source, String message) {
        String output = source + " : " + message;
        Log.d(TAG, output);
    }

    public static void e(Object source, String message) {
        String name = source.getClass().getSimpleName();
        String output = name + " : " + message;
        Log.e(TAG, output);
    }

    public static void e(String source, String message) {
        String output = source + " : " + message;
        Log.e(TAG, output);
    }

    public static void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromAssets(Context context, String fileName) {

        AssetManager assetManager = context.getAssets();

        InputStream is = null;

        try {
            is = assetManager.open(fileName);

        } catch (IOException e) {

            e.printStackTrace();

        }

        Bitmap bitmap = BitmapFactory.decodeStream(is);

        return bitmap;

    }

    public static void copyAssets(Context context) {

        AssetManager assetManager = context.getAssets();

        String[] files = null;

        try {
            files = assetManager.list("emojis");

        } catch (IOException e) {

            Log.e("tag", "Failed to get asset file list.", e);
        }
        for (String filename : files) {

            InputStream in = null;

            OutputStream out = null;

            try {

                in = assetManager.open("emojis/" + filename);

                File outFile = new File(context.getFilesDir(), filename);

                out = new FileOutputStream(outFile);

                copyFile(in, out);

                in.close();

                in = null;

                out.flush();

                out.close();

                out = null;

            } catch (IOException e) {

                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {

        byte[] buffer = new byte[1024];

        int read;

        while ((read = in.read(buffer)) != -1) {

            out.write(buffer, 0, read);
        }
    }


}
