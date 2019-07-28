package com.grobo.notifications.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.grobo.notifications.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class utils {

    public static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(56), rnd.nextInt(256));
    }

    public static boolean getWifiInfo(Context context) {

        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {

                    Log.e("wifiinfo", wifiInfo.getSSID());
                    if (wifiInfo.getSSID().equals("\"TESTING\"")) return true;
                    else
                        Toast.makeText(context, "Please connect to IITP network", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Wifi not connected, please connect to IITP network", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context, "Wifi not enabled, please connect to IITP network", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean wifiLogin(Context context) {

        String portal = "22483910-82dd-11e5-9e1e-74a2e6a350fe";

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        final OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();


        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {

                Log.e("wifilogin", "starting login");

                MediaType mediaType1 = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
                RequestBody body1 = RequestBody.create(mediaType1, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"user.username\"\r\n\r\n1801ee03\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"user.password\"\r\n\r\n05-05-2000\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"portal\"\r\n\r\n22483910-82dd-11e5-9e1e-74a2e6a350fe\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
                Request request1 = new Request.Builder()
                        .url("https://172.16.98.250:8443/portal/LoginSubmit.action")
                        .post(body1)
                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                        .build();

                try (Response response1 = client.newCall(request1).execute()) {
                    Log.e("wifilogin1", String.valueOf(response1.code()));
                    Log.e("wifilogin1", response1.body().string());

                    if (response1.code() == 200) {
                        MediaType mediaType2 = MediaType.parse("application/x-www-form-urlencoded");
                        RequestBody body2 = RequestBody.create(mediaType2, "portal=22483910-82dd-11e5-9e1e-74a2e6a350fe&undefined=");
                        Request request2 = new Request.Builder()
                                .url("https://172.16.98.250:8443/portal/Continue.action?from=POST_ACCESS_BANNER")
                                .post(body2)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .build();
                        Response response2 = client.newCall(request2).execute();

                        Log.e("wifilogin2", String.valueOf(response2.code()));
                        Log.e("wifilogin1", response2.body().string());

//                            Log.e("wifilogin3", response1.body().string());


                    }


                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.

                execute();

        return false;
    }

    public static void openPlayStoreForApp(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context
                            .getResources()
                            .getString(R.string.app_market_link) + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context
                            .getResources()
                            .getString(R.string.app_google_play_store_link) + appPackageName)));
        }
    }

    public static String loadJSONFromAsset(Context context, String jsonFileName)
            throws IOException {

        AssetManager manager = context.getAssets();
        InputStream is = manager.open(jsonFileName);

        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, StandardCharsets.UTF_8);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean saveImage(Context context, Bitmap finalBitmap, String dir, String fileName) {

        File childDir = new File(context.getFilesDir(), dir);
        if (!childDir.exists()) {
            childDir.mkdirs();
        }

        File file = new File (childDir, fileName);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getAppVersion(Context context) {
        String vName = null;
        try {
            vName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return vName;
    }

    public static class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... imgUrls) {

            URL url;
            HttpURLConnection connection;

            try {
                url = new URL(imgUrls[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                return BitmapFactory.decodeStream(stream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

