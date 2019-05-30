package com.example.y_t.datameasurer.Models;


import android.net.wifi.ScanResult;

/**
 * AP节点的信息
 */
public class APinfo {

    private String _name;

    public String get_name() {
        return _name;
    }

    private String _mac;

    public String get_mac() {
        return _mac;
    }

    private int _level;

    public int get_level() {
        return _level;
    }

    public APinfo(String name, String mac, int level) {
        _name = name;
        _mac = mac;
        _level = level;
    }

    @Override
    public String toString() {
        return String.format("<- NAME:%s   MAC:%s   Level:%d ->", _name, _mac, _level);
    }

    public static APinfo FromScanResult(ScanResult res) {
        return new APinfo(res.SSID, res.BSSID, res.level);
    }
}
