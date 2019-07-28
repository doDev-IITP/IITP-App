package com.grobo.notifications.main;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;
import com.grobo.notifications.network.MessRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
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
    private String cancelMealString;
    private Spinner spinner;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(getActivity()!=null)
        getActivity().setTitle( "Mess" );
        super.onViewCreated( view, savedInstanceState );
    }

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

        final EditText cancelMealDate = rootView.findViewById(R.id.cancel_meal_select_date);
        cancelMealDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePickerDialog(cancelMealDate).show();
            }
        });

        spinner = rootView.findViewById(R.id.cancel_meal_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_items, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        Button cancelMealButton = rootView.findViewById(R.id.cancel_meal_ok_button);
        cancelMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelMealFunction();
            }
        });

        populateData();

        return rootView;
    }

    private void populateData() {

        progressDialog.setMessage("Loading Data...");
        progressDialog.show();

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
                        parseMessJson(json);
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

    private void parseMessJson(String json) {

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

    private void cancelMealFunction() {

        progressDialog.setMessage("Cancelling Meal...");
        progressDialog.show();

        MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create(MessRoutes.class);

        String userId = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_MONGO_ID, "");
        String meal = null;
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                meal = "1";
                break;
            case 1:
                meal = "2";
                break;
            case 2:
                meal = "3";
                break;
            case 3:
                meal = "4";
                break;
        }
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("currentMeal", cancelMealString + "_" + meal + "_-1");
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

        Call<ResponseBody> call = service.cancelMeal(userId, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("mess response", response.toString());
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    String message = object.getString("message");

                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("failure", t.getMessage());
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(getContext(), "Meal cancellation failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private DatePickerDialog getDatePickerDialog(final EditText cancelMealDate) {
        final Calendar calendar = Calendar.getInstance();

        return new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

//                if (day  ) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault());
                cancelMealDate.setText(dateFormat.format(calendar.getTime()));
                cancelMealString = dayOfMonth + "_" + month + "_" + year;
//                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}

