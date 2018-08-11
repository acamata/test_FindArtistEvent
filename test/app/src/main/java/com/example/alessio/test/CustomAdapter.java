package com.example.alessio.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomAdapter extends BaseAdapter {
    private JSONArray data;
    private LayoutInflater inflater;
    private Locale locale;

    public CustomAdapter(Context context, JSONArray array) {
        data = array;
        inflater = LayoutInflater.from(context);
        this.locale = Locale.getDefault();
    }

    @Override
    public int getCount() {
        return data.length();
    }


    @Override
    public JSONObject getItem(int position) {
        try {
            return (JSONObject) data.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;


        if (view == null) {
            view = inflater.inflate(R.layout.list_item, viewGroup, false);

            holder = new ViewHolder();
            holder.eventDate = (TextView) view.findViewById(R.id.eventDate);
            holder.eventName = (TextView) view.findViewById(R.id.eventName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ItemDataHolder h = new ItemDataHolder();
        try {
            JSONObject item = getItem(position);
            //parseDate(item.getString("formatted_datetime"), h);
            JSONObject venue = item.getJSONObject("venue");
            h.eventName = venue.getString("name") + " ," + venue.getString("city") + " ," + venue.getString("country");
            h.eventDate = item.getString("formatted_datetime");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.eventDate.setText(h.eventDate);
        holder.eventName.setText(h.eventName);

        return view;
    }

    private class ViewHolder {
        TextView eventDate;

        TextView eventName;

    }

    public static class ItemDataHolder {
        String eventDate = "";
        String eventTime = "";
        String eventName = "";


    }

    private void parseDate(String s, ItemDataHolder h) {
        s = s.replace(" at", "");
        s = s.replaceAll(",", "");
        DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy hh:mma", Locale.ENGLISH);
        DateFormat outputDateFormat = new SimpleDateFormat("dd MMMMM yyyy", locale);
        DateFormat outputTimeFormat = new SimpleDateFormat("HH:mm", locale);
        Date parsedDate;

        try {
            parsedDate = inputFormat.parse(s);
            h.eventDate =  outputDateFormat.format(parsedDate);
            h.eventTime =  outputTimeFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
