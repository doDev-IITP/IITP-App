package com.grobo.notifications.timetable;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public final class TimetableUtility {

    private static final String LOG_TAG = TimetableUtility.class.getSimpleName();

    public static String downloadTimetable(String requestUrl) {
        if (requestUrl == null) {
            return null;
        }

        Log.e(LOG_TAG, requestUrl);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestUrl).get().build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();

                if (json.equals("null"))
                    return null;

                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<TimetableItem> extractTimetable(String jsonResponse, String dayPreference) {

        if (jsonResponse == null || jsonResponse.isEmpty() || dayPreference == null || dayPreference.isEmpty()) {
            Log.e(LOG_TAG, "No data in timetable json");
            return null;
        }

        List<TimetableItem> timetableData = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);

            JSONArray day = jsonObject.getJSONArray(dayPreference);

            for (int j = 0; j < day.length(); j++) {

                JSONObject currentSubject = day.getJSONObject(j);

                Gson gson = new GsonBuilder().create();
                TimetableItem newItem = gson.fromJson(currentSubject.toString(), TimetableItem.class);

                timetableData.add(newItem);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the timetable JSON results", e);
        }

        return timetableData;
    }
}