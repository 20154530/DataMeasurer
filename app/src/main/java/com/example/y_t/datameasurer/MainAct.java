package com.example.y_t.datameasurer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.*;
import com.example.y_t.datameasurer.manager.StatusBarManager;
import com.example.y_t.datameasurer.manager.WIFIUtil;
import com.example.y_t.datameasurer.databinding.ActivityMainBinding;

public class MainAct extends AppCompatActivity {

    //region Properties

    //权限请求码
    private static final int PERMISSION_REQUEST_CODE = 0;
    public  BroadcastReceiver mReceiver;

    private GridLayout _listtitle = null;
    private WIFIUtil _wifiutil = null;
    private GridLayout _rootPanel = null;
    private DisplayMetrics _display = null;
    //endregion

    //region Overrides..
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        _wifiutil = new WIFIUtil(this);
        binding.setWifi(_wifiutil);
        StatusBarManager.getInstance().setCurrentActivity(this);
        _display = getResources().getDisplayMetrics();
        _listtitle = findViewById(R.id.wifi_list_title);
        _rootPanel = findViewById(R.id.rootlayout);
        _rootPanel.setMinimumHeight(_display.heightPixels-StatusBarManager.getInstance().GetHeight());
        SetListTitle();
        registerBroadcast();
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(2147483647);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllPermission = true;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i : grantResults) {
                if (i != getPackageManager().PERMISSION_GRANTED) {
                    hasAllPermission = false;   //判断用户是否同意获取权限
                    break;
                }
            }
            if (!hasAllPermission) {
                Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //endregion

    //region Methods
    public void CheckPermission() {
//        if (MainAct.this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED) {
//            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
//        }
    }

    private void SetListTitle() {
        if (_listtitle == null)
            return;
        _listtitle.setBackgroundResource(R.color.colorMainTheme);
        TextView name = _listtitle.findViewById(R.id.wifi_name);
        TextView mac = _listtitle.findViewById(R.id.wifi_mac);
        TextView level = _listtitle.findViewById(R.id.wifi_level);
        name.setText("NAME");
        mac.setText("MAC");
        level.setText("LEVEL");
    }
    //endregion
}
