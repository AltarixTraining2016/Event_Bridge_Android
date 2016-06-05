package com.example.timofey.diploma_app.worked_classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by timofey on 12.05.2016.
 */
public class DateConverter {

    Long unixDate;

    public DateConverter(Long unixDate) {
        this.unixDate = unixDate;
    }

    public void setUnixDate(Long unixDate) {
        this.unixDate = unixDate;
    }

    public String toInfoString(){
        Date date = new Date(this.unixDate * 1000);
        System.out.println(date.toString());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + " " + getMounth(calendar.get(Calendar.MONTH)))
                + " " + String.valueOf(calendar.get(Calendar.YEAR) + ", " + dateFormat.format(date));
    }


    String getMounth(int num){
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "Января");
        map.put(1, "Февраля");
        map.put(2, "Марта");
        map.put(3, "Апреля");
        map.put(4, "Мая");
        map.put(5, "Июня");
        map.put(6, "Июля");
        map.put(7, "Августа");
        map.put(8, "Сентября");
        map.put(9, "Октября");
        map.put(10, "Ноября");
        map.put(11, "Декабря");

        return map.get(num);
    }

    static String getMounthAbbreviation(Long unixDate){

        int month;
        Date date = new Date(unixDate * 1000);
        System.out.println(date.toString());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        month = calendar.get(Calendar.MONTH);

        Map<Integer, String> map = new HashMap<Integer, String>();

        map.put(0, "ЯНВ");
        map.put(1, "ФЕВ");
        map.put(2, "МАР");
        map.put(3, "АПР");
        map.put(4, "МАЙ");
        map.put(5, "ИЮН");
        map.put(6, "ИЮЛ");
        map.put(7, "АВГ");
        map.put(8, "СЕН");
        map.put(9, "ОКТ");
        map.put(10, "НОЯ");
        map.put(11, "ДЕК");

        return map.get(month);
    }

    static String getDay(Long unixDate){

        Date date = new Date(unixDate * 1000);
        System.out.println(date.toString());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (day <= 9) return 0 + String.valueOf(day);
         else return String.valueOf(day);
    }
}
