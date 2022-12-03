package com.ritssupreme.phlurtyz002.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ritssupreme.phlurty002.broadcastReceiver.NetworkChangeReceiver;
import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.adapter.ViewPagerAdapter;
import com.ritssupreme.phlurtyz002.core.BaseActivity;
import com.ritssupreme.phlurtyz002.fragment.CategoryFragment;
import com.ritssupreme.phlurtyz002.model.Advertisment;
import com.ritssupreme.phlurtyz002.model.CategoryAsset;
import com.ritssupreme.phlurtyz002.model.CategoryView;
import com.ritssupreme.phlurtyz002.service.AdvertismentService;
import com.ritssupreme.phlurtyz002.service.PPCService;
import com.ritssupreme.phlurtyz002.utils.ApiEndPoints;
import com.ritssupreme.phlurtyz002.utils.Prefs;
import com.ritssupreme.phlurtyz002.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private ImageView adImage;

    private TextView removeAd;

    private ViewGroup adView;

    private static final String PNG = ".png";

    private static final String ITEM_NAME_KEY="itemNames";

    private static final String IMAGE_PATH_KEY="imagePaths";

    private static final int AD_DURATION =10000;

    private List<Advertisment> advertisments = new ArrayList<>();

    private boolean isNetworkChangeReceiverRegistered = false;

    private Handler handler = new Handler();

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = this;

        Prefs prefs = new Prefs(this);

        prefs.open();

        if(prefs.isFirstTime()){

            Utils.copyAssets(this);

        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager, "");

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        adImage = (ImageView)findViewById(R.id.ad_image);

        removeAd = (TextView)findViewById(R.id.remove_ad);

        adView = (RelativeLayout)findViewById(R.id.ad_view);

        adView.setVisibility(View.GONE);

       // fetchAds();

        adImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Advertisment advertisment =(Advertisment)v.getTag();

                PPCService.getInstance().registerClick(advertisment)

                        .subscribeOn(Schedulers.io())

                        .observeOn(AndroidSchedulers.mainThread())

                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onCompleted() {

                                Log.d("Complete","Completed");

                                Intent intent = new Intent();

                                intent.setAction(Intent.ACTION_VIEW);

                                intent.addCategory(Intent.CATEGORY_BROWSABLE);

                                intent.setData(Uri.parse(advertisment.getUrl()));

                                startActivity(intent);

                            }

                            @Override
                            public void onError(Throwable e) {

                                Log.d("Error",e.toString());

                            }

                            @Override
                            public void onNext(Boolean success) {


                            }
                        });

            }
        });

        removeAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adView.removeAllViews();

            }
        });

    }

    public boolean isOnline(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Network Status","CONNECTED");

            updateAds();

        }
    };

    public void fetchAds() {

        registerReceiver(networkChangeReceiver,new IntentFilter("CONNECTED"));

        isNetworkChangeReceiverRegistered = true;

        AdvertismentService.getInstance().getAllAdvertisments()

                .subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<List<Advertisment>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Advertisment> _advertisments) {

                        advertisments = _advertisments;

                        initAdView();

                    }
                });

    }

    public void updateAds(){

        AdvertismentService.getInstance().getAllAdvertisments()

                .subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<List<Advertisment>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Advertisment> _advertisments) {

                        advertisments = _advertisments;

                    }
                });

    }

    private Runnable updateAdClockTask = new Runnable() {
        @Override
        public void run() {

            if(advertisments != null && !advertisments.isEmpty()) {

                Random r = new Random();

                int randomAd = r.nextInt(advertisments.size());

                populateAd(advertisments.get(randomAd));

                handler.postDelayed(this,AD_DURATION);

            }
        }
    };

    public void initAdView() {

        if(advertisments != null && !advertisments.isEmpty()) {

            if(handler != null){

                handler.removeCallbacks(updateAdClockTask);
            }

            handler.postDelayed(updateAdClockTask, AD_DURATION);

        }
    }

    public void populateAd(Advertisment advertisment){

        Picasso.with(this).load(ApiEndPoints.HOST + ApiEndPoints.UPLOAD_DIRECTORY + advertisment.getImage()).into(adImage, new Callback() {

            @Override
            public void onSuccess() {

                if(adView.getVisibility() != View.VISIBLE){

                    adView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onError() {

                System.out.print("Error");
            }
        });

        adImage.setTag(advertisment);

    }


    private void setupViewPager(ViewPager viewPager, final String filterText) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Map<String, CategoryView> categoryViewMap = new HashMap<>();

            try {

                FilenameFilter filenameFilter =new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String fileName) {


                            if(!filterText.isEmpty() && fileName.endsWith(PNG)){
                                if(fileName.toLowerCase().replaceAll(" ", "_").contains(filterText.toLowerCase().replaceAll(" ", "_"))  || fileName.equalsIgnoreCase(filterText.toLowerCase())){
                                    return true;
                                }
                            }else{
                                if (fileName.endsWith(PNG)) {
                                    return true;
                                }
                            }

                        return false;
                    }
                };

                String[] imgPaths = this.getFilesDir().list(filenameFilter);

                for(String imgPath : imgPaths){

                        String[] fileNameWithCategory = imgPath.split("-");

                        String[] fileNameWithExt = fileNameWithCategory[1].split("\\.");

                        String categoryName = fileNameWithCategory[0];

                        String fileName = WordUtils.capitalizeFully(fileNameWithExt[0].replaceAll("_", " "));

                        CategoryView categoryView = new CategoryView();

                        CategoryAsset asset = new CategoryAsset();

                        asset.setAssetName(fileName);

                        asset.setAssetPath(imgPath);

                        if(!categoryViewMap.containsKey(categoryName)){

                            categoryView.setCategoryName(categoryName);

                            List<CategoryAsset> assets = new ArrayList<>();

                            assets.add(asset);

                            categoryView.setCategoryAssetList(assets);

                            categoryViewMap.put(categoryName, categoryView);

                        }else {

                            categoryViewMap.get(categoryName).getCategoryAssetList().add(asset);

                        }

                }

            } catch (Exception e) {

                e.printStackTrace();
            }

        for (Map.Entry<String, CategoryView> entry : categoryViewMap.entrySet()) {

            Bundle bundle = new Bundle();

            ArrayList<String> itemNames = new ArrayList<>();

            ArrayList<String> imagePaths = new ArrayList<>();

            for (CategoryAsset asset : entry.getValue().getCategoryAssetList()) {

                itemNames.add(asset.getAssetName());

                imagePaths.add(asset.getAssetPath());

            }

            bundle.putStringArrayList(ITEM_NAME_KEY, itemNames);

            bundle.putStringArrayList(IMAGE_PATH_KEY, imagePaths);

            CategoryFragment fragment = new CategoryFragment();

            fragment.setArguments(bundle);

            adapter.addFragment(fragment, entry.getValue().getCategoryName());

        }

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }





    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();
        }
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

                setupViewPager(viewPager, newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                setupViewPager(viewPager, query);
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_categories) {

        } else if (id == R.id.nav_favorites) {

            startActivity(new Intent(MainActivity.this, FavoriteActivity.class));

//        }
//        else if (id == R.id.nav_special) {
//
//            startActivity(new Intent(MainActivity.this, SpecialsActivity.class));

        } else if (id == R.id.nav_recent) {

            startActivity(new Intent(MainActivity.this, RecentModActivity.class));

        } else if (id == R.id.nav_suggest) {

            startActivity(new Intent(MainActivity.this, SuggestionActivity.class));

        }
//        else if (id == R.id.nav_unlock) {
//
//            startActivity(new Intent(MainActivity.this, DownloadActivity.class));
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onResume() {

        super.onResume();

        handler.postDelayed(updateAdClockTask,AD_DURATION);
    }

    @Override
    protected void onPause() {

        super.onPause();

        handler.removeCallbacks(updateAdClockTask);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if(isNetworkChangeReceiverRegistered){

            unregisterReceiver(networkChangeReceiver);

            isNetworkChangeReceiverRegistered = false;

        }
    }
}
