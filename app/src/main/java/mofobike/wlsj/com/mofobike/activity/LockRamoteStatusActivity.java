package mofobike.wlsj.com.mofobike.activity;

import android.app.Activity;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.chinamobile.iot.onenet.OneNetApi;
import com.chinamobile.iot.onenet.OneNetError;
import com.chinamobile.iot.onenet.OneNetResponse;
import com.chinamobile.iot.onenet.ResponseListener;
import com.google.gson.Gson;

import java.util.Date;

import mofobike.wlsj.com.mofobike.R;
import mofobike.wlsj.com.mofobike.manager.OneNetApiManager;
import mofobike.wlsj.com.mofobike.model.DataStreamBean;

import static com.android.volley.VolleyLog.TAG;

public class LockRamoteStatusActivity extends Activity {


    private TextView mLockStatusDataTv;
    private TableLayout mTableLayout;
    private String mDeviceID;


    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            requestData();
            mHanlder.sendEmptyMessageDelayed(1, 10000);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_ramote_status);
        findViewById(R.id.LockStatusBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mLockStatusDataTv = (TextView) findViewById(R.id.LockStatusDataTv);
        mTableLayout = (TableLayout) findViewById(R.id.LockStatusTabLayout);
        mDeviceID = getIntent().getStringExtra("DeviceId");

        mHanlder.sendEmptyMessageDelayed(1, 100);
    }


    public void requestData() {
        OneNetApi.getInstance(this).getDatastream(OneNetApiManager.API_KEY,
                mDeviceID, "OFO", new ResponseListener() {
                    @Override
                    public void onResponse(OneNetResponse response) {
                        try {
                            Log.i(TAG, "response:" + response.getRawResponse());
                            DataStreamBean bean = new Gson().fromJson(response.getRawResponse(), DataStreamBean.class);
                            mLockStatusDataTv.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " " +
                                    bean.getData().getCurrent_value());
                            onDataRec(bean.getData());
                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void onError(OneNetError error) {

                    }
                });
    }

    private void onDataRec(DataStreamBean.DataBean bean) {
        mTableLayout.removeAllViews();
        Log.i(TAG, "onDataRec");
        String[] content = bean.getCurrent_value().split("#");
        addLabelAndValue("引导码：", content[0]);
        addLabelAndValue("g_x：", content[1]);
        addLabelAndValue("g_y：", content[2]);
        addLabelAndValue("g_z：", content[3]);
        String lockStatus = content[4] + "(";
        if (content[4].equals("O")) {
            lockStatus += "开锁";
        } else if (content[4].equals("C")) {
            lockStatus += "闭锁";
        } else {
            lockStatus += "未知";
        }
        lockStatus += ")";
        addLabelAndValue("锁的状态：", lockStatus);
        addLabelAndValue("电池电量：", content[5]);
        addLabelAndValue("故障代码：", content[6]);
        addLabelAndValue("紧急状态：", content[7]);
        addLabelAndValue("卫星个数：", content[8]);
        addLabelAndValue("时间：", content[9]);
        addLabelAndValue("GPS状态：", content[10]);
        addLabelAndValue("纬度：", content[11]);
        addLabelAndValue("南北纬：", content[12]);
        addLabelAndValue("经度：", content[13]);
        addLabelAndValue("东西经：", content[14]);
        addLabelAndValue("对地速度：", content[15]);
        addLabelAndValue("方向角度：", content[16]);
        addLabelAndValue("日期：", content[17]);

    }

    private void addLabelAndValue(String key, String value) {
        TableRow row = new TableRow(this);
        TextView label = new TextView(this);
        label.setText(key);
        TextView valuetv = new TextView(this);
        valuetv.setText(value);
        row.addView(label);
        row.addView(valuetv);
        mTableLayout.addView(row);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHanlder.removeCallbacksAndMessages(null);
    }
}
