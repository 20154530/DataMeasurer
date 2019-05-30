package com.example.y_t.datameasurer.manager;

import android.app.Service;
import com.example.y_t.datameasurer.Models.APInfoSet;

import java.util.List;

public abstract class DataMsger  extends Service {

    public OnMsgReceivedListener Listener = null;

    public void PostData(List<APInfoSet> msg){  }

    public void PostData(String msg){ }

    public abstract void Listen();

    public abstract void Init();

    public abstract void Close();
}
