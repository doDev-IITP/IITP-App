package com.grobo.notifications.feed;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grobo.notifications.R;
import com.grobo.notifications.network.GetDataService;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.DatePickerHelper;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class AddFeedActivity extends AppCompatActivity {

    EditText title;
    EditText description;
    EditText venue;
    EditText image;
    EditText coordinators;
    EditText fb;
    EditText inst;
    EditText twitter;
    Button postFeed;
    EditText date;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        getSupportActionBar().setTitle("Post new feed");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        title = findViewById(R.id.add_feed_title);
        description = findViewById(R.id.add_feed_description);
        venue = findViewById(R.id.add_feed_venue);
        image = findViewById(R.id.add_feed_image);
        coordinators = findViewById(R.id.add_feed_coordinators);
        fb = findViewById(R.id.add_feed_fb);
        inst = findViewById(R.id.add_feed_inst);
        twitter = findViewById(R.id.add_feed_twitter);
        postFeed = findViewById(R.id.post_button);
        date = findViewById(R.id.add_feed_date);

        final DatePickerHelper dateHelper = new DatePickerHelper(AddFeedActivity.this, date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateHelper.getDatePickerDialog().show();
            }
        });
        long eventDate = dateHelper.getTimeInMillisFromCalender();

        postFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long eventDate = dateHelper.getTimeInMillisFromCalender();
                if (validateFeed(eventDate)) {
                    post(eventDate);
                }
            }
        });
    }

    private void post(long eventDate) {

        Map<String, Object> jsonParams = new ArrayMap<>();

        jsonParams.put("feedPoster", prefs.getString(ROLL_NUMBER, "0"));
        jsonParams.put("eventDate", eventDate);
        jsonParams.put("eventVenue", venue.getText().toString());
        jsonParams.put("eventName", title.getText().toString());
        jsonParams.put("eventDescription", description.getText().toString());

        String[] pos = {fb.getText().toString(), inst.getText().toString(), twitter.getText().toString()};
        jsonParams.put("postLinks", pos);

        String[] array = coordinators.getText().toString().split(",");
        jsonParams.put("coordinators", array);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Void> call = service.postFeed(token, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddFeedActivity.this, "Feed Successfully posted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("failure", t.getMessage());
            }
        });
    }


    private boolean validateFeed(long time) {
        boolean valid = true;

        String t = title.getText().toString();
        String d = description.getText().toString();
        String v = venue.getText().toString();

        if (t.isEmpty()) {
            title.setError("Please enter a valid title");
            valid = false;
        } else {
            title.setError(null);
        }

        if (d.isEmpty()) {
            description.setError("Please enter a description");
            valid = false;
        } else {
            description.setError(null);
        }

        if (time < System.currentTimeMillis()) {
            date.setError("Please enter a value greater than current time");
            valid = false;
        } else {
            date.setError(null);
        }

        if (v.isEmpty()) {
            venue.setError("Please enter a valid venue");
            valid = false;
        } else {
            venue.setError(null);
        }
        return valid;
    }


}
