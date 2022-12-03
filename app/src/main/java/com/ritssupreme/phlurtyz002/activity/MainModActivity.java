package com.ritssupreme.phlurtyz002.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.ritssupreme.phlurty002.broadcastReceiver.NetworkChangeReceiver;
import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.adapter.ViewPagerAdapter;
import com.ritssupreme.phlurtyz002.api.RetrofitClientInstance;
import com.ritssupreme.phlurtyz002.api.interfaces.ApiCallback;
import com.ritssupreme.phlurtyz002.api.interfaces.ClickAdvert;
import com.ritssupreme.phlurtyz002.api.interfaces.GetForAllAdverts;
import com.ritssupreme.phlurtyz002.api.interfaces.GetForAllCategories;
import com.ritssupreme.phlurtyz002.api.models.AllAdverts;
import com.ritssupreme.phlurtyz002.api.models.AllCategory;
import com.ritssupreme.phlurtyz002.api.models.CLickAdvertsRequest;
import com.ritssupreme.phlurtyz002.api.models.CLickAdvertsResponse;
import com.ritssupreme.phlurtyz002.core.BaseActivity;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment10;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment2;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment3;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment4;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment5;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment6;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment7;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment8;
import com.ritssupreme.phlurtyz002.fragment.MainModCategoryFragment9;
import com.ritssupreme.phlurtyz002.model.Advertisment;
import com.ritssupreme.phlurtyz002.service.AdvertismentService;
import com.ritssupreme.phlurtyz002.utils.ApiEndPoints;
import com.ritssupreme.phlurtyz002.utils.Helpers;
import com.ritssupreme.phlurtyz002.utils.Prefs;
import com.ritssupreme.phlurtyz002.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainModActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static ArrayList<AllCategory> fetchedData;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private ImageView adImage;

    private TextView removeAd;

    private ViewGroup adView;

    private static final String PNG = ".png";

    private static final String ITEM_NAME_KEY="itemNames";

    private static final String IMAGE_PATH_KEY="imagePaths";

    private static final int AD_DURATION = 20000;

    private List<Advertisment> advertisments = new ArrayList<>();

    private boolean isNetworkChangeReceiverRegistered = false;

    private Handler handler = new Handler();

//    final List<Banner> bnnerFromNetworks = new ArrayList<>();

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

        getCategories();

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        adImage = (ImageView)findViewById(R.id.ad_image);



        removeAd = (TextView)findViewById(R.id.remove_ad);

        adView = (RelativeLayout)findViewById(R.id.ad_view);

        Timer timerObj = new Timer();

//        if(bnnerFromNetworks.size() > 0 && adView.getVisibility() == View.VISIBLE) {
//            timerObj.cancel();
//            System.out.println("------------------we stop");
//        }else{
            TimerTask timerTaskObj = new TimerTask() {
                public void run() {
                    //perform your action here
                    fetchAds();
                //    System.out.println("------------------we run");
                }
            };
            timerObj.schedule(timerTaskObj, 0, AD_DURATION);
//            System.out.println("------------------we move");
//        }





        adImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Advertisment advertisment =(Advertisment)v.getTag();

