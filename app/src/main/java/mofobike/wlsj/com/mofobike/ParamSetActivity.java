package mofobike.wlsj.com.mofobike;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import mofobike.wlsj.com.mofobike.manager.BlueToothManager;
import mofobike.wlsj.com.mofobike.util.ParamSettingUtil;

public class ParamSetActivity extends Activity {


    private EditText mEdittext;
    private CheckBox mTimerSettingCkb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.setting_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });





        mTimerSettingCkb = (CheckBox) findViewById(R.id.setting_timer);
        mTimerSettingCkb.setChecked(ParamSettingUtil.isTimerOpen(this));
        mTimerSettingCkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ParamSettingUtil.setTimerIsOpen(ParamSetActivity.this, isChecked);
            }
        });
    }


}
