package com.example.y_t.datameasurer.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import com.example.y_t.datameasurer.Adapters.APinfoAdapter;
import com.example.y_t.datameasurer.MainAct;
import com.example.y_t.datameasurer.Models.APInfoSet;
import com.example.y_t.datameasurer.Models.APinfo;
import com.example.y_t.datameasurer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 获取wifi列表
 */
public class WIFIUtil {

    private static boolean _wifi = false;
    private static List<APinfo> _aps = null;
    private static List<ScanResult> _aplist = null;
    private static DataMsger _msger = null;
    private final Object _lock = new Object();
    private static int _packagegroup = -1;
    private ArrayList<APInfoSet> _datapackage = null;
    private APinfoAdapter _apt = null;
    private Activity _holder;
    private WifiManager _manager = null;
    private Handler _handler = null;
    private Thread _scan = null;

    public ObservableField<String> NUM = new ObservableField<>();
    public ObservableField<String> NOW = new ObservableField<>();
    public ObservableField<String> NOWInfo = new ObservableField<>();

    public ObservableField<String> IP = new ObservableField<>("39.106.123.5");
    public ObservableField<String> PORT = new ObservableField<>("3306");
    public ObservableField<String> Status = new ObservableField<>("NULL");
    //节点序号
    public ObservableField<String> ID = new ObservableField<>("0");
    public ObservableField<String> IDY = new ObservableField<>("0");
    //节点组内编号
    public ObservableField<String> SubID = new ObservableField<>("0");
    //楼层号
    public ObservableField<String> Layer = new ObservableField<>("0");
    //测量方向
    public ObservableField<String> Direction = new ObservableField<>("0");

