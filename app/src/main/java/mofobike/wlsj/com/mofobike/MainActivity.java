package mofobike.wlsj.com.mofobike;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import mofobike.wlsj.com.mofobike.activity.UnlockWaitActivity;
import mofobike.wlsj.com.mofobike.manager.BDMapManager;
import mofobike.wlsj.com.mofobike.manager.BlueUnlockManager;
import mofobike.wlsj.com.mofobike.model.DataStreamBean;
import mofobike.wlsj.com.mofobike.model.DeviceListBean;
import mofobike.wlsj.com.mofobike.presenter.IMainActivity;
import mofobike.wlsj.com.mofobike.presenter.MainActivityPresenter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IMainActivity {


    private static final int REQUEST_CODE_LOCATION = 100;
    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE_CAMERA = 200;
    public static final int REQUEST_CODE_RAMOTE = 201;
    public static final int REQUEST_CODE_BLUETOOTH = 202;
    private MapView mMapView;
    private MainActivityPresenter mPresenter;
    private BDMapManager mBDMapManager;
    private BlueUnlockManager mBLUManager;

    BitmapDescriptor bitmapDescriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        mMapView = (MapView) findViewById(R.id.MainMapView);
        mBDMapManager = new BDMapManager(mMapView.getMap());
        mPresenter = new MainActivityPresenter(this);


        int permissionState = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionState != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            mPresenter.locationMine();
        }

        mBLUManager = new BlueUnlockManager(this, new BlueUnlockManager.IOnUnlockListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailed() {

            }
        });

        findViewById(R.id.scan_unlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionState = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA);
                if (permissionState != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                } else {
                    scanQRCode(REQUEST_CODE_RAMOTE);
                }

            }
        });

        findViewById(R.id.blurtooth_unlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //蓝牙解锁
                int permissionState = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA);
                if (permissionState != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                } else {
                    scanQRCode(REQUEST_CODE_BLUETOOTH);
                }

            }
        });

        mPresenter.loadDevices();
        bitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.lock1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RAMOTE || requestCode == REQUEST_CODE_BLUETOOTH) {
            onScanResult(requestCode, resultCode, data);
        }
    }

    private void onScanResult(int requestCode, int resultCode, Intent data) {
        //扫描二维码回调
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "扫描失败", Toast.LENGTH_LONG).show();
            return;
        }
        if (data == null) {
            Toast.makeText(this, "扫描失败", Toast.LENGTH_LONG).show();
            return;
        }
        Bundle bundle = data.getExtras();
        if (bundle == null) {
            Toast.makeText(this, "扫描失败", Toast.LENGTH_LONG).show();
            return;
        }
        String content = bundle.getString("result");
        if (content == null) {
            Toast.makeText(this, "扫描失败", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, UnlockWaitActivity.class);
        intent.putExtra("action", requestCode);
        intent.putExtra("content", content);
        startActivity(intent);

        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    private void scanQRCode(int requestCode) {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length <= 0) {
                Toast.makeText(this, "没有相机权限", Toast.LENGTH_LONG).show();
                return;
            }
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                mPresenter.locationMine();
                return;
            }
            Toast.makeText(this, "没有相机权限", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ParamSetActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scan) {
            // Handle the camera action
        } else if (id == R.id.nav_bluetooth) {
            mBLUManager.unlock("");

        } else if (id == R.id.nav_disconnect) {
            mBLUManager.disConnect();

        } else if (id == R.id.nav_read) {
            mBLUManager.readNotifyDevices();

        } else if (id == R.id.nav_unlock) {
            mBLUManager.sendUnlockCmd();

        } else if (id == R.id.nav_param) {
            Intent intent = new Intent(this, ParamSetActivity.class);
            startActivity(intent);
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMineLocationRec(final BDLocation location) {
        Log.i(TAG, "onMineLocationRec");
        mBDMapManager.markMineLocation(location, R.mipmap.location_mine_descriptor);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                mBDMapManager.markLocation(latLng, R.mipmap.bk_location_marker);
//            }
//        }, 2000);
    }

    @Override
    public void onDeviceListRec(DeviceListBean deviceList) {
        //TODO

    }


    @Override
    public void onLocationRec(final DataStreamBean bean) {
        Log.i(TAG, "onLocationRec");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBDMapManager.markLocation(bean.getData().getLocation(), bitmapDescriptor);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBLUManager.disConnect();
    }
}
