package com.example.y_t.datameasurer.manager;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPSocketMsger extends DataMsger{
    private String _IP;
    private int _port;
    private Socket _client;
    byte _buff[] = new byte[4096];
    private PrintWriter _pw;
    private InputStream _is;
    private DataInputStream _dis;
    private static boolean _isrunning = true;
    private static boolean _connected = false;

    public TCPSocketMsger(String ip, int port) {
        _IP = ip;
        _port = port;
    }

    public void set_listener(OnMsgReceivedListener l) {
        this.Listener = l;
    }

    @Override
    public void PostData(String msg) {
        while (!_connected);
        _pw.println(msg);
        _pw.flush();
    }

    @Override
    public void Listen() {
        new Thread(() -> {
            int rcvLen;
            String rcvMsg;
            try {
                _client = new Socket(_IP, _port);
                _client.setSoTimeout(2000);
                _pw = new PrintWriter(_client.getOutputStream(), true);
                _is = _client.getInputStream();
                _dis = new DataInputStream(_is);
                if (_client.isConnected())
                    _connected = true;
                while (_isrunning) {
                    try {
                        rcvLen = _dis.read(_buff);
                        rcvMsg = new String(_buff, 0, rcvLen, "utf-8");
                        if (Listener != null) Listener.Get(rcvMsg);
                        if (rcvMsg.equals("QuitClient")) {   //服务器要求客户端结束
                            _isrunning = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    _pw.close();
                    _is.close();
                    _dis.close();
                    _client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }



    @Override
    public void Init() {

    }

    @Override
    public void Close() {
        _isrunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
