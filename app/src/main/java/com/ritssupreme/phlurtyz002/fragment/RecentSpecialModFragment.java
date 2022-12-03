package com.ritssupreme.phlurtyz002.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.adapter.ModGridAdapter;
import com.ritssupreme.phlurtyz002.database.DatabaseHandler;
import com.ritssupreme.phlurtyz002.model.GridItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Stack;


/**
 * Created by kibrom on 3/20/17.
 */

public class RecentSpecialModFragment extends Fragment {

    private Context context;
    ModGridAdapter modGridAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {


        this.context = getContext();


        View view = inflater.inflate(R.layout.fragment_recent_special_mod, container, false);

        DatabaseHandler db = new DatabaseHandler(context);

        final Stack<GridItem> items = db.getRecentApiList();
        final Stack<GridItem> itemsUntouched = items;

      //  Toast.makeText(context, String.valueOf(itemsUntouched.size()), Toast.LENGTH_SHORT).show();



        GridView gridView = (GridView) view.findViewById(R.id.recent_special_mod_gridview);

        modGridAdapter = new ModGridAdapter(context, items);
        gridView.setAdapter(modGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shareImage(items.get(position), context);
            }
        });


        return view;
    }


    static public void shareImage(GridItem fetchedImage, final Context context) {
        final String url = fetchedImage.getAssetPath();

        if(Build.VERSION.SDK_INT > 23){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        Picasso.with(context).load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalImageUri(context, bitmap) );
                context.startActivity(Intent.createChooser(i, "Share Image"));
            }
            @Override public void onBitmapFailed(Drawable errorDrawable) {
                Toast.makeText(context, "Image path is invalid", Toast.LENGTH_SHORT).show();
            }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });

    }

//    static public Uri getLocalBitmapUri(Bitmap bitmap, Context context) {
//        Uri bmpUri = null;
//        try {
//            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
//            FileOutputStream out = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//            out.close();
//            bmpUri = Uri.fromFile(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bmpUri;
//    }

    public static Uri getLocalImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "IMG_" + Calendar.getInstance().getTime(),null);
        return Uri.parse(path);
    }
}




