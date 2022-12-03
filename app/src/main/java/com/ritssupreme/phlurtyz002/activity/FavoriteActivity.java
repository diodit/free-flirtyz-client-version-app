package com.ritssupreme.phlurtyz002.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.StrictMode;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.adapter.ModGridAdapter;
import com.ritssupreme.phlurtyz002.core.BaseActivity;
import com.ritssupreme.phlurtyz002.database.DatabaseHandler;
import com.ritssupreme.phlurtyz002.model.GridItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * Created by kibrom on 3/20/17.
 */

public class FavoriteActivity extends BaseActivity {

    private Context context;

    private Activity activity;

    ModGridAdapter modGridAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        activity = this;

        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DatabaseHandler db = new DatabaseHandler(this);

        final List<GridItem> itemsDb = db.getFavoriteList();

        Stack<GridItem> stack = new Stack<>();
        for(GridItem itemsY : itemsDb){
            GridItem gridItem = new GridItem();
            gridItem.setAssetName(itemsY.getAssetName());
            gridItem.setAssetPath(itemsY.getAssetPath());
            stack.add(gridItem);
        }


        final Stack<GridItem> items = stack;

        //final List<GridItem> items = db.getFavoriteList();

       // List<String> itemNames = new ArrayList<>();

       // List<Bitmap> images = new ArrayList<>();

//        for (GridItem item : items) {
//
//            try {
//
//                InputStream is = this.openFileInput(item.getAssetPath());
//
//                images.add(BitmapFactory.decodeStream(is));
//
//            } catch (FileNotFoundException e) {
//
//                e.printStackTrace();
//            }
//
//            itemNames.add(item.getAssetName());
//        }

        final GridView gridView = (GridView) findViewById(R.id.favorite_gridview);

       // GridAdapter adapter = new GridAdapter(this, itemNames, images);

        modGridAdapter = new ModGridAdapter(this, items);

        gridView.setAdapter(modGridAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                mode.setTitle("Select Emoji");


                mode.setSubtitle("One Emoji selected");

                MenuInflater inflater = getMenuInflater();

                inflater.inflate(R.menu.remove_from_fav_rec, menu);


                return true;

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // TODO Auto-generated method stub

                switch (item.getItemId()) {

                    case R.id.add_to_favorite:

                        mode.finish();
                }

                int selectCount = gridView.getCheckedItemCount();
                switch (selectCount) {
                    case 1:
                        mode.setSubtitle("One Emoji selected");


                        break;
                    default:
                        mode.setSubtitle("" + selectCount + " Emoji selected");

                        break;
                }

                return true;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // TODO Auto-generated method stub

                //TODO add emojis in array. remove emoji from array when unchecked
//                String[] fileName = imagePaths.get(position).split("/");
//
//                final File photoFile = new File(context.getFilesDir(), fileName[1]);

//                Log.d("fileName", photoFile.toString());

                int selectCount = gridView.getCheckedItemCount();
                switch (selectCount) {
                    case 1:
                        mode.setSubtitle("One Emoji selected");
                        break;
                    default:
                        mode.setSubtitle("" + selectCount + " Emoji selected");
                        break;
                }

            }
        });
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                String fileName = items.get(position).getAssetPath();
//
//                final File photoFile = new File(context.getFilesDir(), fileName);
//
//                Uri uri = FileProvider.getUriForFile(context, "com.dio.phlurtyz002", photoFile);
//
//                Intent sendIntent = ShareCompat.IntentBuilder.from(activity)
//
//                        .setType("image/png")
//
//                        .setSubject("Subject")
//
//                        .setStream(uri)
//
//                        .setChooserTitle("Share Emoji")
//
//                        .createChooserIntent()
//
//                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//                context.startActivity(sendIntent);
//
//            }
//        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shareImage(items.get(position), context);
            }
        });

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
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                context.startActivity(Intent.createChooser(i, "Share Image"));
            }
            @Override public void onBitmapFailed(Drawable errorDrawable) {
                Toast.makeText(context, "Image path is invalid", Toast.LENGTH_SHORT).show();
            }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });

    }

    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
