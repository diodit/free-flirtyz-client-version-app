package com.ritssupreme.phlurtyz002.fragment;

import android.app.Activity;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.adapter.MyGridAdapter;
import com.ritssupreme.phlurtyz002.api.RetrofitClientInstance;
import com.ritssupreme.phlurtyz002.api.interfaces.ApiCallback;
import com.ritssupreme.phlurtyz002.api.interfaces.GetForAllCategories;
import com.ritssupreme.phlurtyz002.api.models.AllCategory;
import com.ritssupreme.phlurtyz002.database.DatabaseHandler;
import com.ritssupreme.phlurtyz002.model.GridItem;
import com.ritssupreme.phlurtyz002.utils.ApiEndPoints;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by kibrom on 3/20/17.
 */

public class MainModCategoryFragment9 extends Fragment implements Checkable {

    private Activity activity;

    private Context context;
    private static ArrayList<AllCategory> fetchedData;

    GridView gridView;
    MyGridAdapter myGridAdapter;
    ProgressBar simpleProgressBar;


    private List<GridItem> selectedForFavorite = new ArrayList<>();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.context = getContext();

        activity = getActivity();

        final String filterText = this.getArguments().getString("filterText");
        final String categoryId = this.getArguments().getString("categoryId");


        View view = inflater.inflate(R.layout.fragment_main_mods, container, false);

        gridView = (GridView) view.findViewById(R.id.special_gridview);

        simpleProgressBar = (ProgressBar) view.findViewById(R.id.simpleProgressBar);
        simpleProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#fdcb05"), android.graphics.PorterDuff.Mode.MULTIPLY);


        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchEmojiData(activity, simpleProgressBar, filterText, categoryId); // your code
                pullToRefresh.setRefreshing(false);
            }
        });

        fetchEmojiData(activity, simpleProgressBar, filterText, categoryId);


        return view;
    }

    private void fetchEmojiData(final Context context, final ProgressBar simpleProgressBar, final String filterText, final String categoryId) {
        simpleProgressBar.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);

        if(!isOnline(requireActivity())){
            simpleProgressBar.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Please check your network connection.", Toast.LENGTH_SHORT).show();
        }else{
            getImagesFromApi(activity, new ApiCallback() {
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
            }, filterText, categoryId);
        }
    }

    public boolean isOnline(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

    private void doImageFunctions() {

        myGridAdapter = new MyGridAdapter(activity, fetchedData);
        gridView.setAdapter(myGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shareImage(fetchedData.get(position), activity);
            }
        });

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);


        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                mode.setTitle("Select Emoji");

                mode.setSubtitle("One Emoji selected");

                MenuInflater inflater = getActivity().getMenuInflater();

                inflater.inflate(R.menu.action_mode, menu);


                return true;

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.add_to_favorite:

                        mode.finish();

                        DatabaseHandler db = new DatabaseHandler(context);

                        db.addFavoriteInBulk(selectedForFavorite);
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
            public void onItemCheckedStateChanged(ActionMode mode, int position,long id, boolean checked) {

                //TODO add emojis in array. remove emoji from array when unchecked

                GridItem gridItem = new GridItem();

                gridItem.setAssetName(fetchedData.get(position).getName());

                gridItem.setAssetPath(fetchedData.get(position).getFile());

                if(checked){

                    selectedForFavorite.add(gridItem);

                }else {

                    Iterator<GridItem> it = selectedForFavorite.iterator();

                    while (it.hasNext()){

                        if(it.next().getAssetPath().equals(gridItem.getAssetPath())){

                            it.remove();
                        }
                    }
                }

                int selectCount = gridView.getCheckedItemCount();

                switch (selectCount) {

                    case 1:

                        mode.setSubtitle("Emoji selected");

                        break;

                    default:

                        mode.setSubtitle("" + selectCount + " Emoji selected");

                        break;
                }

            }
        });
    }

    public static void getImagesFromApi(final Context context, final ApiCallback callback, final  String filterText, final String categoryId){
        Call<List<AllCategory>> call = RetrofitClientInstance.getRetrofitFlirtyInstance().create(GetForAllCategories.class).getAllData(ApiEndPoints.CategoryById.concat(categoryId));

        call.enqueue(new Callback<List<AllCategory>>() {
            @Override
            public void onResponse(Call<List<AllCategory>> call, Response<List<AllCategory>> response) {

                if (response.isSuccessful()) {

                    ArrayList<AllCategory> imageObjects = new ArrayList<>();
                    if(filterText.isEmpty()){

                        for(AllCategory fetchd : response.body()) {
                            AllCategory allCategory = new AllCategory();
                            allCategory.setName(fetchd.getName());
                            allCategory.setFile(ApiEndPoints.SPECIALS_BASE_URL + fetchd.getFile());

                            imageObjects.add(allCategory);
                        }
                    }else{
                        for(AllCategory fetchd : response.body()) {
                            if(fetchd.getName().toLowerCase().contains(filterText.toLowerCase())){
                                AllCategory allCategory = new AllCategory();
                                allCategory.setName(fetchd.getName());
                                allCategory.setFile(ApiEndPoints.SPECIALS_BASE_URL + fetchd.getFile());

                                imageObjects.add(allCategory);
                            }

                        }

                    }
                    fetchedData = imageObjects;


                    if(response.body().size() < 1){
                        Toast.makeText(context, "No special emojis found.", Toast.LENGTH_SHORT).show();
                    }

                    callback.onResponse(response.body() != null);
                }else{
                    Toast.makeText(context, "An error occurred while fetching emojis." + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    callback.onResponse(false);
                }

            }

            @Override
            public void onFailure(Call<List<AllCategory>> call, Throwable t) {
                call.cancel();
                Log.e("error", "onFailure: ",t );
                Toast.makeText(context, "Connection Error.", Toast.LENGTH_SHORT).show();
                callback.onResponse(false);
            }
        });

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
                i.putExtra(Intent.EXTRA_STREAM,getImageUri( context,bitmap));
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

        db.addRecent(item);
    }





    public static Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "IMG_" + Calendar.getInstance().getTime(),null);
        return Uri.parse(path);
    }



    @Override
    public void setChecked(boolean checked) {

    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void toggle() {

    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
//        }
//    }
}




