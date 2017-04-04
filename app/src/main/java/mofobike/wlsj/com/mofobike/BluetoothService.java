package mofobike.wlsj.com.mofobike;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import mofobike.wlsj.com.mofobike.manager.BlueToothManager;
import mofobike.wlsj.com.mofobike.manager.BlueUnlockManager;


public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";
    public static final String KEY_MAC_ADD = "key_mac_add";
    public static final String KEY_ACTION = "key_action";

    public static final int ACTION_CONNECT = 0x01;
    public static final int ACTION_UNLOCK = 0x02;
    public static final int ACTION_DISCONNECT = 0x03;


    private String mMacAdd;
    private int mAction;

    private BlueUnlockManager mBlueUnlockManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        mAction = intent.getIntExtra(KEY_ACTION, 0);
        createManager();
        if (mAction == ACTION_CONNECT) {
            mMacAdd = intent.getStringExtra(KEY_MAC_ADD);
            mBlueUnlockManager.scan(mMacAdd);
        }
        if (mAction == ACTION_UNLOCK) {
            BlueToothManager.sendUnlockCmd(this);
        }
        if (mAction == ACTION_DISCONNECT) {
            mBlueUnlockManager.disConnect();
        }
        return super.onStartCommand(intent, flags, startId);

    }

    private void createManager() {
        if (mBlueUnlockManager == null) {
            mBlueUnlockManager = new BlueUnlockManager(this, new BlueUnlockManager.IOnUnlockListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailed() {

                }
            });
        }
    }


}
