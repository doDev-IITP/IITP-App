package com.grobo.notifications.admin.clubevents;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Spanned;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.network.EventsRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.utils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class EditClubEventDetailActivity extends FragmentActivity {

    private ProgressDialog progressDialog;

    private ClubEventItem current;
    private PORItem currentPor;

    private boolean editMode = false;

    private static final int SELECT_PICTURE = 783;
    private Bitmap selectedImage;

    private ImageView imagePreview;
    private EditText eventTitle;
    private EditText eventDescription;
    private EditText eventDate;
    private EditText eventVenue;
    private MaterialButton previewDescription;
    private String fbLink = null;
    private String instLink = null;
    private String twitterLink = null;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    private String originalDescription = "";
    private boolean previewMode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club_event_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        calendar = Calendar.getInstance();

        if (getIntent().hasExtra("por")) {
            currentPor = getIntent().getParcelableExtra("por");
            if (currentPor != null) {

                if (getIntent().hasExtra("eventId")) {
                    String eventId = getIntent().getStringExtra("eventId");
                    editMode = true;
                    downloadEventData(eventId);
                } else {
                    editMode = false;
                    showCreateData();
                }

            } else utils.showFinishAlertDialog(this, "Alert!!!", "POR data not found.");
        } else utils.showFinishAlertDialog(this, "Alert!!!", "POR not found.");
    }

    private void showCreateData() {

        imagePreview = findViewById(R.id.image_preview);

        eventTitle = findViewById(R.id.edit_event_title);
        eventDescription = findViewById(R.id.edit_event_description);
        eventVenue = findViewById(R.id.edit_event_venue);
        eventDate = findViewById(R.id.edit_event_date);

        Button imageSelector = findViewById(R.id.button_select_image);
        imageSelector.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_PICTURE);
        });

        ImageView fb = findViewById(R.id.event_facebook);
        fb.setOnClickListener(v -> showLinkDialog(1));

        ImageView inst = findViewById(R.id.event_instagram);
        inst.setOnClickListener(v -> showLinkDialog(2));

        ImageView twitter = findViewById(R.id.event_twitter);
        twitter.setOnClickListener(v -> showLinkDialog(3));

        Button saveEventButton = findViewById(R.id.button_save_event);
        saveEventButton.setOnClickListener(v -> {
            if (eventTitle.getText().toString().isEmpty()) {
                eventTitle.setError("Enter a title!");
            } else if (eventDescription.getText().toString().isEmpty()) {
                eventDescription.setError("Enter a valid description!");
            } else if (eventVenue.getText().toString().isEmpty()) {
                eventVenue.setError("Enter a valid venue!");
            } else showPostDialog();
        });

        eventDate.setOnClickListener(view1 -> setDate());

        activatePreviewButton();
    }

    private void showEditData() {
        if (current != null) {

            imagePreview = findViewById(R.id.image_preview);
            Glide.with(this)
                    .load("")
                    .thumbnail(Glide.with(this).load(current.getImageUrl()))
                    .centerInside()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(imagePreview);

            eventTitle = findViewById(R.id.edit_event_title);
            eventTitle.setText(current.getName());
            eventDescription = findViewById(R.id.edit_event_description);
            eventDescription.setText(current.getDescription());
            eventVenue = findViewById(R.id.edit_event_venue);
            eventVenue.setText(current.getVenue());

            Button imageSelector = findViewById(R.id.button_select_image);
            imageSelector.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            });

            ImageView fb = findViewById(R.id.event_facebook);
            fb.setOnClickListener(v -> showLinkDialog(1));

            ImageView inst = findViewById(R.id.event_instagram);
            inst.setOnClickListener(v -> showLinkDialog(2));

            ImageView twitter = findViewById(R.id.event_twitter);
            twitter.setOnClickListener(v -> showLinkDialog(3));

            Button saveEventButton = findViewById(R.id.button_save_event);
            saveEventButton.setOnClickListener(v -> {
                if (eventTitle.getText().toString().isEmpty()) {
                    eventTitle.setError("Enter a title!");
                } else if (eventDescription.getText().toString().isEmpty()) {
                    eventDescription.setError("Enter a valid description!");
                } else if (eventVenue.getText().toString().isEmpty()) {
                    eventVenue.setError("Enter a valid venue!");
                } else showPostDialog();
            });

            if (current.getPostLinks() != null) for (String page : current.getPostLinks()) {
                if (page.contains("facebook")) fbLink = page;
                else if (page.contains("instagram")) instLink = page;
                else if (page.contains("twitter")) twitterLink = page;
            }

            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            eventDate = findViewById(R.id.edit_event_date);
            eventDate.setText(format.format(current.getDate()));
            calendar.setTimeInMillis(current.getDate());
            eventDate.setOnClickListener(view1 -> setDate());

            activatePreviewButton();

        } else utils.showSimpleAlertDialog(this, "Alert!!!", "Event not found.");
    }

    private void activatePreviewButton() {
        previewDescription = findViewById(R.id.button_preview_description);
        previewDescription.setOnClickListener(v -> {
            if (!previewMode) {
                originalDescription = eventDescription.getText().toString();
                if (!originalDescription.isEmpty()) {
                    final Markwon markwon = Markwon.builder(this)
                            .usePlugin(GlideImagesPlugin.create(this))
                            .usePlugin(HtmlPlugin.create())
                            .build();
                    final Spanned spanned = markwon.toMarkdown(originalDescription);
                    markwon.setParsedMarkdown(eventDescription, spanned);
                    previewDescription.setText("Back to edit");
                    eventDescription.setEnabled(false);
                    previewMode = true;
                }
            } else {
                eventDescription.setText(originalDescription);
                previewDescription.setText("Preview");
                eventDescription.setEnabled(true);
                previewMode = false;
            }
        });
    }

    private void downloadEventData(String eventId) {

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        EventsRoutes service = RetrofitClientInstance.getRetrofitInstance().create(EventsRoutes.class);

        Call<ClubEventItem> call = service.getEventById(token, eventId);
        call.enqueue(new Callback<ClubEventItem>() {
            @Override
            public void onResponse(@NonNull Call<ClubEventItem> call, @NonNull Response<ClubEventItem> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) current = response.body();
                showEditData();
            }

            @Override
            public void onFailure(@NonNull Call<ClubEventItem> call, @NonNull Throwable t) {
                if (progressDialog != null) progressDialog.dismiss();
                showEditData();
            }
        });
    }

    private void showLinkDialog(int type) {

        View custom = getLayoutInflater().inflate(R.layout.dialog_enter_text, null);
        EditText editText = custom.findViewById(R.id.enter_text);

        switch (type) {
            case 1:
                editText.setHint("Enter facebook URL");
                if (fbLink != null) editText.setText(fbLink);
                break;
            case 2:
                editText.setHint("Enter instagram URL");
                if (instLink != null) editText.setText(instLink);
                break;
            case 3:
                editText.setHint("Enter twitter URL");
                if (twitterLink != null) editText.setText(twitterLink);
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(custom);
        builder.setPositiveButton("OK", (dialog, which) -> {
            switch (type) {
                case 1:
                    fbLink = editText.getText().toString();
                    break;
                case 2:
                    instLink = editText.getText().toString();
                    break;
                case 3:
                    twitterLink = editText.getText().toString();
                    break;
            }
        });
        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(true);

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Uri returnUri = data.getData();

                        Bitmap tempImage = MediaStore.Images.Media.getBitmap(getContentResolver(), returnUri);

                        Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                        if (returnCursor != null) {
                            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();

                            long imageSize = returnCursor.getLong(sizeIndex);
                            if (imageSize > 2 * 1000 * 1000) {
                                utils.showSimpleAlertDialog(this, "Alert!!!", "Please select an image with size less than 2 MB !");
                            } else {
                                imagePreview.setImageBitmap(tempImage);
                                selectedImage = tempImage;
                            }

                            returnCursor.close();
                        }

                    } catch (Exception e) {
                        utils.showSimpleAlertDialog(this, "Alert!!!", "Image reading error!");
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Canceled!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPostDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation Dialog")
                .setMessage("Saving event... Please confirm!!")
                .setPositiveButton("Confirm", (dialog, which) -> postImage())
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void postImage() {

        if (selectedImage == null) {
            if (current != null && current.getImageUrl() != null && !current.getImageUrl().isEmpty())
                post(current.getImageUrl());
            else post("");
        } else {

            progressDialog.setMessage("Uploading image...");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(0);
            progressDialog.show();

            Random r = new Random();
            int i = (r.nextInt(90000) + 9999);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

            StorageReference storageRef = firebaseStorage.getReference().child(String.format("events/event%s%s.jpg", String.valueOf(System.currentTimeMillis()), String.valueOf(i)));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] newImage = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(newImage);
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.e("progress", "Upload is " + progress + "% done");
                progressDialog.setProgress((int) progress);
            });

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful() && task.getException() != null) throw task.getException();
                return storageRef.getDownloadUrl();
            }).addOnSuccessListener(imageUrl -> {
                post(imageUrl.toString());
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                utils.showSimpleAlertDialog(this, "Alert!", "File upload failed !!!");
            });
        }
    }

    private void post(String imageUrl) {

        progressDialog.setMessage("Saving event details...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("name", eventTitle.getText().toString());

        if (previewMode) jsonParams.put("description", originalDescription);
        else jsonParams.put("description", eventDescription.getText().toString());

        jsonParams.put("venue", eventVenue.getText().toString());
        jsonParams.put("imageUrl", imageUrl);
        jsonParams.put("relatedClub", currentPor.getClubId());
        jsonParams.put("date", calendar.getTimeInMillis());

        List<String> social = new ArrayList<>();
        if (fbLink != null && !fbLink.isEmpty()) social.add(fbLink);
        if (instLink != null && !instLink.isEmpty()) social.add(instLink);
        if (twitterLink != null && !twitterLink.isEmpty()) social.add(twitterLink);
        jsonParams.put("postLinks", social);

        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        EventsRoutes service = RetrofitClientInstance.getRetrofitInstance().create(EventsRoutes.class);

        Call<ResponseBody> call;
        if (editMode) call = service.patchEventById(token, body, current.getId());
        else call = service.postEvent(token, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    utils.showFinishAlertDialog(EditClubEventDetailActivity.this, "Alert!!!", "Event saved.");
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Log.e("failure", response.code() + response.errorBody().string());
                            utils.showSimpleAlertDialog(EditClubEventDetailActivity.this, "Alert!!!", response.errorBody().string() + " Error: " + response.code());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(EditClubEventDetailActivity.this, "Save failed, please check internet connection", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
        calendar.set(year, month, dayOfMonth);
        eventDate.setText(dateFormat.format(calendar.getTime()));
        setTime();
    };

    private void setTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        eventDate.setText(dateFormat.format(calendar.getTime()));
    };
}
