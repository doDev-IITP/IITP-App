package com.grobo.notifications.main;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;
import com.grobo.notifications.network.MessRoutes;
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

import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;

public class MessFragment extends Fragment {

    public MessFragment() {
    }

    private ProgressDialog progressDialog;
    private LinearLayout messLL;
    private LinearLayout messSelectionLL;
    private TextView selectedMess;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_mess, container, false);

        messLL = rootView.findViewById(R.id.ll_mess_details);
        messSelectionLL = rootView.findViewById(R.id.ll_mess_selection);

        selectedMess = rootView.findViewById(R.id.mess_selected_mess);

        progressDialog.setMessage("Loading Data...");
        progressDialog.show();
        populateData();

        return rootView;
    }

    private void populateData() {

        MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create(MessRoutes.class);

        String userId = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_MONGO_ID, "");

        Call<ResponseBody> call = service.getMessData(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String json = null;
                    try {
                        json = response.body().string();
                        parseJson(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("json", json);
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Mess data not found.", Toast.LENGTH_SHORT).show();
                    messSelectionLL.setVisibility(View.VISIBLE);
                } else {
                    Log.e("json", "failed");
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("failure", t.getMessage());
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }
        });

    }

    private void parseJson(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject mess = jsonObject.getJSONObject("mess");

            JSONArray cancelledMeals = mess.getJSONArray("cancelledMeals");
            List<String> foodData = new ArrayList<>();
            for (int k = 0; k < cancelledMeals.length(); k++) {
                foodData.add(cancelledMeals.getString(k));
            }

            String messString = null;
            switch (mess.getInt("messChoice")) {
                case 1:
                    messString = "BH1 Mess 1";
                    break;
                case 2:
                    messString = "BH1 Mess 2";
                    break;
                case 3:
                    messString = "BH2 Mess";
                    break;
                case 4:
                    messString = "GH Mess";
                    break;
            }
            selectedMess.setText(messString);


            messLL.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();


    }
}

