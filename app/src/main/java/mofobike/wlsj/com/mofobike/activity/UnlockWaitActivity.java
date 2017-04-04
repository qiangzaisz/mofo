package mofobike.wlsj.com.mofobike.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import mofobike.wlsj.com.mofobike.BluetoothActionReceiver;
import mofobike.wlsj.com.mofobike.MainActivity;
import mofobike.wlsj.com.mofobike.R;
import mofobike.wlsj.com.mofobike.manager.BlueToothManager;
import mofobike.wlsj.com.mofobike.manager.BluetoothLogManager;
import mofobike.wlsj.com.mofobike.manager.OneNetApiManager;
import mofobike.wlsj.com.mofobike.model.DataStreamBean;
import mofobike.wlsj.com.mofobike.model.DeviceListBean;


public class UnlockWaitActivity extends Activity implements BluetoothLogManager.ILogListener, OneNetApiManager.IOneNetListener {
    private static final String TAG = "UnlockWaitActivity";
    private AVLoadingIndicatorView mIndicatorView;
    private TextView mStatus;
    private int mAction;
    private String mContent;

    private String mBluetoothMac;
    private String mChinaMobileIEMI;
    private TextView mLogTv;


    private BluetoothActionReceiver mRecevicer = new BluetoothActionReceiver() {
        @Override
        protected void onBluetoothConnectFailed(Intent intent) {
            super.onBluetoothConnectFailed(intent);
            Log.i(TAG, "onBluetoothConnectFailed()");
            mIndicatorView.clearAnimation();
            mStatus.setText("蓝牙连接失败！");
        }

        @Override
        protected void onUnlockSuccesss(Intent intent) {
            super.onUnlockSuccesss(intent);
            Log.i(TAG, "onUnlockSuccesss()");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startLockStatucActivity();
                }
            });

        }
    };

    private void startLockStatucActivity() {
        Intent intent1 = new Intent(UnlockWaitActivity.this, LockStatucActivity.class);
        startActivity(intent1);
        finish();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        mIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.UnlockIndicatorView);
        mStatus = (TextView) findViewById(R.id.UnlockStatus);

        mAction = getIntent().getIntExtra("action", 0);
        mContent = getIntent().getStringExtra("content");


        Uri uri = Uri.parse(mContent);

        mBluetoothMac = uri.getQueryParameter("mac");
        mChinaMobileIEMI = uri.getQueryParameter("mobileimei");

        Log.i(TAG, "mBluetoothMac:" + mBluetoothMac + ",mChinaMobileIEMI:" + mChinaMobileIEMI);


        if (mAction == MainActivity.REQUEST_CODE_BLUETOOTH) {
            Log.i(TAG, "CODE_BLUETOOTH");
            BlueToothManager.connect(this, mBluetoothMac);
            IntentFilter filter = new IntentFilter(BluetoothActionReceiver.ACTION_CONNECT_FAILED);
            filter.addAction(BluetoothActionReceiver.ACTION_UNLOACK_SUCCESS);
            filter.addAction(BluetoothActionReceiver.ACTION_DATA_REFRESHED);
            registerReceiver(mRecevicer, filter);

            mLogTv = (TextView) findViewById(R.id.WaitLog);
            BluetoothLogManager.register(this);
        }


        if (mAction == MainActivity.REQUEST_CODE_RAMOTE) {
            new OneNetApiManager(this, this).sendUnlockCmd(mChinaMobileIEMI);
            findViewById(R.id.WaitDivider).setVisibility(View.GONE);
            Log.i(TAG, "CODE_RAMOTE");
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(mRecevicer);
            BluetoothLogManager.unregister(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void onLogRec(String s) {
        mLogTv.setText(s);
    }

    @Override
    public void onDeviceListRec(DeviceListBean deviceList) {

    }

    @Override
    public void onUnlockSuccess() {
        Toast.makeText(this, "远程解锁成功", Toast.LENGTH_SHORT).show();
        Intent intent1 = new Intent(UnlockWaitActivity.this, LockRamoteStatusActivity.class);
        intent1.putExtra("DeviceId", mChinaMobileIEMI);
        startActivity(intent1);
        finish();
    }

    @Override
    public void onLocationRec(DataStreamBean ben) {

    }
}
