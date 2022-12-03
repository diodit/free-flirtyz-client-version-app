package com.ritssupreme.phlurtyz002.core;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.ritssupreme.phlurtyz002.utils.Prefs;
import com.ritssupreme.phlurtyz002.utils.Utils;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        d("< -------------------- onCreate(Bundle) -------------------- >");
    }

    @Override
    protected void onStart() {
        super.onStart();
        d("< -------------------- onStart() -------------------- >");
    }

    @Override
    protected void onResume() {
        super.onResume();
        d("< -------------------- onResume() -------------------- >");
    }

    @Override
    protected void onPause() {
        super.onPause();
        d("< -------------------- onPause() -------------------- >");
    }

    @Override
    protected void onStop() {
        super.onStop();
        d("< -------------------- onStop() -------------------- >");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        d("< -------------------- onDestroy() -------------------- >");
    }

    protected Prefs getPrefs() {
        return new Prefs(this);
    }

    protected Prefs getPrefs(String name) {
        return new Prefs(this, name);
    }

    public void d(String message) {
        Utils.d(this, message);
    }

    public void e(String message) {
        Utils.e(this, message);
    }

    public void toast(String message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    protected void toast(String message, int length) {
        Toast.makeText(this, message, length).show();
    }
}
