package com.example.timofey.diploma_app.worked_classes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.timofey.diploma_app.R;
import com.example.timofey.diploma_app.data_classes.VKEvent;

import java.util.ArrayList;

import android.widget.TextView;

/**
 * Created by timofey on 28.05.2016.
 */
public class CustomListAdapter extends BaseAdapter {


    private LayoutInflater LInflater;
    private Activity activity;
    private ArrayList<VKEvent> list;

    public CustomListAdapter(Context context, ArrayList<VKEvent> list){
        this.list = list;
        this.activity = (Activity) context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.my_list, null);
            holder.day = (TextView) convertView.findViewById(R.id.day);
            holder.month = (TextView) convertView.findViewById(R.id.mounth);
            holder.year = (TextView) convertView.findViewById(R.id.year);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        holder.day.setText(DateConverter.getDay(list.get(position).getDateStart()));
        holder.year.setText(list.get(position).getYear());
        holder.month.setText(DateConverter.getMounthAbbreviation(list.get(position).getDateStart()));

        holder.name.setText(list.get(position).getTitle());
        return convertView;
    }

    public static class ViewHolder{
        private TextView day;
        private TextView month;
        private TextView year;
        private TextView name;
    }
}
