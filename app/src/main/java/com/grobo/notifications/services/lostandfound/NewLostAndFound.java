package com.grobo.notifications.services.lostandfound;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.grobo.notifications.R;
import com.grobo.notifications.network.LostAndFoundRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class NewLostAndFound extends Fragment implements View.OnClickListener {


    public NewLostAndFound() {
    }

    private ProgressDialog progressDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_lost_found, container, false);

        TextView mdate = view.findViewById(R.id.lost_found_date);
        TextView mtime = view.findViewById(R.id.lost_found_time);

        ImageView date_btn = view.findViewById(R.id.date_set_btn);
        date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        ImageView time_btn = view.findViewById(R.id.time_set_btn);
        time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        //Date Listener
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date_value = dayOfMonth + "/" + month + "/" + year;
                mdate.setText(date_value);


            }
        };

        //Time Listener
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time_value = hourOfDay + ":" + minute;
                mtime.setText(time_value);
            }
        };

        Button postButton = view.findViewById(R.id.post_lost_found);
        postButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        return view;
    }

    private void setTime() {
        Calendar c2 = Calendar.getInstance();
        int hour = c2.get(Calendar.HOUR_OF_DAY);
        int minute = c2.get(Calendar.MINUTE);
        int a_p = c2.get(Calendar.AM_PM);

        TimePickerDialog time_object = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hour, minute, true);
        time_object.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        time_object.show();
    }

    private void setDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DAY_OF_MONTH);

        //Create DatePicker Dialog Object
        DatePickerDialog date_obeject = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, date);
        date_obeject.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        date_obeject.show();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.post_lost_found) {
            validatePost();
        }
    }

    private void validatePost() {
        boolean valid = true;

        TextView name = getView().findViewById(R.id.lost_found_name);
        TextView description = getView().findViewById(R.id.lost_found_description);
        TextView place = getView().findViewById(R.id.lost_found_place);
        TextView time = getView().findViewById(R.id.lost_found_time);
        TextView date = getView().findViewById(R.id.lost_found_date);
        TextView contact = getView().findViewById(R.id.lost_found_contact);
        RadioGroup status = getView().findViewById(R.id.radio_group_lost_found);

        if (name.getText().toString().isEmpty()) {
            name.setError("Please enter a valid title");
            valid = false;
        } else {
            name.setError(null);
        }

        if (place.getText().toString().isEmpty()) {
            place.setError("Please enter a description");
            valid = false;
        } else {
            place.setError(null);
        }

        if (time.getText().toString().isEmpty()) {
            time.setError("Please enter a valid venue");
            valid = false;
        } else {
            time.setError(null);
        }

        if (date.getText().toString().isEmpty()) {
            date.setError("Please enter a valid venue");
            valid = false;
        } else {
            date.setError(null);
        }

        if (contact.getText().toString().isEmpty()) {
            contact.setError("Please enter a valid venue");
            valid = false;
        } else {
            contact.setError(null);
        }

        int statusNumber;
        switch (status.getCheckedRadioButtonId()) {
            case R.id.radio_lost:
                statusNumber = 1;
                break;
            case R.id.radio_found:
                statusNumber = 2;
                break;
            default:
                statusNumber = 0;
        }


        if (valid) {

            progressDialog.setMessage("Posting Lost and found...");
            progressDialog.show();

            String feedPoster = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_MONGO_ID, "");

            Map<String, Object> jsonParams = new ArrayMap<>();
            jsonParams.put("lostnfoundPoster", feedPoster);
            jsonParams.put("date", date.getText().toString());
            jsonParams.put("time", time.getText().toString());
            jsonParams.put("name", name.getText().toString());
            jsonParams.put("place", place.getText().toString());
            jsonParams.put("description", description.getText().toString());
            jsonParams.put("contact", contact.getText().toString());
            jsonParams.put("lostStatus", statusNumber);

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());
            String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");

            LostAndFoundRoutes service = RetrofitClientInstance.getRetrofitInstance().create(LostAndFoundRoutes.class);
            Call<ResponseBody> call = service.postLostNFound(token, body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Posted Successfully.", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    } else {
                        Log.e("failure", response.message());
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getContext(), "Post failed, please try again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
