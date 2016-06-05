package com.example.timofey.diploma_app.worked_classes;

import android.graphics.drawable.Drawable;

import com.example.timofey.diploma_app.data_classes.VKEvent;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by timofey on 15.04.2016.
 */
public class EventsImporter {

    public EventsImporter(){}

    public ArrayList<VKEvent> getEventsByJson(VKResponse response) throws JSONException {

        Long currentDate = System.currentTimeMillis() / 1000L;
        Long eventDate;
        String type;

        JSONObject jsonObject = new JSONObject(response.json.toString());
        ArrayList<VKEvent> eventList = new ArrayList<VKEvent>();
        jsonObject = jsonObject.getJSONObject("response");
        int count = jsonObject.getInt("count");
        JSONArray jsonArray = jsonObject.getJSONArray("items");



        for (int i = 0; i < count; i++) {
            type = jsonArray.getJSONObject(i).getString("type").toString();



            if (type.equals("event")) {
                eventDate = jsonArray.getJSONObject(i).getLong("finish_date");
               // if (eventDate > currentDate){

                    JSONObject jsonPlace = jsonArray.getJSONObject(i);
                    System.out.println(jsonPlace.toString());
                    VKEvent event = addEvent(jsonArray.getJSONObject(i));
                    eventList.add(event);
               // }
            }
        }
        return eventList;
    }


    public VKEvent addEvent(JSONObject jsonObject) throws JSONException {
        int id = jsonObject.getInt("id");
        String title = jsonObject.getString("name");
        String description = jsonObject.getString("description");
        Long dateStart = jsonObject.getLong("start_date");
        Long dateEnd = jsonObject.getLong("finish_date");
        String place = new String();
        String photo_50 = new String();
        String photo_200 = new String();


        try {
            photo_50 = jsonObject.getString("photo_50");
            photo_200 = jsonObject.getString("photo_200");
        } catch (JSONException e){
            try {
                jsonObject = jsonObject.getJSONObject("place");
                place = jsonObject.getString("address");
                return new VKEvent(id, title, description, dateStart, dateEnd, place);
            } catch (JSONException e1){
                return new VKEvent(id, title, description, dateStart, dateEnd);
            }
        }
        try{
            jsonObject = jsonObject.getJSONObject("place");
            place = jsonObject.getString("address");
            return new VKEvent(id, title, description, dateStart, dateEnd, place, photo_50, photo_200);
        }catch (JSONException e2){
            return new VKEvent(id, title, description, dateStart, dateEnd, photo_50, photo_200);
        }
    }

    public static Drawable getDrawableFromUrl(String urlString) throws MalformedURLException, IOException {
        URL url = new URL(urlString);

        InputStream inputStream = url.openStream();

        Drawable drawable = Drawable.createFromStream(inputStream, "src");

        return drawable;
    }

}
