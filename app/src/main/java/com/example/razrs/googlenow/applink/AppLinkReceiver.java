package com.example.razrs.googlenow.applink;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

/**
 * Created by razrs on 5/20/2015.
 */
public class AppLinkReceiver {
    public void onReceive(Context context, Intent intent) {
        // Start the AppLinkService on BT connection
        if (intent.getAction().compareTo(BluetoothDevice.ACTION_ACL_CONNECTED) == 0) {
            AppLinkApplication app = AppLinkApplication.getInstance();
            if (app != null) {
                app.startSyncProxyService();
            }
        }
        // Stop the AppLinkService on BT disconnection
        else if (intent.getAction().compareTo(BluetoothDevice.ACTION_ACL_DISCONNECTED) == 0) {
            AppLinkService als = AppLinkService.getInstance();
            AppLinkApplication app = AppLinkApplication.getInstance();
            if (app != null && als != null) {
                app.endSyncProxyService();
            }
        }
        else if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            // signal your service to stop audio playback
        }
    }
}
