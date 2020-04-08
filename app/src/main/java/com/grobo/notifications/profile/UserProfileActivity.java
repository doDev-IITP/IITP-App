package com.grobo.notifications.profile;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.account.por.PORRecyclerAdapter;
import com.grobo.notifications.network.PorRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.network.UserRoutes;
import com.grobo.notifications.utils.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class UserProfileActivity extends AppCompatActivity implements PORRecyclerAdapter.OnPORSelectedListener {

    private PORRecyclerAdapter adapter;

    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private UserProfileItem currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getWindow().setStatusBarColor(Color.parseColor("#185a9d"));
        findViewById(R.id.back_button).setOnClickListener(v -> UserProfileActivity.super.onBackPressed());

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);

        if (getIntent() != null && getIntent().hasExtra("user_id"))
            getUserDetails(getIntent().getStringExtra("user_id"));
        else
            utils.showSimpleAlertDialog(this, "Alert!!!", "User id not found!");
    }

    private void getUserDetails(String userId) {

        progressDialog.setMessage("Loading profile...");
        progressDialog.show();

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "");

        UserRoutes service = RetrofitClientInstance.getRetrofitInstance().create(UserRoutes.class);
        Call<UserProfileItem> call = service.getUserById(token, userId);

        call.enqueue(new Callback<UserProfileItem>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileItem> call, @NonNull Response<UserProfileItem> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    showUserDetails();
                } else {
                    Toast.makeText(UserProfileActivity.this, "Load failed!! Pls check internet connection.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileItem> call, @NonNull Throwable t) {
                if (progressDialog != null) progressDialog.dismiss();
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                Toast.makeText(UserProfileActivity.this, "Load failed!! Pls check internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showUserDetails() {

        if (currentUser != null) {
            TextView email = findViewById(R.id.tv_profile_email);
            email.setText(currentUser.getEmail());

            TextView name = findViewById(R.id.tv_profile_name);
            name.setText(currentUser.getName());

            ImageView profilePic = findViewById(R.id.iv_profile_dp);
            Glide.with(this)
                    .load(currentUser.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.profile_photo)
                    .into(profilePic);

            TextView phone = findViewById(R.id.tv_profile_phone);
            phone.setText(currentUser.getPhone());

            TextView rollNumber = findViewById(R.id.tv_profile_roll);
            rollNumber.setText(currentUser.getInstituteId());

            recyclerView = findViewById(R.id.rv_pors);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new PORRecyclerAdapter(this);
            recyclerView.setAdapter(adapter);

            getPorData();
        } else {
            utils.showSimpleAlertDialog(this, "Alert!!!", "User details not found!");
        }
    }

    private void getPorData() {

        ProgressBar porProgressBar = findViewById(R.id.progress_bar_pors);
        TextView textNoPor = findViewById(R.id.tv_no_por);

        PorRoutes service = RetrofitClientInstance.getRetrofitInstance().create(PorRoutes.class);

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "");
        String userId = currentUser.getId();

        Call<ResponseBody> call = service.getPorByUser(token, userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        try {
                            String data = response.body().string();

                            JSONObject mainObject = new JSONObject(data);
                            JSONArray pors = mainObject.getJSONArray("pors");

                            if (pors.length() > 0) {

                                List<PORItem> porItemList = new ArrayList<>();

                                for (int i = 0; i < pors.length(); i++) {
                                    JSONObject por = pors.getJSONObject(i);

                                    String porId = por.getString("_id");
                                    JSONObject club = por.getJSONObject("club");
                                    String clubId = club.getString("_id");
                                    String clubName = club.getString("name");
                                    int code = por.getInt("code");
                                    String position = por.getString("position");

                                    JSONArray array = por.getJSONArray("access");
                                    List<Integer> access = new ArrayList<>();
                                    for (int j = 0; j < array.length(); j++) access.add(array.getInt(j));

                                    porItemList.add(new PORItem(porId, clubId, clubName, code, position, access));
                                }

                                adapter.setItemList(porItemList);

                                recyclerView.setVisibility(View.VISIBLE);
                                textNoPor.setVisibility(View.GONE);

                            } else {
                                recyclerView.setVisibility(View.GONE);
                                textNoPor.setVisibility(View.VISIBLE);
                            }

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else
                    Toast.makeText(UserProfileActivity.this, "Update failed!!", Toast.LENGTH_SHORT).show();

                porProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                Toast.makeText(UserProfileActivity.this, "Update failed!!", Toast.LENGTH_SHORT).show();
                porProgressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onPORSelected(PORItem porItem) {

    }
}