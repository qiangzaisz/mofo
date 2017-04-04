package mofobike.wlsj.com.mofobike.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import mofobike.wlsj.com.mofobike.R;
import mofobike.wlsj.com.mofobike.manager.BluetoothLogManager;

public class LogActivity extends Activity implements BluetoothLogManager.ILogListener {

    private TextView mLog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLog = (TextView) findViewById(R.id.LogTv);
        BluetoothLogManager.register(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothLogManager.unregister(this);
    }

    @Override
    public void onLogRec(String s) {
        mLog.setText(s);
    }
}
