package com.example.timofey.diploma_app.data_classes;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by timofey on 08.02.2016.
 */
public class VKEvent implements Serializable, Comparable{

    private int id;
    private String title;
    private String definition;
    private Long dateStart;
    private Long dateEnd;
    private String place;
    private String photo_50;
    private String photo_200;

    public VKEvent(int id, String title, String definition, Long dateStart, Long dateEnd, String place, String photo_50, String photo_200) {
        this.id = id;
        this.title = title;
        this.definition = definition;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.place = place;
        this.photo_50 = photo_50;
        this.photo_200 = photo_200;
    }

    public VKEvent(int id, String title, String definition, Long dateStart, Long dateEnd, String photo_50, String photo_200) {
        this.id = id;
        this.title = title;
        this.definition = definition;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.photo_50 = photo_50;
        this.photo_200 = photo_200;
    }

    public VKEvent(int id, String title, String definition, Long dateStart, Long dateEnd, String place) {
        this.id = id;
        this.title = title;
        this.definition = definition;
        this.dateStart = dateStart;
        this.place = place;
        this.dateEnd = dateEnd;
    }

    public VKEvent(int id, String title, String definition, Long dateStart, Long dateEnd) {
        this.id = id;
        this.title = title;
        this.definition = definition;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDefinition() {
        return definition;
    }

    public Long getDateStart() {
        return dateStart;
    }

    public String getYear(){
        Date date = new Date(this.dateStart * 1000);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    public Long getDateEnd() {
        return dateEnd;
    }

    public String getPlace() {
        return place;
    }

    public String getPhoto_50() {
        return photo_50;
    }

    public String getPhoto_200() {
        return photo_200;
    }

    @Override
    public int compareTo(Object another) {
        VKEvent anotherEvent = (VKEvent) another;
        return dateStart > anotherEvent.getDateStart() ? 1 : dateStart < anotherEvent.getDateStart() ? -1 : 0;
    }
}
