package com.grobo.notifications.account;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.grobo.notifications.utils.Constants.BASE_URL;
import static com.grobo.notifications.utils.Constants.LOGIN_FAILED;
import static com.grobo.notifications.utils.Constants.USER_NOT_REGISTERED;

public class NetworkFragment extends Fragment {

    private static final String TAG = "NetworkFragment";
    private LoginCallback callback;

    private LoginTask newLoginTask;

    public NetworkFragment() {}

    public static NetworkFragment getInstance(FragmentManager fragmentManager) {
        NetworkFragment networkFragment = new NetworkFragment();
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void login(String email, String password){
        newLoginTask = new LoginTask(callback);
        newLoginTask.execute(email, password);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (LoginCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }



    public interface LoginCallback {
        void onLoginSuccess(String response);
    }

    private static class LoginTask extends AsyncTask<String, Void, String> {

        private LoginCallback callback;

        LoginTask(LoginCallback callback){
            this.callback = callback;
        }
        void setCallback(LoginCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url, cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url);
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    })
                    .build();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "user_login=" + strings[0] + "&user_password=" + strings[1] + "&selected_server=172.16.1.222%3A995%2Fpop3%2Fssl%2Fnovalidate-cert");

            Request request = new Request.Builder()
                    .url(BASE_URL + "qrcode/stud_check.php")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            try(Response response = client.newCall(request).execute()) {

                if (response.body() != null) {

                    int loginCheck = loginCheck(response.body().string());

                    if (loginCheck == 1){

                        Headers headers = response.headers();
                        Request request1 = new Request.Builder()
                                .url( BASE_URL + "qrcode/update.php")
                                .addHeader("Cookie", Objects.requireNonNull(headers.get("Set-Cookie")))
                                .build();

                        try (Response response1 = client.newCall(request1).execute()){

                            if (response1.body() != null){
                                return response1.body().string();
                            }

                        } catch (IOException e){
                            e.printStackTrace();
                        }

                    } else if (loginCheck == 0){
                        return USER_NOT_REGISTERED;
                    } else {
                        return LOGIN_FAILED;
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                callback.onLoginSuccess(response);
            }
        }

        private int loginCheck(String response){
            if(response.contains("<a href=\"logout.php\">LOGOUT</a>") && response.contains("Click here to download the QR code")) {
                return 1;
            } else if (response.contains("<a href=\"logout.php\">LOGOUT</a>") && response.contains("Click here for Registration")){
                return 0;
            }
            return -1;
        }
    }
}
