package com.example.razrs.googlenow.applink;


import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.proxy.SyncProxyALM;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AppLinkApplication extends Application {
    public static final String TAG = "Hello AppLink"; // Global TAG used in logging
    private static AppLinkApplication instance;
    private static Activity currentUIActivity;

    static {
        instance = null;
    }

    private static synchronized void setInstance(AppLinkApplication app) {
        instance = app;
    }

    public static synchronized AppLinkApplication getInstance() {
        return instance;
    }

    public static synchronized void setCurrentActivity(Activity act) {
        currentUIActivity = act;
    }

    public static synchronized Activity getCurrentActivity() {
        return currentUIActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppLinkApplication.setInstance(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void startSyncProxyService() {
        // Get the local Bluetooth adapter
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // If BT adapter exists, is enabled, and there are paired devices, start service/proxy
        if (mBtAdapter != null) {
            if ((mBtAdapter.isEnabled() && mBtAdapter.getBondedDevices().isEmpty() == false)) {
                Intent startIntent = new Intent(this, AppLinkService.class);
                startService(startIntent);
            }
        }
    }

    // Recycle the proxy
    public void endSyncProxyInstance() {
        AppLinkService serviceInstance = AppLinkService.getInstance();
        if (serviceInstance != null){
            SyncProxyALM proxyInstance = serviceInstance.getProxy();
            // if proxy exists, reset it
            if(proxyInstance != null){
                serviceInstance.reset();
                // if proxy == null create proxy
            } else {
                serviceInstance.startProxy();
            }
        }
    }
    // Stop the AppLinkService
    public void endSyncProxyService() {
        AppLinkService serviceInstance = AppLinkService.getInstance();
        if (serviceInstance != null){
            serviceInstance.stopService();
        }
    }

    public void showAppVersion(Context context) {
        String appMessage = "Google Now Link Beta Info not available";
        String proxyMessage = "Proxy Version Info not available";
        AppLinkService serviceInstance = AppLinkService.getInstance();
        try {
            appMessage = "Google Now Link Version: " +
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.d(AppLinkApplication.TAG, "Can't get package info", e);
        }

        try {
            if (serviceInstance != null){
                SyncProxyALM syncProxy = serviceInstance.getProxy();
                if (syncProxy != null){
                    String proxyVersion = syncProxy.getProxyVersionInfo();
                    if (proxyVersion != null){
                        proxyMessage = "Proxy Version: " + proxyVersion;
                    }
                }
            }
        } catch (SyncException e) {
            Log.d(AppLinkApplication.TAG, "Can't get Proxy Version", e);
            e.printStackTrace();
        }
        new AlertDialog.Builder(context).setTitle("App Version Information")
                .setMessage(appMessage + "\r\n" + proxyMessage)
                .setNeutralButton(android.R.string.ok, null).create().show();
    }

}