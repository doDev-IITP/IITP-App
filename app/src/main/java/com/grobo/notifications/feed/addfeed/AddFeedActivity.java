package com.grobo.notifications.feed.addfeed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grobo.notifications.R;
import com.grobo.notifications.feed.FeedItem;
import com.grobo.notifications.feed.FeedViewModel;
import com.grobo.notifications.network.FeedRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.utils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_NAME;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class AddFeedActivity extends FragmentActivity {

    private static final int SELECT_PICTURE = 783;

    private Bitmap selectedImage;

    private SharedPreferences preferences;
    private ProgressDialog progressDialog;

    private ImageView imagePreview;
    private TextView feedTitle;
    private TextView feedDescription;
    private MaterialButton previewDescription;
    private String fbLink = null;
    private String instLink = null;
    private String twitterLink = null;

    private String originalDescription = "";
    private boolean previewMode = false;

    private boolean editMode = false;
    private FeedViewModel viewModel;
    private FeedItem currentFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");

        if (getIntent().hasExtra("feedId")) {
            String feedId = getIntent().getStringExtra("feedId");
            if (feedId != null) {
                currentFeed = viewModel.getFeedById(feedId);
                if (currentFeed != null) editMode = true;
                else utils.showFinishAlertDialog(this, "Alert!!!", "Invalid feed!");
            } else utils.showFinishAlertDialog(this, "Alert!!!", "Invalid feed id!");
        } else {
            editMode = false;
        }

        showInitialData();
    }

    private void showInitialData() {
        TextView feedPoster = findViewById(R.id.feed_poster);
        feedPoster.setText(String.format("%s\n%s", preferences.getString(USER_NAME, ""), preferences.getString(ROLL_NUMBER, "").toUpperCase()));

        imagePreview = findViewById(R.id.image_preview);
        feedTitle = findViewById(R.id.add_feed_title);
        feedDescription = findViewById(R.id.add_feed_description);

        Button imageSelector = findViewById(R.id.button_select_image);
        imageSelector.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_PICTURE);
        });

        ImageView fb = findViewById(R.id.feed_facebook);
        fb.setOnClickListener(v -> showLinkDialog(1));

        ImageView inst = findViewById(R.id.feed_instagram);
        inst.setOnClickListener(v -> showLinkDialog(2));

        ImageView twitter = findViewById(R.id.feed_twitter);
        twitter.setOnClickListener(v -> showLinkDialog(3));

        Button postFeedButton = findViewById(R.id.button_post_feed);
        postFeedButton.setOnClickListener(v -> {
            if (feedTitle.getText().toString().isEmpty()) {
                feedTitle.setError("Enter a title!");
            } else if (feedDescription.getText().toString().isEmpty()) {
                feedDescription.setError("Enter a valid description!");
            } else showPostDialog();
        });

        if (currentFeed != null) {
            Glide.with(this)
                    .load(currentFeed.getEventImageUrl())
                    .centerInside()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(imagePreview);
            feedTitle.setText(currentFeed.getEventName());
            feedDescription.setText(currentFeed.getEventDescription());
            if (currentFeed.getPostLinks() != null) for (String page : currentFeed.getPostLinks()) {
                if (page.contains("facebook")) fbLink = page;
                else if (page.contains("instagram")) instLink = page;
                else if (page.contains("twitter")) twitterLink = page;
            }
        }

        activatePreviewButton();
    }

    private void activatePreviewButton() {
        previewDescription = findViewById(R.id.button_preview_description);
        previewDescription.setOnClickListener(v -> {
            if (!previewMode) {
                originalDescription = feedDescription.getText().toString();
                if (!originalDescription.isEmpty()) {
                    final Markwon markwon = Markwon.builder(this)
                            .usePlugin(GlideImagesPlugin.create(this))
                            .usePlugin(HtmlPlugin.create())
                            .build();
                    final Spanned spanned = markwon.toMarkdown(originalDescription);
                    markwon.setParsedMarkdown(feedDescription, spanned);
                    previewDescription.setText("Back to edit");
                    feedDescription.setEnabled(false);
                    previewMode = true;
                }
            } else {
                feedDescription.setText(originalDescription);
                previewDescription.setText("Preview");
                feedDescription.setEnabled(true);
                previewMode = false;
            }
        });
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
                .setMessage("Posting this feed... Please confirm!!")
                .setPositiveButton("Confirm", (dialog, which) -> postImage())
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void postImage() {

        if (selectedImage == null)
            if (currentFeed != null && currentFeed.getEventImageUrl() != null && !currentFeed.getEventImageUrl().isEmpty())
                post(currentFeed.getEventImageUrl());
            else post("");
        else {

            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(0);
            progressDialog.show();

            Random r = new Random();
            int i = (r.nextInt(90000) + 9999);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

            StorageReference storageRef = firebaseStorage.getReference().child(String.format("feeds/feed%s%s.jpg", String.valueOf(System.currentTimeMillis()), String.valueOf(i)));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] newImage = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(newImage);
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.e("progress", "Upload is " + progress + "% done");
                progressDialog.setProgress((int) progress);
//                progressDialog.show();
            });

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful() && task.getException() != null) throw task.getException();
                return storageRef.getDownloadUrl();
            }).addOnSuccessListener(imageUrl -> {
                post(imageUrl.toString());
            }).addOnFailureListener(e -> {
                utils.showSimpleAlertDialog(this, "Alert!", "File upload failed !!!");
                progressDialog.dismiss();
            });
        }
    }

    private void post(String imageUrl) {

        progressDialog.setIndeterminate(true);
        progressDialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("eventName", feedTitle.getText().toString());
        jsonParams.put("eventImageUrl", imageUrl);

        if (previewMode) jsonParams.put("eventDescription", originalDescription);
        else jsonParams.put("eventDescription", feedDescription.getText().toString());

        List<String> social = new ArrayList<>();
        if (fbLink != null && !fbLink.isEmpty()) social.add(fbLink);
        if (instLink != null && !instLink.isEmpty()) social.add(instLink);
        if (twitterLink != null && !twitterLink.isEmpty()) social.add(twitterLink);
        jsonParams.put("postLinks", social);

        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        String token = preferences.getString(USER_TOKEN, "0");

        FeedRoutes service = RetrofitClientInstance.getRetrofitInstance().create(FeedRoutes.class);

        Call<ResponseBody> call;
        if (editMode) call = service.editFeedById(token, currentFeed.getId(), body);
        else call = service.postFeed(token, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(AddFeedActivity.this, "Feed Successfully saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    utils.showSimpleAlertDialog(AddFeedActivity.this, "Alert!!!", "Post failed! Error: " + response.code());
                    try {
                        Log.e("failure", response.code() + (response.errorBody() != null ? response.errorBody().string() : ""));
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
                Toast.makeText(AddFeedActivity.this, "Post failed, please check internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLinkDialog(int type) {

        View custom = getLayoutInflater().inflate(R.layout.dialog_enter_text, null);
        EditText editText = custom.findViewById(R.id.enter_text);

        switch (type) {
            case 1:
                editText.setHint("Enter facebook post URL");
                if (fbLink != null) editText.setText(fbLink);
                break;
            case 2:
                editText.setHint("Enter instagram post URL");
                if (instLink != null) editText.setText(instLink);
                break;
            case 3:
                editText.setHint("Enter twitter post URL");
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
}
