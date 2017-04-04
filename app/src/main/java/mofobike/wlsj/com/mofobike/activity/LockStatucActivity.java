package mofobike.wlsj.com.mofobike.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.clj.fastble.utils.HexUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import mofobike.wlsj.com.mofobike.BluetoothActionReceiver;
import mofobike.wlsj.com.mofobike.R;
import mofobike.wlsj.com.mofobike.manager.BlueToothManager;
import mofobike.wlsj.com.mofobike.manager.BluetoothDataManager;
import mofobike.wlsj.com.mofobike.model.BikeStatus;

import static mofobike.wlsj.com.mofobike.R.id.LockStatucTv;

public class LockStatucActivity extends Activity implements BluetoothDataManager.ILockListener {


    private TextView mLockData;

    private TextView mLockStatusTv;
    private TextView mLockSensor;
    private TextView mLockBattery;

    private BluetoothActionReceiver mReceiver = new BluetoothActionReceiver() {
        @Override
        protected void onDataRefreshed(Intent intent) {
            super.onDataRefreshed(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_status);
        findViewById(R.id.LockStatusBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.LockStatucDisConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueToothManager.disconnect(LockStatucActivity.this);
            }
        });
        mLockData = (TextView) findViewById(R.id.LockStatusDataTv);
        BluetoothDataManager.register(this);
        mLockStatusTv = (TextView) findViewById(LockStatucTv);
        mLockSensor = (TextView) findViewById(R.id.LockStatucSensor);
        mLockBattery = (TextView) findViewById(R.id.LockStatucBattery);
    }

    @Override
    public void onLockStatusRec(final BikeStatus bikeStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bikeStatus.data == null) {
                    return;
                }
                StringBuffer sb = new StringBuffer(new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()))
                        .append(",").append(String.valueOf(HexUtil.encodeHex(bikeStatus.data)));
                mLockData.setText(sb);


                parseData(bikeStatus.data);
            }
        });

    }

    private void parseData(byte[] data) {
        if (data.length < 10) {
            return;
        }
        if (data[2] == 0x01) {
            mLockStatusTv.setText("锁住");
        } else {
            mLockStatusTv.setText("开启");
        }

        StringBuffer sensor = new StringBuffer("x=").append(getShort(data, 3))
                .append(",y=").append(getShort(data, 5))
                .append(",z=").append(getShort(data, 7));
        mLockSensor.setText(sensor.toString());

        mLockBattery.setText(String.valueOf(data[8]));

    }


    /**
     * 通过byte数组取到short
     *
     * @param b
     * @param index 第几位开始取
     * @return
     */
    public static short getShort(byte[] b, int index) {
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothDataManager.unresgister(this);
        BlueToothManager.disconnect(LockStatucActivity.this);
    }
}
