package com.grobo.notifications.feed;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.grobo.notifications.network.FeedRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class FeedUtils {

    public static void reactOnFeed(Context context, String feedId) {
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_TOKEN, "0");

        FeedRoutes service = RetrofitClientInstance.getRetrofitInstance().create(FeedRoutes.class);
        Call<ResponseBody> call = service.reactOnFeed(token, feedId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 201)
                    Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                else if (response.code() == 202)
                    Toast.makeText(context, "Unliked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

}
