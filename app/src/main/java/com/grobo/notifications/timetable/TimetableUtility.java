package com.grobo.notifications.timetable;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public final class TimetableUtility {

    public static final String LOG_TAG = TimetableUtility.class.getName();

    private TimetableUtility() {
    }


    public static String doEverything(String requestUrl){
        URL url = createUrl(requestUrl);

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return jsonResponse;
    }

    public static List<TimetableItem> extractTimetable(String jsonResponse, String dayPreference) {

        if (jsonResponse == null || TextUtils.isEmpty(jsonResponse)) {
            Log.e("mylogmessage", "No data in timetable json");
            return null;
        }

        List<TimetableItem> timetableData = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);

            JSONArray day = jsonObject.getJSONArray(dayPreference);

            for (int j = 0; j < day.length(); j++) {

                JSONObject currentSubject = day.getJSONObject(j);

                String subject = currentSubject.getString("subject");

                String time = currentSubject.getString("time");

                timetableData.add(new TimetableItem(time, subject));
            }

        } catch (JSONException e) {
            Log.e("mylogmessage", "Problem parsing the timetable JSON results", e);
        }

        Log.e("mylogmessage", timetableData.toString());

        return timetableData;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving json result", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder outputJson = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                outputJson.append(line);
                line = bufferedReader.readLine();
            }
        }
        return outputJson.toString();
    }

}