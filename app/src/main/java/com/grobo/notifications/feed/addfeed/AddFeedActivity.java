package com.grobo.notifications.feed.addfeed;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.grobo.notifications.R;
import com.grobo.notifications.feed.FeedDetailFragment;
import com.grobo.notifications.network.FeedRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class AddFeedActivity extends AppCompatActivity implements AddFeedFragment.OnFeedPreviewListener {

    String feedPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        getSupportActionBar().setTitle("Post new feed");

        feedPoster = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_MONGO_ID, "");

        setBaseFragment(savedInstanceState);
    }

    private void setBaseFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.frame_layout_add_feed) != null) {

            if (savedInstanceState != null) {
                return;
            }
            AddFeedFragment firstFragment = new AddFeedFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_add_feed, firstFragment).commit();
        }
    }

    String title;
    String description;
    String image;
    String venue;
    String fb;
    String twitter;
    String inst;
    String coordinators;
    long date;

    private void post() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading");
        dialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("feedPoster", feedPoster);
        jsonParams.put("eventDate", date);
        jsonParams.put("eventVenue", venue);
        jsonParams.put("eventName", title);
        jsonParams.put("eventDescription", description);
        jsonParams.put("eventImageUrl", image);

        String[] pos = {fb, inst, twitter};
        jsonParams.put("postLinks", pos);

        String[] array = coordinators.split(",");
        jsonParams.put("coordinators", array);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        FeedRoutes service = RetrofitClientInstance.getRetrofitInstance().create(FeedRoutes.class);
        Call<Void> call = service.postFeed(token, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddFeedActivity.this, "Feed Successfully posted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("failure", String.valueOf(response.code()));
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("failure", t.getMessage());
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(AddFeedActivity.this, "Post failed, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFeedPreview(String title, String description, String venue, long date, String image, String fb, String inst, String twitter, String coordinators) {

        this.title = title;
        this.description = description;
        this.image = image;
        this.coordinators = coordinators;
        this.date = date;
        this.venue = venue;
        this.fb = fb;
        this.twitter = twitter;
        this.inst = inst;

        Fragment next = new FeedDetailFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        args.putString("venue", venue);
        args.putLong("date", date);
        args.putString("image", image);
        args.putString("fb", fb);
        args.putString("inst", inst);
        args.putString("twitter", twitter);
        args.putString("fb", fb);
        next.setArguments(args);
        showFragmentWithTransition(next);
    }

    private void showFragmentWithTransition(Fragment newFragment) {

        Fragment current = getSupportFragmentManager().findFragmentById(R.id.frame_layout_add_feed);
        current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_add_feed, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_feed_action_save) {
            if (getSupportFragmentManager().findFragmentById(R.id.frame_layout_add_feed) instanceof FeedDetailFragment) {
                showUnsavedChangesDialog();
            } else {
                Toast.makeText(AddFeedActivity.this, "Please preview the feed first", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation Dialog");
        builder.setMessage("Posting this feed... Please confirm!!");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                post();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
