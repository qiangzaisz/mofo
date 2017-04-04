package mofobike.wlsj.com.mofobike.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chinamobile.iot.onenet.OneNetApi;
import com.chinamobile.iot.onenet.OneNetError;
import com.chinamobile.iot.onenet.OneNetResponse;
import com.chinamobile.iot.onenet.ResponseListener;
import com.google.gson.Gson;

import mofobike.wlsj.com.mofobike.model.DataStreamBean;
import mofobike.wlsj.com.mofobike.model.DeviceListBean;

import static com.android.volley.VolleyLog.TAG;

public class OneNetApiManager {


    public static final String API_KEY = "=JWgfhzG3umyEu=E7SIfIEYSmYA=";
    public static final String UNLOCK_CMD = "ITT#OFO#0000#0000#0000#";
    private Context mContext;
    private IOneNetListener mListener;

    public OneNetApiManager(Context context, IOneNetListener listener) {
        mListener = listener;
        mContext = context;
    }


    public void loadDevices() {
        OneNetApi.getInstance(mContext).getDevices(API_KEY, null, null, null, null, null, null, new ResponseListener() {

            @Override
            public void onResponse(OneNetResponse response) {
                Log.i(TAG, "repose:" + response.getRawResponse());
                DeviceListBean list = new Gson().fromJson(response.getRawResponse(), DeviceListBean.class);
                Log.i(TAG, "list:" + list.getData().getDevices().size());
                if (mListener != null) {
                    mListener.onDeviceListRec(list);
                }

            }

            @Override
            public void onError(OneNetError error) {
                // 网络或服务器错误
            }

        });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            requestData();
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    };

    private String tmpDeviceId = "";

    public void requestData() {
        requestData(null);
    }

    public void requestData(final IOneNetListener listener) {
        Log.i(TAG, "requestData");
        OneNetApi.getInstance(mContext).getDatastream(OneNetApiManager.API_KEY,
                tmpDeviceId, "OFO", new ResponseListener() {
                    @Override
                    public void onResponse(OneNetResponse response) {
                        Log.i(TAG, "requestData:" + response.getRawResponse());
                        try {
                            Log.i(TAG, "response:" + response.getRawResponse());
                            DataStreamBean bean = new Gson().fromJson(response.getRawResponse(), DataStreamBean.class);
                            if (bean.getData().isUnlocked()) {
                                onUnlockSuccess();
                            }
                            if (listener != null) {
                                listener.onLocationRec(bean);
                            }
                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void onError(OneNetError error) {

                    }
                });
    }

    private void onUnlockSuccess() {
        if (mListener != null) {
            mListener.onUnlockSuccess();
            handler.removeCallbacksAndMessages(null);
        }
    }


    public void sendUnlockCmd(String deviceId) {
        Log.i(TAG, "sendUnlockCmd():" + deviceId);
        tmpDeviceId = deviceId;
        OneNetApi.getInstance(mContext).sendToEdp(API_KEY, deviceId, UNLOCK_CMD, new ResponseListener() {


            @Override
            public void onResponse(final OneNetResponse response) {
                Log.i(TAG, "onResponse success():" + response.getRawResponse());
                if (response.getErrno() == 0) {
                }
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onError(OneNetError error) {
            }
        });


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //测试数据
                onUnlockSuccess();
            }
        }, 5000);
    }

    public interface IOneNetListener {

        void onDeviceListRec(DeviceListBean deviceList);


        void onUnlockSuccess();


        void onLocationRec(DataStreamBean ben);
    }

}
