package com.grobo.notifications.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.grobo.notifications.R;
import com.grobo.notifications.network.OtherRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

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

import static com.grobo.notifications.utils.Constants.BASE_URL;
import static com.grobo.notifications.utils.Constants.IS_ADMIN;
import static com.grobo.notifications.utils.Constants.IS_QR_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.IS_TT_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.PHONE_NUMBER;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.USER_NAME;
import static com.grobo.notifications.utils.Constants.USER_POR;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;
import static com.grobo.notifications.utils.Constants.USER_YEAR;
import static com.grobo.notifications.utils.Constants.WEBMAIL;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }

    private SharedPreferences prefs;
    private OnLogoutCallback callback;
    private Context context;
    private PORRecyclerAdapter adapter;

    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        if (getContext() != null)
            context = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView email = view.findViewById(R.id.tv_profile_email);
        email.setText(prefs.getString(WEBMAIL, WEBMAIL));

        TextView name = view.findViewById(R.id.tv_profile_name);
        name.setText(prefs.getString(USER_NAME, USER_NAME));

        ImageView profilePic = view.findViewById(R.id.iv_profile_dp);
        Glide.with(this)
                .load(BASE_URL + "img/" + prefs.getString(ROLL_NUMBER, ROLL_NUMBER).toLowerCase() + ".jpg")
                .centerCrop()
                .placeholder(R.drawable.profile_photo)
                .into(profilePic);

        TextView phone = view.findViewById(R.id.tv_profile_phone);
        phone.setText(prefs.getString(PHONE_NUMBER, PHONE_NUMBER));

        Button button = view.findViewById(R.id.profile_logout_button);
        button.setOnClickListener(v -> logout());

        recyclerView = view.findViewById(R.id.rv_pors);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PORRecyclerAdapter((PORRecyclerAdapter.OnPORSelectedListener)context);
        recyclerView.setAdapter(adapter);

        updateData();
    }

    private void updateData() {

        if (getView() != null) {

            ProgressBar porProgressBar = getView().findViewById(R.id.progress_bar_pors);
            TextView porListHeader = getView().findViewById(R.id.tv_positions_header);

            OtherRoutes service = RetrofitClientInstance.getRetrofitInstance().create(OtherRoutes.class);

            String token = prefs.getString(USER_TOKEN, "");
            String userId = prefs.getString(USER_MONGO_ID, "");

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
                                        int access = por.getInt("access");
                                        String position = por.getString("position");

                                        porItemList.add(new PORItem(porId, clubId, clubName, access, position));
                                    }

                                    adapter.setItemList(porItemList);

                                    recyclerView.setVisibility(View.VISIBLE);
                                    porListHeader.setVisibility(View.VISIBLE);
                                    porProgressBar.setVisibility(View.GONE);

                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    porListHeader.setVisibility(View.GONE);
                                    porProgressBar.setVisibility(View.GONE);
                                }

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    if (t.getMessage() != null)
                        Log.e("failure", t.getMessage());
                    Toast.makeText(context, "Update failed!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void logout() {

        FirebaseMessaging fcm = FirebaseMessaging.getInstance();
        fcm.unsubscribeFromTopic(prefs.getString(USER_BRANCH, "junk"));
        fcm.unsubscribeFromTopic(prefs.getString(USER_YEAR, "junk"));
        fcm.unsubscribeFromTopic(prefs.getString(USER_YEAR, "junk") + prefs.getString(USER_BRANCH, ""));
        fcm.unsubscribeFromTopic(prefs.getString(ROLL_NUMBER, "junk"));

        prefs.edit().putString(WEBMAIL, "")
                .putString(ROLL_NUMBER, "")
                .putString(USER_TOKEN, "")
                .putBoolean(LOGIN_STATUS, false)
                .putString(USER_NAME, "")
                .putString(PHONE_NUMBER, "")
                .putString("jsonString", "")
                .putBoolean(IS_QR_DOWNLOADED, false)
                .putBoolean(IS_TT_DOWNLOADED, false)
                .putBoolean(IS_ADMIN, false)
                .putString(USER_POR, "")
                .putInt("mess_choice", 0)
                .apply();

        callback.onLogout();
    }

    interface OnLogoutCallback {
        void onLogout();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLogoutCallback) {
            callback = (OnLogoutCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }


}
