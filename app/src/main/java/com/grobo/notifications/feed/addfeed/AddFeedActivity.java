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
import androidx.preference.PreferenceManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grobo.notifications.R;
import com.grobo.notifications.network.FeedRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.utils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private String fbLink = null;
    private String instLink = null;
    private String twitterLink = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");

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

        if (selectedImage == null) post("placeholder");
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
        jsonParams.put("eventDescription", feedDescription.getText().toString());
        jsonParams.put("eventImageUrl", imageUrl);

        List<String> social = new ArrayList<>();
        if (fbLink != null && !fbLink.isEmpty()) social.add(fbLink);
        if (instLink != null && !instLink.isEmpty()) social.add(instLink);
        if (twitterLink != null && !twitterLink.isEmpty()) social.add(twitterLink);
        jsonParams.put("postLinks", social);

        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        String token = preferences.getString(USER_TOKEN, "0");

        FeedRoutes service = RetrofitClientInstance.getRetrofitInstance().create(FeedRoutes.class);
        Call<ResponseBody> call = service.postFeed(token, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(AddFeedActivity.this, "Feed Successfully posted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("failure", String.valueOf(response.code()));
                    utils.showSimpleAlertDialog(AddFeedActivity.this, "Alert!!!", "Post failed! Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
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


    private void showData() {

//        ImageView imageView = findViewById(R.id.image);
//
//        TextView title = findViewById(R.id.title);
//        TextView description = findViewById(R.id.description);
//        TextView feedPoster = findViewById(R.id.feed_poster);
//
//        if (current != null) {
//
//            Glide.with(this)
//                    .load("")
//                    .thumbnail(Glide.with(this).load(current.getEventImageUrl()))
//                    .centerInside()
//                    .placeholder(R.drawable.baseline_dashboard_24)
//                    .into(imageView);
//
//            title.setText(current.getEventName());
//
//            if (current.getEventDescription() == null) {
//                current.setEventDescription("No Description");
//            }
//
//            feedPoster.setText(String.format("%s\n%s", current.getDataPoster().getName(), current.getDataPoster().getInstituteId().toUpperCase()));
//
//            final Markwon markwon = Markwon.builder(this)
//                    .usePlugin(GlideImagesPlugin.create(this))
//                    .usePlugin(HtmlPlugin.create())
//                    .build();
//
//            final Spanned spanned = markwon.toMarkdown(current.getEventDescription());
//            markwon.setParsedMarkdown(description, spanned);
//
//            if (current.getLikes() != null)
//                reactCount.setText(String.valueOf(current.getLikes().size()));
//            else reactCount.setText("0");
//
//            if (current.getLikes().contains(PreferenceManager.getDefaultSharedPreferences(this).getString(USER_MONGO_ID, "")))
//                like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart).colorRes(R.color.feed_detail_like));
//            else
//                like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart_o).colorRes(R.color.feed_detail_like));
//
//            like.setOnClickListener(v -> {
//                FeedUtils.reactOnFeed(this, current.getId());
//                if (current.getLikes().contains(myMongoId)) {
//                    like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart_o).colorRes(R.color.feed_detail_like));
//                    current.getLikes().remove(myMongoId);
//                    reactCount.setText(String.valueOf(current.getLikes().size()));
//                    feedViewModel.insert(current);
//                } else {
//                    like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart).colorRes(R.color.feed_detail_like));
//                    current.getLikes().add(myMongoId);
//                    reactCount.setText(String.valueOf(current.getLikes().size()));
//                    feedViewModel.insert(current);
//                }
//            });
//
//            if (current.getPostLinks() != null) {
//                for (String page : current.getPostLinks()) {
//                    if (page.contains("facebook")) {
//                        ImageView fb = findViewById(R.id.feed_facebook);
//                        fb.setVisibility(View.VISIBLE);
//                        fb.setOnClickListener(view1 -> openWebsiteIntent(this, page));
//                    } else if (page.contains("instagram")) {
//                        ImageView inst = findViewById(R.id.feed_instagram);
//                        inst.setVisibility(View.VISIBLE);
//                        inst.setOnClickListener(view1 -> openWebsiteIntent(this, page));
//                    } else if (page.contains("twitter")) {
//                        ImageView tw = findViewById(R.id.feed_twitter);
//                        tw.setVisibility(View.VISIBLE);
//                        tw.setOnClickListener(view1 -> openWebsiteIntent(this, page));
//                    }
//                }
//            }
//
//        }
    }

}
