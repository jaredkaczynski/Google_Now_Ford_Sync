package com.example.razrs.googlenow.applink;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.exception.SyncExceptionCause;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.*;
import com.ford.syncV4.proxy.rpc.enums.LockScreenStatus;
import com.ford.syncV4.proxy.rpc.enums.SyncDisconnectedReason;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by razrs on 5/20/2015.
 */
public class AppLinkService extends Service implements IProxyListenerALM {
    // variable used to increment correlation ID for every request sent to SYNC
    public int autoIncCorrId = 0;
    // variable to contain the current state of the service
    private static AppLinkService instance = null;
    // variable to access the BluetoothAdapter
    private BluetoothAdapter mBtAdapter;
    // variable to create and call functions of the SyncProxy
    private SyncProxyALM proxy = null;

    // Service shutdown timing constants
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final int STOP_SERVICE_DELAY = 5000;
    private Handler mHandler = new Handler();
    /**
     * Runnable that stops this service if there hasn't been a connection to SYNC
     * within a reasonable amount of time since ACL_CONNECT.
     */
    private Runnable mCheckConnectionRunnable = new Runnable() {
        @Override
        public void run() {
            Boolean stopService = true;
            // If the proxy has connected to SYNC, do NOT stop the service
            if (proxy != null && proxy.getIsConnected()) {
                stopService = false;
            }
            if (stopService) {
                mHandler.removeCallbacks(mCheckConnectionRunnable);
                mHandler.removeCallbacks(mStopServiceRunnable);
                stopSelf();
            }
        }
    };

    /**
     * Queue's a runnable that stops the service after a small delay,
     * unless the proxy manages to reconnects to SYNC.
     */
    public void stopService() {
        mHandler.removeCallbacks(mStopServiceRunnable);
        mHandler.postDelayed(mStopServiceRunnable, STOP_SERVICE_DELAY);
    }

    /**
     * Runnable that stops this service on ACL_DISCONNECT after a short time delay.
     * This is a workaround until some synchronization issues are fixed within the proxy.
     */
    private Runnable mStopServiceRunnable = new Runnable() {
        @Override
        public void run() {
            // As long as the proxy is null or not connected to SYNC, stop the service
            if (proxy == null || !proxy.getIsConnected()) {
                mHandler.removeCallbacks(mCheckConnectionRunnable);
                mHandler.removeCallbacks(mStopServiceRunnable);
                stopSelf();
            }
        }
    };

    public static AppLinkService getInstance() {
        return instance;
    }

    public SyncProxyALM getProxy() {
        return proxy;
    }

    @Override
    public void onOnHMIStatus(OnHMIStatus onHMIStatus) {

    }

    @Override
    public void onProxyClosed(String info, Exception e, SyncDisconnectedReason reason) {
        LockScreenManager.clearLockScreen();

        if ((((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.SYNC_PROXY_CYCLED)) {
            if (((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.BLUETOOTH_DISABLED) {
                Log.v(AppLinkApplication.TAG, "reset proxy in onproxy closed");
                reset();
            }
        }
    }

    public void reset() {
        if (proxy != null) {
            try {
                proxy.resetProxy();
            } catch (SyncException e1) {
                e1.printStackTrace();
                //something goes wrong, & the proxy returns as null, stop the service.
                // do not want a running service with a null proxy
                if (proxy == null) {
                    stopSelf();
                }
            }
        } else {
            startProxy();
        }
    }

    public void startProxy() {
        if (proxy == null) {
            try {
                proxy = new SyncProxyALM(this, "Hello AppLink", true, "438316430");
            } catch (SyncException e) {
                e.printStackTrace();
                // error creating proxy, returned proxy = null
                if (proxy == null) {
                    stopSelf();
                }
            }
        }
    }

    @Override
    public void onOnDriverDistraction(OnDriverDistraction notification) {
    }

    @Override
    public void onError(String info, Exception e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onGenericResponse(GenericResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOnCommand(OnCommand notification) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAddCommandResponse(AddCommandResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAddSubMenuResponse(AddSubMenuResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCreateInteractionChoiceSetResponse(
            CreateInteractionChoiceSetResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAlertResponse(AlertResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeleteCommandResponse(DeleteCommandResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeleteInteractionChoiceSetResponse(
            DeleteInteractionChoiceSetResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPerformInteractionResponse(PerformInteractionResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResetGlobalPropertiesResponse(
            ResetGlobalPropertiesResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
    }

    @Override
    public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onShowResponse(ShowResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSpeakResponse(SpeakResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOnButtonEvent(OnButtonEvent notification) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOnButtonPress(OnButtonPress notification) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOnPermissionsChange(OnPermissionsChange notification) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOnTBTClientState(OnTBTClientState notification) {
        // TODO Auto-generated method stub
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUnsubscribeVehicleDataResponse(
            UnsubscribeVehicleDataResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetVehicleDataResponse(GetVehicleDataResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadDIDResponse(ReadDIDResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetDTCsResponse(GetDTCsResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnVehicleData(OnVehicleData notification) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnAudioPassThru(OnAudioPassThru notification) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPutFileResponse(PutFileResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeleteFileResponse(DeleteFileResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onListFilesResponse(ListFilesResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSetAppIconResponse(SetAppIconResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScrollableMessageResponse(ScrollableMessageResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnLanguageChange(OnLanguageChange notification) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSliderResponse(SliderResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDiagnosticMessageResponse(DiagnosticMessageResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnHashChange(OnHashChange arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnKeyboardInput(OnKeyboardInput arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnSystemRequest(OnSystemRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnTouchEvent(OnTouchEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSystemRequestResponse(SystemRequestResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnLockScreenNotification(OnLockScreenStatus notification) {
        LockScreenStatus displayLockScreen = notification.getShowLockScreen();
        // Show lockscreen in both REQUIRED and OPTIONAL
        //if (displayLockScreen == LockScreenStatus.REQUIRED || displayLockScreen == LockScreenStatus.OPTIONAL) {
        //Show lockscreen in only REQUIRED
        if (displayLockScreen == LockScreenStatus.REQUIRED) {
            LockScreenManager.showLockScreen();
        } else {
            LockScreenManager.clearLockScreen();
        }
    }


}
