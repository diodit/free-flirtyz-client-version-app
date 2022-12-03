package com.ritssupreme.phlurtyz002.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.adapter.MyGridAdapter;
import com.ritssupreme.phlurtyz002.api.RetrofitClientInstance;
import com.ritssupreme.phlurtyz002.api.interfaces.ApiCallback;
import com.ritssupreme.phlurtyz002.api.interfaces.GetForAllCategories;
import com.ritssupreme.phlurtyz002.api.models.AllCategory;
import com.ritssupreme.phlurtyz002.core.BaseActivity;
import com.ritssupreme.phlurtyz002.database.DatabaseHandler;
import com.ritssupreme.phlurtyz002.model.GridItem;
import com.ritssupreme.phlurtyz002.utils.ApiEndPoints;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kibrom on 3/20/17.
 */

public class SpecialsActivity extends BaseActivity {

    private Context context;
    private static ArrayList<AllCategory> fetchedData;

    private Activity activity;

    GridView gridView;
    MyGridAdapter myGridAdapter;
    private  final static String URL_API = ApiEndPoints.SPECIALS_BASE_URL;
    ProgressBar simpleProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        activity = this;

        setContentView(R.layout.activity_specials);
        gridView = (GridView) findViewById(R.id.special_gridview);

        simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        simpleProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#fdcb05"), android.graphics.PorterDuff.Mode.MULTIPLY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchEmojiData(SpecialsActivity.this, simpleProgressBar); // your code
                pullToRefresh.setRefreshing(false);
            }
        });

        fetchEmojiData(SpecialsActivity.this, simpleProgressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        // return true;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));



        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                filterAndSearchEmojis(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAndSearchEmojis(query);
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    private void filterAndSearchEmojis (String filterText){
        if(!filterText.isEmpty()){
            ArrayList<AllCategory> newList = new ArrayList<>();
            for(AllCategory newArr : fetchedData){
                if(newArr.getName().toLowerCase().replace(' ', '_').contains(filterText.toLowerCase().replace(' ', '_'))){

                    newList.add(newArr);

                }
            }
            fetchedData = newList;
            doImageFunctions();
        }else{
            fetchEmojiData(SpecialsActivity.this, simpleProgressBar);
        }

    }

    private void fetchEmojiData(final Context context, final ProgressBar simpleProgressBar) {
        simpleProgressBar.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);

        if(!isOnline(this)){
            simpleProgressBar.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Please check your network connection.", Toast.LENGTH_SHORT).show();
        }else{
            getImagesFromApi(this, new ApiCallback() {
                @Override
                public void onResponse(boolean success) {
                    simpleProgressBar.setVisibility(View.INVISIBLE);
                    gridView.setVisibility(View.VISIBLE);
                    if(success){
                        doImageFunctions();
                    }else{
                      //  otherResponse(simpleProgressBar);
                    }
                }
            });
        }
    }

    public boolean isOnline(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

    private void doImageFunctions() {

        myGridAdapter = new MyGridAdapter(SpecialsActivity.this, fetchedData);
        gridView.setAdapter(myGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shareImage(fetchedData.get(position),activity);
            }
        });
    }

    public static void getImagesFromApi(final Context context, final ApiCallback callback){
        Call<List<AllCategory>> call = RetrofitClientInstance.getRetrofitFlirtyInstance().create(GetForAllCategories.class).getAllData(
                URL_API.concat("api/category/getAll")
        );

        call.enqueue(new Callback<List<AllCategory>>() {
            @Override
            public void onResponse(Call<List<AllCategory>> call, Response<List<AllCategory>> response) {

                if (response.isSuccessful()) {
                   // fetchedData = response.body();
                    ArrayList<AllCategory> imageObjects = new ArrayList<>();
                    for(AllCategory fetchd : response.body()) {
                        AllCategory allCategory = new AllCategory();
                        allCategory.setName(fetchd.getName());
                        allCategory.setFile(URL_API + fetchd.getFile());

                        imageObjects.add(allCategory);
                    }
                    fetchedData = imageObjects;

                    if(response.body().size() < 1){
                        Toast.makeText(context, "No special emojis found.", Toast.LENGTH_SHORT).show();
                    }

                    callback.onResponse(response.body() != null);
                }else{
                    Toast.makeText(context, "An error occurred while fetching emojis.", Toast.LENGTH_SHORT).show();
                    callback.onResponse(false);
                }

            }

            @Override
            public void onFailure(Call<List<AllCategory>> call, Throwable t) {
                call.cancel();
                Log.e("error", "onFailure: ",t );
                Toast.makeText(context, "Our server is currently under maintenance.", Toast.LENGTH_SHORT).show();
                callback.onResponse(false);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    static public void shareImage(AllCategory fetchedImage, final Context context) {
         final String url = fetchedImage.getFile();
        final String imageName = fetchedImage.getName();

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

        GridItem item = new GridItem();

        item.setAssetName(imageName);

        item.setAssetPath(url);
        DatabaseHandler db = new DatabaseHandler(context);

        db.addRecentApi(item);
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



}
