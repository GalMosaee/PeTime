package com.example.petime.Model;


import androidx.room.TypeConverter;

import com.google.firebase.Timestamp;

import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class TimestampConverters {
    @TypeConverter
    public static Timestamp fromTimestamp(Long value) {
        return value == null ? null : getTimestampFromLong(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Timestamp timestamp){
        return timestamp == null ? new Timestamp(new Date()).toDate().getTime(): timestamp.toDate().getTime();
    }

    /*public static Timestamp getTimestamp(int y,int m, int d) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, y);
            cal.set(Calendar.MONTH, m);
            cal.set(Calendar.DATE, d);
            return new Timestamp(cal.getTime());
    }*/

    public static Timestamp getTimestampFromLong(long time){
        Date date = new Date(time);
        return new Timestamp(date);
    }

    public static long getLongFromTimestamp(Timestamp timestamp) {
        return timestamp.toDate().getTime();
    }
}
