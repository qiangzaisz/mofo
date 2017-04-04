package mofobike.wlsj.com.mofobike.presenter;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

import mofobike.wlsj.com.mofobike.model.DataStreamBean;
import mofobike.wlsj.com.mofobike.model.DeviceListBean;

public interface IMainActivity {

    public void onMineLocationRec(BDLocation location);

    void onDeviceListRec(DeviceListBean deviceList);

    void onLocationRec(DataStreamBean bean);
}
