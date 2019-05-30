package com.example.y_t.datameasurer.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.y_t.datameasurer.Models.APinfo;
import com.example.y_t.datameasurer.R;

import java.util.List;

public class APinfoAdapter extends ArrayAdapter {
    private int _resID;
    private List<APinfo> _aps;

    public APinfoAdapter(Context context, int style , List<APinfo> objects) {
        super(context, style, objects);
        _resID = style;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        APinfo info = (APinfo) getItem(position);
        //动态加载布局文件
        View view = LayoutInflater.from(getContext()).inflate(_resID, parent, false);
        //找实例
        ((TextView)view.findViewById(R.id.wifi_name)).setText(info.get_name());
        ((TextView)view.findViewById(R.id.wifi_mac)).setText(info.get_mac());
        ((TextView)view.findViewById(R.id.wifi_level)).setText(""+info.get_level());
        //返回布局
        return view;
    }



}
