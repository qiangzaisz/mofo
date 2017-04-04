package mofobike.wlsj.com.mofobike.presenter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.chinamobile.iot.onenet.OneNetApi;
import com.chinamobile.iot.onenet.OneNetError;
import com.chinamobile.iot.onenet.OneNetResponse;
import com.chinamobile.iot.onenet.ResponseListener;
import com.google.gson.Gson;

import mofobike.wlsj.com.mofobike.manager.OneNetApiManager;
import mofobike.wlsj.com.mofobike.model.DataStreamBean;
import mofobike.wlsj.com.mofobike.model.DeviceListBean;

public class MainActivityPresenter implements OneNetApiManager.IOneNetListener {

    private static final String TAG = "MainActivityPresenter";
    private IMainActivity mMainActivity;
    private OneNetApiManager mOneNetApiManager;

    public MainActivityPresenter(IMainActivity mainActivity) {
        this.mMainActivity = mainActivity;
        mOneNetApiManager = new OneNetApiManager((Context) mainActivity, this);
    }


    public void locationMine() {
        startLocation();
    }

    private void startLocation() {
        Log.i(TAG, "location");
        final LocationClient locationClient = new LocationClient((Context) mMainActivity);
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(final BDLocation bdLocation) {
                Log.i(TAG, "onReceiveLocation(),"
                        + bdLocation.getAddrStr() + ",la:" + bdLocation.getLatitude() + ",lng:" + bdLocation.getLongitude());
                if (bdLocation == null || bdLocation.getLocType() == 162) {
                    Toast.makeText((Context) mMainActivity, "定位失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                locationClient.stop();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMainActivity.onMineLocationRec(bdLocation);
                    }
                });
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
        option.setTimeOut(10000);
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
        locationClient.start();// 开始定位
    }


    public void runOnUiThread(Runnable action) {
        ((Activity) mMainActivity).runOnUiThread(action);
    }


    public void loadDevices() {
        mOneNetApiManager.loadDevices();
    }

    public void sendUnlockCmd() {
        mOneNetApiManager.sendUnlockCmd("5095205");
    }

    @Override
    public void onDeviceListRec(DeviceListBean deviceList) {


        for (DeviceListBean.DataBean.DevicesBean device : deviceList.getData().getDevices()) {
            OneNetApi.getInstance((Context) mMainActivity).getDatastream(OneNetApiManager.API_KEY,
                    device.getId(), "OFO", new ResponseListener() {
                        @Override
                        public void onResponse(OneNetResponse response) {
                            Log.i(TAG, "requestData:" + response.getRawResponse());
                            try {
                                Log.i(TAG, "response:" + response.getRawResponse());
                                DataStreamBean bean = new Gson().fromJson(response.getRawResponse(), DataStreamBean.class);
                                mMainActivity.onLocationRec(bean);
                            } catch (Exception e) {

                            }

                        }

                        @Override
                        public void onError(OneNetError error) {

                        }
                    });
        }
        mMainActivity.onDeviceListRec(deviceList);
    }

    @Override
    public void onUnlockSuccess() {

    }

    @Override
    public void onLocationRec(DataStreamBean ben) {

    }
}
