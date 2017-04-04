package mofobike.wlsj.com.mofobike.manager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.conn.BleGattCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.ListScanCallback;
import com.clj.fastble.utils.HexUtil;

import java.util.ArrayList;

import mofobike.wlsj.com.mofobike.BluetoothActionReceiver;
import mofobike.wlsj.com.mofobike.BluetoothService;

public class BlueToothManager {
    private BleManager mBleManager;
    public static final String TAG = "BlueToothManager";
    public static final String BLUE_TEST_MAC = "18:7A:93:01:00:03";

    public static String blueMac(Context context) {
        return context.getSharedPreferences("sp", Context.MODE_PRIVATE)
                .getString("mac", BLUE_TEST_MAC);
    }


    public static void setBlueMac(Context context, String mac) {
        context.getSharedPreferences("sp", Context.MODE_PRIVATE).edit().putString("mac", mac).apply();
    }

    private static final byte[] UNLOCK_CMD = {(byte) 0xaa, (byte) 0x55, (byte) 0x0d, (byte) 0x0a};

    private static final String SERVCIE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";

    private static final String CHARATER_ISTIC_READ = "0000fff1-0000-1000-8000-00805f9b34fb";
    private static final String CHARATER_ISTIC_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";


    private boolean isConected;

    protected BluetoothGatt mBluetoothGatt;
    private ArrayList<BluetoothGattCharacteristic> mCharateristic = new ArrayList<>();
    private Context mContext;
    private String mMac;
    private Handler mHanlder = new Handler(Looper.getMainLooper());


    public BlueToothManager(Context context) {
        mBleManager = new BleManager(context);
        this.mContext = context;
    }


