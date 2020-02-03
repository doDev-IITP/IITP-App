package com.grobo.notifications.clubs;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.grobo.notifications.network.ClubRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class ClubUtils {

    public static void followClub(Context context, String clubId) {
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_TOKEN, "0");

        ClubRoutes service = RetrofitClientInstance.getRetrofitInstance().create(ClubRoutes.class);
        Call<ResponseBody> call = service.followClub(token, clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 201)
                    Toast.makeText(context, "Followed", Toast.LENGTH_SHORT).show();
                else if (response.code() == 202)
                    Toast.makeText(context, "Unfollowed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

}
