package com.example.razrs.googlenow.applink;

import com.example.razrs.googlenow.applink.AppLinkApplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by razrs on 5/20/2015.
 */
public class AppLinkActivity extends Activity{
    private boolean activityOnTop;

    /**
     * Activity is moving to the foreground.
     * Set this activity as the current activity and that it is on top.
     * Update the lockscreen.
     */
    @Override
    protected void onResume() {
        super.onResume();
        AppLinkApplication.setCurrentActivity(this);
        activityOnTop = true;
        if (LockScreenManager.getLockScreenStatus()) {
            LockScreenManager.showLockScreen();
        }
    }

    /**
     * Activity becoming partially visible (obstructed by another).
     * Activity is no longer on top.
     */
    @Override
    protected void onPause() {
        activityOnTop = false;
        super.onPause();
    }

    /**
     * Activity is no longer visible on the screen.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        // Set the current activity to null if no other activity has taken the foreground.
        if (AppLinkApplication.getCurrentActivity() == this) {
            AppLinkApplication.setCurrentActivity(null);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public boolean isActivityonTop(){
        return activityOnTop;
    }
}
