package mofobike.wlsj.com.mofobike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothActionReceiver extends BroadcastReceiver {

    public static final String ACTION_CONNECT_FAILED = "action_connect_failed";
    public static final String ACTION_DATA_REFRESHED = "action_data_refreshed";

    public static final String ACTION_UNLOACK_SUCCESS = "action_unlock_success";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_CONNECT_FAILED)) {
            onBluetoothConnectFailed(intent);
        }

        if (action.equals(ACTION_DATA_REFRESHED)) {
            onDataRefreshed(intent);
        }
        if (action.equals(ACTION_UNLOACK_SUCCESS)) {
            onUnlockSuccesss(intent);
        }
    }

    protected void onUnlockSuccesss(Intent intent) {

    }

    protected void onDataRefreshed(Intent intent) {
    }

    protected void onBluetoothConnectFailed(Intent intent) {

    }
}
