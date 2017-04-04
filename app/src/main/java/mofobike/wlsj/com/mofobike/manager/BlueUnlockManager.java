package mofobike.wlsj.com.mofobike.manager;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;

public class BlueUnlockManager extends BlueToothManager {


    private static final String TAG = "BlueUnlockManager";
    private IOnUnlockListener mListener;


    public BlueUnlockManager(Context context, IOnUnlockListener listener) {
        super(context);
        mListener = listener;
    }

    public void unlock(String mac) {
        scan(mac);
    }


    public void sendUnlockCmd() {
        Log.i(TAG, "sendUnlockCmd");
        sendUnloackCmd(mBluetoothGatt, getWriteCharacteristic());
    }

    public void sendUnloackCmd(BluetoothGatt gatt,
                               BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "sendUnlockCMD");
        if (characteristic != null) {
            byte[] cmd = new byte[4];
            cmd[0] = (byte) 0xaa;
            cmd[1] = (byte) 0x55;
            cmd[2] = (byte) 0x0d;
            cmd[3] = (byte) 0x0a;
            characteristic.setValue(cmd);
            boolean status = gatt.writeCharacteristic(characteristic);
            Log.i(TAG, "sendUnlockCMD().status:" + status);

        }
    }

    @Override
    protected void onDataRec(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        StringBuffer sb = new StringBuffer();
        for (byte b : characteristic.getValue()) {
            sb.append(b).append(",");
        }
        Log.i(TAG, "onCharacteristicRead().buffer:" + sb.toString());

        byte[] data = characteristic.getValue();
        if (data[0] == 0x55 && data[1] == 0xFE) {
            onUnloackAck();
            return;
        }
        super.onDataRec(gatt, characteristic, status);
    }

    public void onUnloackAck() {
        Log.i(TAG, "onUnloackAck");
        if (mListener != null) {
            mListener.onSuccess();
        }
    }


    public interface IOnUnlockListener {
        public void onSuccess();

        public void onFailed();
    }
}
