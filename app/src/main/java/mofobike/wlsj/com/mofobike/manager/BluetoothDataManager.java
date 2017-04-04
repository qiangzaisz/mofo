package mofobike.wlsj.com.mofobike.manager;

import android.util.Log;

import java.util.ArrayList;

import mofobike.wlsj.com.mofobike.model.BikeStatus;

public class BluetoothDataManager {


    private static final String TAG = "BluetoothDataManager";
    private static ArrayList<ILockListener> bikeListeners = new ArrayList<>();

    public static void onBluetoothDateRec(byte[] data) {
        if (data.length != 20) {
            Log.e(TAG, "");
        }
        BikeStatus bikeStatus = new BikeStatus();
        bikeStatus.data = data;

        for (ILockListener listener : bikeListeners) {
            listener.onLockStatusRec(bikeStatus);
        }
    }

    public static void register(ILockListener listener) {
        if (!bikeListeners.contains(listener)) {
            bikeListeners.add(listener);
        }
    }

    public static void unresgister(ILockListener listener) {
        bikeListeners.remove(listener);
    }


    public interface ILockListener {

        public void onLockStatusRec(BikeStatus bikeStatus);
    }

}
