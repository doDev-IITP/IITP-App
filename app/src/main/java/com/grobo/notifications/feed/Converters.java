package com.grobo.notifications.feed;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {

    @TypeConverter
    public static String stringFromArray(List<String> strings) {
        if (strings != null) {
            StringBuilder string = new StringBuilder();
            for (String s : strings) string.append(s).append(",");

            return string.toString();
        }
        return "";
    }

    @TypeConverter
    public static List<String> arrayFromString(String concatenatedStrings) {

        if (concatenatedStrings != null && !concatenatedStrings.isEmpty()) {
            return new ArrayList<>(Arrays.asList(concatenatedStrings.split(",")));
        }
        return new ArrayList<>();
    }
}