package com.example.razrs.googlenow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.razrs.googlenow.R;
import com.ford.syncV4.proxy.SyncProxyALM;

/**
 * Created by razrs on 5/20/2015.
 */
public class LockScreenActivity extends Activity{
    private static LockScreenActivity instance;

    static {
        LockScreenActivity.instance = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    }

    // Disable back button on lockscreen
    @Override
    public void onBackPressed() {
    }

    @Override
    public void onDestroy() {
        LockScreenActivity.instance = null;
        super.onDestroy();
    }

    public void exit() {
        super.finish();
    }

    public static LockScreenActivity getInstance() {
        return instance;
    }
}