    public WIFIUtil(Activity activity) {
        _datapackage = new ArrayList<>(5);
        _holder = activity;
        _manager = (WifiManager) _holder.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ((MainAct) _holder).mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                // 该扫描已成功完成。
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    synchronized (_lock) {
                        _wifi = true;
                        _lock.notify();
                    }
                } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_ENABLED:
                            _manager.startScan();
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            break;
                    }
                }
            }
        };
        _handler = new Handler();
        StateCheck();
        IP.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                _msger = null;
            }
        });
        PORT.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                _msger = null;
            }
        });
        // ReceiveDataAsync();
    }

    private void StateCheck() {
        Init();
        ((MainAct) _holder).CheckPermission();
//        if ((WifiManager.WIFI_STATE_ENABLED != _manager.getWifiState()) && (WifiManager.WIFI_STATE_ENABLING != _manager.getWifiState())) {
//            _manager.setWifiEnabled(true);
//        }
        _scan.start();
    }

    private void Init() {
        _aps = new ArrayList<>();
        //创建新的适配器
        _apt = new APinfoAdapter(_holder, R.layout.wifi_item, _aps);
        ListView reslist = _holder.findViewById(R.id.reslist);
        reslist.setAdapter(_apt);
        reslist.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            NOW.set(_aps.get(position).get_name());
            ScanResult temp = _aplist.get(position);
            // NOWInfo.set(temp.toString());
            NOWInfo.set(String.format("%-14s : %-18s \n%-14s : %-18s \n%-14s : %-18s \n%-14s : %-18s \n", "MAC", temp.BSSID, "LEVEL", temp.level, "Frequency", temp.frequency, "Capabilities", temp.capabilities));
        });

        //在异步线程中刷新信号信息
        _scan = new Thread(() -> {
            while (true) {
                if (!_wifi) {
                    synchronized (_lock) {
                        try {
                            _lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                _aplist = _manager.getScanResults();
                Collections.sort(_aplist, (ScanResult o1, ScanResult o2) -> Integer.compare(o2.level, o1.level));
                if (!_aps.isEmpty()) _aps.clear();
                for (int i = 0; i < _aplist.size(); i++) {
                    if (i >= 40) break;
                    _aps.add(APinfo.FromScanResult(_aplist.get(i)));
                }
                _handler.post(() -> {
                    _apt.notifyDataSetChanged();
                    NUM.set("" + _aps.size());
                });
                if (_packagegroup > 0) {
                    _handler.post(() -> {
                        Status.set("Getting:" + _packagegroup);
                    });
                    _packagegroup--;
                    _datapackage.add(new APInfoSet(
                            Integer.parseInt(Layer.get()),
                            Integer.parseInt(ID.get()),
                            Integer.parseInt(IDY.get()),
                            Integer.parseInt(SubID.get()),
                            Integer.parseInt(Direction.get()),
                            _aps.size(),
                            _aps));
                } else if (_packagegroup == 0) {
                    _packagegroup--;
                    _handler.post(() -> {
                        Status.set("Sending!");
                    });
                    // TODO:sendMessage
                    _msger = new MySqlMsger();
                    _msger.Init();
                    _msger.PostData(_datapackage);
                    _msger.Close();
                    _datapackage.clear();
                    _handler.post(() -> {
                        Status.set("Success!");
                    });
                }
                _wifi = false;
                _manager.startScan();
            }
        });
    }

//    private void SendData() {
//        try {
//            DatagramSocket udpsoc = new DatagramSocket();
//            InetAddress serverAddress = InetAddress.getByName(IP.get());
//            byte[] udpbuffer = GetPackageString().getBytes("UTF-8");
//            DatagramPacket udppac = new DatagramPacket(udpbuffer, udpbuffer.length, serverAddress, Integer.parseInt(PORT.get()));
//            udpsoc.send(udppac);
//            udpsoc.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void ReceiveDataAsync() {
//        new Thread(() -> {
//            ServerSocket ss = null;
//            try {
//                ss = new ServerSocket(20190);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            while (true) {
//                try {
////                    DatagramSocket socket = new DatagramSocket(20190);
////                    byte data[] = new byte[1024];
////                    DatagramPacket packet = new DatagramPacket(data, data.length);
////                    socket.receive(packet);
////                    String result = new String(packet.getData(), packet.getOffset(), packet.getLength());
////                    System.out.println(result);
////                    _handler.post(() -> {
////                        Status.set(result);
////                    });
////                    socket.close();
//                    //不止接受一个客户端
//                    Socket s = ss.accept();//接受一个连接
//                    DataInputStream dis = new DataInputStream(s.getInputStream());//输入管道
//                    System.out.println(dis.readUTF());
//                    Thread.sleep(200);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
    //获取一组数据的字符形式
//    private String GetDataString(List<APinfo> infos) {
//        StringBuilder builder = new StringBuilder();
//        builder.append('+');
//        builder.append(String.format("%s-", ID.get()));
//        builder.append(String.format("%s-", SubID.get()));
//        builder.append(String.format("%s-", Layer.get()));
//        builder.append(String.format("%s-", Direction.get()));
//        builder.append('=');
//        for (APinfo i : infos) {
//            builder.append(String.format("%s-%s|", i.get_mac(), -i.get_level()));
//        }
//        return builder.toString();
//    }

    public void ADD(int resid, int maxvalue) {
        EditText text = _holder.findViewById(resid);
        int now = Integer.parseInt(text.getText().toString());
        if (now + 1 <= maxvalue)
            now++;
        else
            now = 0;
        text.setText("" + now);
    }

    public void DEC(int resid, int maxvalue) {
        EditText text = _holder.findViewById(resid);
        int now = Integer.parseInt(text.getText().toString());
        if (now - 1 >= 0)
            now--;
        else
            now = maxvalue;
        text.setText("" + now);
    }

    //切换扫描状态
    public void SwitchWifiState(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            StartScan();
        else
            StopScan();
    }

    //发送一组新数据（5个）
    public void SendPackage(View view) {
        _packagegroup = 5;
    }

    //开始扫描
    public void StartScan() {
        if ((WifiManager.WIFI_STATE_ENABLED != _manager.getWifiState()) && (WifiManager.WIFI_STATE_ENABLING != _manager.getWifiState())) {
            _manager.setWifiEnabled(true);
        }
    }

    //停止扫描
    public void StopScan() {
        _manager.setWifiEnabled(false);
    }

}