//                PPCService.getInstance().registerClick(advertisment)
//
//                        .subscribeOn(Schedulers.io())
//
//                        .observeOn(AndroidSchedulers.mainThread())
//
//                        .subscribe(new Observer<Boolean>() {
//                            @Override
//                            public void onCompleted() {
//
//                                Log.d("Complete","Completed");
//
//                                Intent intent = new Intent();
//
//                                intent.setAction(Intent.ACTION_VIEW);
//
//                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
//
//                                intent.setData(Uri.parse(advertisment.getUrl()));
//
//                                startActivity(intent);
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                                Log.d("Error",e.toString());
//
//                            }
//
//                            @Override
//                            public void onNext(Boolean success) {
//
//
//                            }
//                        });

            }
        });

        removeAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                adView.removeAllViews();
                adView.setVisibility(View.GONE);

            }
        });

    }

    private void doBnners() {


        Call<AllAdverts> call = RetrofitClientInstance.getRetrofitFlirtyInstanceTwo().create(GetForAllAdverts.class).getAllData(Helpers.ADVERTISMENT_URL + "getRandom");
        call.enqueue(new retrofit2.Callback<AllAdverts>() {
            @Override
            public void onResponse(Call<AllAdverts> call, Response<AllAdverts> response) {

                if (response.isSuccessful()) {
                    // Do your success stuff...
                    if (response.code() == 200) {
                        final List<AllAdverts.Datum> result = response.body().getData();
                        if (result != null){
                       //     bnnerFromNetworks.clear();
                            //BannerLayout banner=(BannerLayout) findViewById(R.id.Banner);



                            for(final AllAdverts.Datum bannerImg : result){
//                                Banner bannerM = new Banner();
//                                bannerM.setUrl(bannerImg.url);
//                                bannerM.setId(bannerImg.id);
//                                bannerM.setName(bannerImg.name);
//                                bannerM.setImage(ApiEndPoints.ADVERTISMENT_BANNER + bannerImg.file);
//                                bnnerFromNetworks.add(bannerM);

                                ////////////////

                                Picasso.with(MainModActivity.this).load(ApiEndPoints.ADVERTISMENT_BANNER + bannerImg.file).into(adImage, new Callback() {

                                    @Override
                                    public void onSuccess() {

                                        if(adView.getVisibility() != View.VISIBLE){

                                            adView.setVisibility(View.VISIBLE);

                                        }
                                        adImage.setVisibility(View.VISIBLE);
                                        removeAd.setVisibility(View.GONE);
                                        adImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String bannerWebisteUrl = bannerImg.url;
                                                String bannerId = bannerImg.id;

                                                if (!bannerWebisteUrl.startsWith("http://") && !bannerWebisteUrl.startsWith("https://"))
                                                    bannerWebisteUrl = "https://" + bannerWebisteUrl;

                                                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bannerWebisteUrl));

                                                //start click call

                                                CLickAdvertsRequest cLickAdvertsRequest = new CLickAdvertsRequest();
                                                Call<CLickAdvertsResponse> call = RetrofitClientInstance.getRetrofitFlirtyInstanceTwo().create(ClickAdvert.class).create(cLickAdvertsRequest, Helpers.ADVERTISMENT_URL + "click/" + Integer.valueOf(bannerId));
                                                call.enqueue(new retrofit2.Callback<CLickAdvertsResponse>() {
                                                    @Override
                                                    public void onResponse(Call<CLickAdvertsResponse> call, Response<CLickAdvertsResponse> response) {
                                                       // bnnerFromNetworks.clear();
                                                        adView.setVisibility(View.GONE);
                                                        startActivity(browserIntent);
                                                    }

                                                    @Override
                                                    public void onFailure(Call<CLickAdvertsResponse> call, Throwable t) {
                                                        Log.e("onFailure", "onFailure: ", t);
                                                        call.cancel();
                                                      //  bnnerFromNetworks.clear();
                                                        adView.setVisibility(View.GONE);
                                                        startActivity(browserIntent);
                                                    }
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError() {

                                        System.out.print("Error");
                                    }
                                });
                                /////////////////
                            }


//                            List<String> urlBnners = new ArrayList<>();
//                            for(Banner bannerFullObj:  bnnerFromNetworks){
//                                urlBnners.add(bannerFullObj.getImage());
//                            }

//                            BaseBannerAdapter webBannerAdapter=new BaseBannerAdapter(MainModActivity.this, urlBnners);
//                            webBannerAdapter.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
//                                @Override
//                                public void onItemClick(int position)
//                                {
//                                    String bannerWebisteUrl = bnnerFromNetworks.get(position).getUrl();
//                                    String bannerId = bnnerFromNetworks.get(position).getId();
//
//                                    if (!bannerWebisteUrl.startsWith("http://") && !bannerWebisteUrl.startsWith("https://"))
//                                        bannerWebisteUrl = "https://" + bannerWebisteUrl;
//
//                                    final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bannerWebisteUrl));
//
//                                    //start click call
//
//                                    CLickAdvertsRequest cLickAdvertsRequest = new CLickAdvertsRequest();
//                                    Call<CLickAdvertsResponse> call = RetrofitClientInstance.getRetrofitFlirtyInstanceTwo().create(ClickAdvert.class).create(cLickAdvertsRequest, Helpers.ADVERTISMENT_URL + "click/" + Integer.valueOf(bannerId));
//                                    call.enqueue(new retrofit2.Callback<CLickAdvertsResponse>() {
//                                        @Override
//                                        public void onResponse(Call<CLickAdvertsResponse> call, Response<CLickAdvertsResponse> response) {
//                                            bnnerFromNetworks.clear();
//                                            adView.setVisibility(View.GONE);
//                                            startActivity(browserIntent);
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<CLickAdvertsResponse> call, Throwable t) {
//                                            Log.e("onFailure", "onFailure: ", t);
//                                            call.cancel();
//                                            bnnerFromNetworks.clear();
//                                            adView.setVisibility(View.GONE);
//                                            startActivity(browserIntent);
//                                        }
//                                    });
//
//                                    //end click call
//
//                                }
//                            });


                        //    adView.setVisibility(View.VISIBLE);

//                            if(banner != null){
//                                banner.setAdapter(webBannerAdapter);
//                            }

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    removeAd.setVisibility(View.VISIBLE);
                                }
                            }, AD_DURATION / 2);
                        }else{
                            System.out.println("null result");
                        }

                    }else{
                        System.out.println("result not 200");
                    }

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        System.out.println(jObjError.getJSONObject("error").getString("message"));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AllAdverts> call, Throwable t) {
                 Log.e("onFailure", "onFailure: ", t);
                call.cancel();
            }
        });
    }

    public void getCategories(){
        getCategoriesFromApi(MainModActivity.this, new ApiCallback() {
            @Override
            public void onResponse(boolean success) {
                if(success){
                    setupViewPager(viewPager, "");
                }else {

                    AlertDialog alertDialog = new AlertDialog.Builder(MainModActivity.this).create();
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    getCategories();
                                }
                            });

                    if(isOnline(MainModActivity.this)){
                        alertDialog.setTitle("Oops!");
                        alertDialog.setMessage("Please check your internet connectivity and try again.");
                        alertDialog.show();
                    }else{
                        alertDialog.setTitle("Oops!");
                        alertDialog.setMessage("We could not get any emogi at this time.");
                        alertDialog.show();
                    }


                }
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

            doBnners();


//            if(advertisments != null && !advertisments.isEmpty()) {
//
//                Random r = new Random();
//
//                int randomAd = r.nextInt(advertisments.size());
//
//                populateAd(advertisments.get(randomAd));

                handler.postDelayed(this,AD_DURATION);

//            }
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

    private void setupViewPager(final ViewPager viewPager, final String filterText) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        int i = 0;
        for (ListIterator<AllCategory> fetched = fetchedData.listIterator(); fetched.hasNext(); i++) {
           AllCategory allCategory = fetched.next();
            Bundle bundle = new Bundle();
            if(i == 0){
                MainModCategoryFragment mainModCategoryFragment = new MainModCategoryFragment();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment, allCategory.getName());
            }

            if(i == 1){
                MainModCategoryFragment2 mainModCategoryFragment2 = new MainModCategoryFragment2();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment2.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment2, allCategory.getName());
            }

            if(i == 2){
                MainModCategoryFragment3 mainModCategoryFragment3 = new MainModCategoryFragment3();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment3.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment3, allCategory.getName());
            }

            if(i == 3){
                MainModCategoryFragment4 mainModCategoryFragment4 = new MainModCategoryFragment4();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment4.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment4, allCategory.getName());
            }

            if(i == 4){
                MainModCategoryFragment5 mainModCategoryFragment5 = new MainModCategoryFragment5();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment5.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment5, allCategory.getName());
            }

            if(i == 5){
                MainModCategoryFragment6 mainModCategoryFragment6 = new MainModCategoryFragment6();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment6.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment6, allCategory.getName());
            }

            if(i == 6){
                MainModCategoryFragment7 mainModCategoryFragment7 = new MainModCategoryFragment7();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment7.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment7, allCategory.getName());
            }

            if(i == 7){
                MainModCategoryFragment8 mainModCategoryFragment8 = new MainModCategoryFragment8();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment8.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment8, allCategory.getName());
            }

            if(i == 8){
                MainModCategoryFragment9 mainModCategoryFragment9 = new MainModCategoryFragment9();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment9.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment9, allCategory.getName());
            }

            if(i == 9){
                MainModCategoryFragment10 mainModCategoryFragment10 = new MainModCategoryFragment10();
                bundle.putString("filterText", filterText);
                bundle.putString("categoryId", String.valueOf(allCategory.getId()));

                mainModCategoryFragment10.setArguments(bundle);
                adapter.addFragment(mainModCategoryFragment10, allCategory.getName());
            }

        }

        if(!filterText.isEmpty()){
            adapter.notifyDataSetChanged();
        }
        viewPager.setAdapter(adapter);


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

            startActivity(new Intent(MainModActivity.this, FavoriteActivity.class));

