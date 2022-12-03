package com.ritssupreme.phlurtyz002.activity;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ritssupreme.phlurty002.broadcastReceiver.NetworkChangeReceiver;
import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.adapter.ViewPagerAdapter;
import com.ritssupreme.phlurtyz002.core.BaseActivity;
import com.ritssupreme.phlurtyz002.fragment.RecentModFragment;
import com.ritssupreme.phlurtyz002.fragment.RecentSpecialModFragment;
import com.ritssupreme.phlurtyz002.model.Advertisment;
import com.ritssupreme.phlurtyz002.utils.Prefs;
import com.ritssupreme.phlurtyz002.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RecentModActivity extends BaseActivity {

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

        setContentView(R.layout.activity_recent_mod);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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

        }
    };




    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();

        RecentModFragment fragment1 = new RecentModFragment();
        RecentSpecialModFragment fragment2 = new RecentSpecialModFragment();

        fragment1.setArguments(bundle);
        fragment2.setArguments(bundle);

        adapter.addFragment(fragment1, "All Categories");
        adapter.addFragment(fragment2, "Special");


        viewPager.setAdapter(adapter);
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();

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


