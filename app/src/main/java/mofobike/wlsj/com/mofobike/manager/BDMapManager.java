package mofobike.wlsj.com.mofobike.manager;

import android.util.Log;
import android.util.SparseArray;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import static com.android.volley.VolleyLog.TAG;

public class BDMapManager {


    private SparseArray<BitmapDescriptor> mBMPDescriptor = new SparseArray<>();
    private BaiduMap mBaiduMap;

    public BDMapManager(BaiduMap map) {
        this.mBaiduMap = map;
        mBaiduMap.setMyLocationEnabled(true);
    }

    public void markMineLocation(BDLocation bdLocation, int res) {
        /**画出当前位置的蓝色圆点*/
        OverlayOptions markerOptions = new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(createBMPDes(res))
                .title("开始")
                .position(new LatLng(bdLocation.getLatitude(),
                        bdLocation.getLatitude()));
        mBaiduMap.setMyLocationData(new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude())
                .build());
        MyLocationConfiguration configuration = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, createBMPDes(res));
        mBaiduMap.setMyLocationConfigeration(configuration);
    }

    public void markLocation(LatLng point, BitmapDescriptor bitmap) {
        Log.i(TAG, "markLocation:" + point);
        //定义Maker坐标点
        //LatLng point = new LatLng(39.963175, 116.400244);
        //构建Marker图标

        //构建MarkerOption，用于在地图上添加Marker
        MarkerOptions option = new MarkerOptions()
                .zIndex(1)
                .position(point)
                .icon(bitmap);

        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }


    public BitmapDescriptor createBMPDes(int resId) {
        BitmapDescriptor bmpDesptor = mBMPDescriptor.get(resId);
        if (bmpDesptor != null) {
            return bmpDesptor;
        }

        BitmapDescriptor create = BitmapDescriptorFactory.
                fromResource(resId);
        mBMPDescriptor.put(resId, create);
        return create;

    }

}
