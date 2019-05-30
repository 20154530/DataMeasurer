package com.example.y_t.datameasurer.manager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.example.y_t.datameasurer.Models.APInfoSet;

import java.sql.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class MySqlMsger extends DataMsger {

    private static String _connectionpara = "jdbc:mysql://39.106.123.5:3306/WIFIDATA";
    private static String _name = "com.mysql.jdbc.Driver";
    private static String _user = "Y_T";
    private static String _password = "20154530";

    private static Connection _conn = null;
    private static PreparedStatement _state = null;

    public MySqlMsger(String connectionstring) {
        _connectionpara = connectionstring;
    }

    public MySqlMsger() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void PostData(List<APInfoSet> msg) {
        try {
            for (APInfoSet info : msg) {
                _state = _conn.prepareStatement(GetSqlParas(info));
                FillSqlParas(info, _state);
                _state.executeUpdate();
                _state.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void Listen() {

    }

    @Override
    public void Init() {
        try {
            Class.forName(_name);
            _conn = DriverManager.getConnection(_connectionpara, _user, _password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Close() {
        try {
            _conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String GetSqlParas(APInfoSet msg) {
        StringBuilder columnbuilder = new StringBuilder();
        StringBuilder parabuilder = new StringBuilder();
        for (int i = 0; i < msg.APS.size(); i++) {
            parabuilder.append(",?,?");
            columnbuilder.append(",bssid_" + i + ",level_" + i);
        }
        return "INSERT INTO Wsignal (floor,pointx,pointy,spoint,direction,amount" + columnbuilder.toString() + ")VALUES( ?,?,?,?,?,?" + parabuilder.toString() + ")";
    }

    private void FillSqlParas(APInfoSet msg, PreparedStatement state) {
        try {
            state.setInt(1, msg.Floor);
            state.setInt(2, msg.PointX);
            state.setInt(3, msg.PointY);
            state.setInt(4, msg.Spoint);
            state.setInt(5, msg.Direction);
            state.setInt(6, msg.Amount);
            for (int i = 0; i < msg.APS.size(); i++) {
                state.setString(2 * i + 7, msg.APS.get(i).get_mac());
                state.setInt(2 * i + 8, msg.APS.get(i).get_level());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