//        } else if (id == R.id.nav_special) {
//
//            startActivity(new Intent(MainModActivity.this, SpecialsActivity.class));

        } else if (id == R.id.nav_recent) {

            startActivity(new Intent(MainModActivity.this, RecentActivity.class));

        } else if (id == R.id.nav_suggest) {

            startActivity(new Intent(MainModActivity.this, SuggestionActivity.class));

        }
//        else if (id == R.id.nav_unlock) {
//
//            startActivity(new Intent(MainModActivity.this, DownloadActivity.class));
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

    public static void getCategoriesFromApi(final Context context, final ApiCallback callback){
        Call<List<AllCategory>> call = RetrofitClientInstance.getRetrofitFlirtyInstance().create(GetForAllCategories.class).getAllData(ApiEndPoints.CategoryFree);

        call.enqueue(new retrofit2.Callback<List<AllCategory>>() {
            @Override
            public void onResponse(Call<List<AllCategory>> call, Response<List<AllCategory>> response) {

                if (response.isSuccessful()) {

                    ArrayList<AllCategory> imageObjects = new ArrayList<>();

                    for(AllCategory fetchd : response.body()) {
                        AllCategory allCategory = new AllCategory();
                        allCategory.setName(fetchd.getName());
                        allCategory.setId(fetchd.getId());

                        imageObjects.add(allCategory);
                    }
                    fetchedData = imageObjects;


                    if(response.body().size() < 1){
                        Toast.makeText(context, "No categories found.", Toast.LENGTH_SHORT).show();
                    }

                    callback.onResponse(response.body() != null);
                }else{
                    Toast.makeText(context, "An error occurred while fetching categories." + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
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


}
