package com.grobo.notifications.feed;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {

    @TypeConverter
    public String stringFromArray(List<String> strings) {
        if (strings != null) {
            StringBuilder string = new StringBuilder();
            for (String s : strings) string.append(s).append(",");

            return string.toString();
        }
        return null;
    }

    @TypeConverter
    public List<String> arrayFromString(String concatenatedStrings) {

        if (concatenatedStrings != null) {
            return new ArrayList<>(Arrays.asList(concatenatedStrings.split(",")));
        }
        return null;
    }
}