    public void scan(final String mac) {
        mMac = mac;
        Log.i(TAG, "unlock:" + mac);
        if (mMac == null || mMac.length() == 0) {
            mMac = blueMac(mContext);
        }
        BluetoothLogManager.log(TAG, "scan devices,filter mac :" + mac);
        mBleManager.scanDevice(new ListScanCallback(10000) {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                super.onLeScan(device, rssi, scanRecord);
                if (!isConected) {
                    BluetoothLogManager.log(TAG, "found devices：" + device.getName() + "," + device.getAddress());
                }
                if (device.getAddress().equals(mMac)) {
                    connectDevice(device);
                }
            }

            @Override
            public void onDeviceFound(final BluetoothDevice[] devices) {
                Log.i(TAG, "onDeviceFound():" + devices.length);
                if (isConected) {
                    return;
                }
                for (BluetoothDevice device : devices) {
                    Log.i(TAG, "device:" + device.getName() + "," + device.toString());

                    if (device.getAddress().equals(mMac)) {
                        connectDevice(device);
                        break;
                    }
                }

                Intent intent = new Intent(BluetoothActionReceiver.ACTION_CONNECT_FAILED);
                mContext.sendBroadcast(intent);


            }

        });
    }


    public void connectDevice(BluetoothDevice device) {
        if (isConected) {
            return;
        }
        BluetoothLogManager.log(TAG, "connectDevice：" + device.getName() + "," + device.getAddress());
        isConected = true;
        Log.i(TAG, "connectDevice:" + device.getName() + "," + device.getAddress());
        mBleManager.connectDevice(device, true, new BleGattCallback() {
            @Override
            public void onNotFoundDevice() {
                Log.i(TAG, "onNotFoundDevice()");

            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                Log.i(TAG, "onConnectSuccess");
                gatt.discoverServices();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                mBluetoothGatt = gatt;
                Log.i(TAG, "onServicesDiscovered():" + status + ",serviceCount:" + gatt.getServices().size());
                for (BluetoothGattService service : gatt.getServices()) {
                    BluetoothLogManager.log(TAG, "onServicesDiscovered：" + service.getUuid());
                    if (service.getUuid().toString().equals(SERVCIE_UUID)) {
                        Log.i(TAG, "onServicesDiscovered():" + service.getType() + "," + service.getUuid()
                                + ",characteristic.size:" + service.getCharacteristics().size());
                        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            Log.i(TAG, "BluetoothGattCharacteristic:" + characteristic.getUuid() + "," + characteristic.getProperties());
                            BluetoothLogManager.log(TAG, "characteristic uuid："
                                    + characteristic.getUuid() + ",properties:" + characteristic.getProperties());
                            if (characteristic.getUuid().toString().equals(CHARATER_ISTIC_READ)
                                    || characteristic.getUuid().toString().equals(CHARATER_ISTIC_WRITE)) {
                                mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                                mCharateristic.add(characteristic);
                            }


                        }


                        mHanlder.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendUnlockCmd();
                            }
                        }, 200);
                        break;

                    }
                }
            }

            @Override
            public void onConnectFailure(BleException exception) {
                Log.i(TAG, "onConnectFailure():" + exception.getDescription());
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.i(TAG, "onCharacteristicRead():" + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                super.onCharacteristicRead(gatt, characteristic, status);
                onDataRec(gatt, characteristic, status);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.i(TAG, "onDescriptorWrite():" + String.valueOf(HexUtil.encodeHex(descriptor.getValue())));
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.i(TAG, "onCharacteristicWrite().buffer:" + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                onCmdWrite(characteristic);


            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);

                BluetoothDataManager.onBluetoothDateRec(characteristic.getValue());
                Log.i(TAG, "onCharacteristicChanged():" + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));

            }

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Log.i(TAG, "onConnectionStateChange()");
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                Log.i(TAG, "onDescriptorRead()");
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
                Log.i(TAG, "onReliableWriteCompleted()");
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
                Log.i(TAG, "onMtuChanged()");
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                Log.i(TAG, "onReadRemoteRssi()");

            }
        });
    }

    private void onCmdWrite(BluetoothGattCharacteristic characteristic) {
        byte[] buf = characteristic.getValue();
        if (buf == null) {
            return;
        }
        if (isUnloackCmd(buf)) {
            sendUnlockSucces();
            mHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readNotifyDevices();
                }
            }, 50);
        }

    }

    private void sendUnlockSucces() {
        Log.i(TAG, "sendUnlockSucces");
        Intent intent = new Intent(BluetoothActionReceiver.ACTION_UNLOACK_SUCCESS);
        mContext.sendBroadcast(intent);
    }

    private boolean isUnloackCmd(byte[] buf) {

        if (buf.length != UNLOCK_CMD.length) {
            Log.i(TAG, "isUnloackCmd(),bug length is not 4");
            return false;
        }

        for (int i = 0; i < buf.length; i++) {
            if (buf[i] != UNLOCK_CMD[i]) {
                return false;
            }
        }
        return true;
    }

    private void onUnloackAck() {
        Log.i(TAG, "onUnloackAck");
    }

    public void sendUnlockCmd() {
        Log.i(TAG, "sendUnlockCmd");

        sendUnloackCmd(mBluetoothGatt, getWriteCharacteristic());
    }

    public void sendUnloackCmd(BluetoothGatt gatt,
                               final BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "sendUnlockCMD");
        BluetoothLogManager.log(TAG, "sendUnlockCmd:" + characteristic.getUuid());
        if (characteristic != null) {
            byte[] cmd = new byte[4];
            cmd[0] = (byte) 0xaa;
            cmd[1] = (byte) 0x55;
            cmd[2] = (byte) 0x0d;
            cmd[3] = (byte) 0x0a;
            boolean status
                    = mBleManager.writeDevice(SERVCIE_UUID, characteristic.getUuid().toString(), cmd, new BleCharacterCallback() {
                @Override
                public void onSuccess(BluetoothGattCharacteristic characteristic) {
                    Log.i(TAG, "writeDevice.onSuccess():" + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                }

                @Override
                public void onFailure(BleException exception) {
                    Log.i(TAG, "onFailure()");

                }
            });

            mHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBleManager.readDevice(SERVCIE_UUID, characteristic.getUuid().toString(), new BleCharacterCallback() {
                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            Log.i(TAG, "readDevice.onSuccess():" + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                        }

                        @Override
                        public void onFailure(BleException exception) {
                            Log.i(TAG, "readDevice().onFailure()");


                        }
                    });
                }
            }, 200);

            mHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBleManager.notify(SERVCIE_UUID, characteristic.getUuid().toString(), new BleCharacterCallback() {
                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            Log.i(TAG, "notify.onSuccess()");
                        }

                        @Override
                        public void onFailure(BleException exception) {
                            Log.i(TAG, "notify.onFailure()");
                        }
                    });
                }
            }, 200);
            Log.i(TAG, "sendUnlockCMD().status:" + status);
            BluetoothLogManager.log(TAG, "sendUnlockCmd:" + HexUtil.encodeHex(cmd) + ",status:" + status);

        }
    }

    public void readNotifyDevices() {

        mBluetoothGatt.setCharacteristicNotification(getNotifyCharacteristic(), true);
        Log.i(TAG, "readNotifyDevices()");
        boolean suc = mBleManager.notify(
                SERVCIE_UUID,
                CHARATER_ISTIC_READ,
                new BleCharacterCallback() {
                    @Override
                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                        Log.i(TAG, "readDevice(),onSuccess:" + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        Log.i(TAG, "readDevice().onFailure: " + exception.getDescription());

                    }
                });
    }

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

        BluetoothDataManager.onBluetoothDateRec(characteristic.getValue());
    }


    protected BluetoothGattCharacteristic getWriteCharacteristic() {
        for (BluetoothGattCharacteristic characteristic : mCharateristic) {
            if (characteristic.getUuid().toString().equals(CHARATER_ISTIC_WRITE)) {
                return characteristic;
            }

        }
        return null;
    }

    protected BluetoothGattCharacteristic getNotifyCharacteristic() {
        for (BluetoothGattCharacteristic characteristic : mCharateristic) {
            if (characteristic.getUuid().toString().equals(CHARATER_ISTIC_READ)) {
                return characteristic;
            }

        }
        return null;
    }


    public void disConnect() {
        Log.i(TAG, "disConnect");
        mBleManager.closeBluetoothGatt();
    }


    public static void connect(Context context, String macAddress) {
        Intent intent = new Intent(context, BluetoothService.class);
        intent.putExtra(BluetoothService.KEY_MAC_ADD, macAddress);
        intent.putExtra(BluetoothService.KEY_ACTION, BluetoothService.ACTION_CONNECT);
        context.startService(intent);
    }

    public static void sendUnlockCmd(Context context) {
        Log.i(TAG, "sendUnlockCmd");
        Intent intent = new Intent(context, BluetoothService.class);
        intent.putExtra(BluetoothService.KEY_ACTION, BluetoothService.ACTION_UNLOCK);
        context.startService(intent);
    }

    public static void disconnect(Context context) {
        Intent intent = new Intent(context, BluetoothService.class);
        intent.putExtra(BluetoothService.KEY_ACTION, BluetoothService.ACTION_DISCONNECT);
        context.startService(intent);
    }
}
