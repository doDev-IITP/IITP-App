package com.grobo.notifications.clubs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.network.ClubRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.utils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class EditClubDetailActivity extends FragmentActivity {

    private ProgressDialog progressDialog;

    private ClubItem current;
    private PORItem currentPor;

    private boolean editMode = false;

    private static final int SELECT_PICTURE = 783;
    private Bitmap selectedImage;

    private ImageView imagePreview;
    private EditText clubTitle;
    private EditText clubDescription;
    private EditText clubBio;
    private String fbLink = null;
    private String instLink = null;
    private String twitterLink = null;
    private String websiteLink = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        if (getIntent().hasExtra("por")) {
            currentPor = getIntent().getParcelableExtra("por");
            if (currentPor != null) {
                editMode = true;
                String clubId = currentPor.getClubId();
                downloadClubData(clubId);
            } else {
                utils.showFinishAlertDialog(this, "Alert!!!", "POR data not found.");
            }
        } else {
            editMode = false;
            showCreateData();
        }
    }

    private void showCreateData() {

        imagePreview = findViewById(R.id.image_preview);

        clubTitle = findViewById(R.id.edit_club_title);
        clubDescription = findViewById(R.id.edit_club_description);
        clubBio = findViewById(R.id.edit_club_bio);

        Button imageSelector = findViewById(R.id.button_select_image);
        imageSelector.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_PICTURE);
        });

        ImageView fb = findViewById(R.id.club_facebook);
        fb.setOnClickListener(v -> showLinkDialog(1));

        ImageView inst = findViewById(R.id.club_instagram);
        inst.setOnClickListener(v -> showLinkDialog(2));

        ImageView twitter = findViewById(R.id.club_twitter);
        twitter.setOnClickListener(v -> showLinkDialog(3));

        CardView website = findViewById(R.id.cv_website);
        website.setOnClickListener(v -> showLinkDialog(4));

        Button saveClubButton = findViewById(R.id.button_save_club);
        saveClubButton.setOnClickListener(v -> {
            if (clubTitle.getText().toString().isEmpty()) {
                clubTitle.setError("Enter a title!");
            } else if (clubDescription.getText().toString().isEmpty()) {
                clubDescription.setError("Enter a valid description!");
            } else if (clubBio.getText().toString().isEmpty()) {
                clubBio.setError("Enter a valid bio!");
            } else showPostDialog();
        });
    }

    private void showEditData() {
        if (current != null) {

            imagePreview = findViewById(R.id.image_preview);
            Glide.with(this)
                    .load("")
                    .thumbnail(Glide.with(this).load(current.getImage()))
                    .centerInside()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(imagePreview);

            clubTitle = findViewById(R.id.edit_club_title);
            clubTitle.setText(current.getName());
            clubDescription = findViewById(R.id.edit_club_description);
            clubDescription.setText(current.getDescription());
            clubBio = findViewById(R.id.edit_club_bio);
            clubBio.setText(current.getBio());

            Button imageSelector = findViewById(R.id.button_select_image);
            imageSelector.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            });

            ImageView fb = findViewById(R.id.club_facebook);
            fb.setOnClickListener(v -> showLinkDialog(1));

            ImageView inst = findViewById(R.id.club_instagram);
            inst.setOnClickListener(v -> showLinkDialog(2));

            ImageView twitter = findViewById(R.id.club_twitter);
            twitter.setOnClickListener(v -> showLinkDialog(3));

            CardView website = findViewById(R.id.cv_website);
            website.setOnClickListener(v -> showLinkDialog(4));

            Button saveClubButton = findViewById(R.id.button_save_club);
            saveClubButton.setOnClickListener(v -> {
                if (clubTitle.getText().toString().isEmpty()) {
                    clubTitle.setError("Enter a title!");
                } else if (clubDescription.getText().toString().isEmpty()) {
                    clubDescription.setError("Enter a valid description!");
                } else if (clubBio.getText().toString().isEmpty()) {
                    clubBio.setError("Enter a valid bio!");
                } else showPostDialog();
            });

            if (current.getPages() != null) for (String page : current.getPages()) {
                if (page.contains("facebook")) fbLink = page;
                else if (page.contains("instagram")) instLink = page;
                else if (page.contains("twitter")) twitterLink = page;
            }

            websiteLink = current.getWebsite();

        } else utils.showSimpleAlertDialog(this, "Alert!!!", "Club not found.");
    }

    private void downloadClubData(String clubId) {

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        ClubRoutes service = RetrofitClientInstance.getRetrofitInstance().create(ClubRoutes.class);

        Call<ClubItem> call = service.getClubById(token, clubId);
        call.enqueue(new Callback<ClubItem>() {
            @Override
            public void onResponse(@NonNull Call<ClubItem> call, @NonNull Response<ClubItem> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    current = response.body();
                }
                showEditData();
            }

            @Override
            public void onFailure(@NonNull Call<ClubItem> call, @NonNull Throwable t) {
                if (progressDialog != null) progressDialog.dismiss();
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
            case 4:
                editText.setHint("Enter website URL");
                if (websiteLink != null) editText.setText(websiteLink);
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
                case 4:
                    websiteLink = editText.getText().toString();
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
                .setMessage("Saving club... Please confirm!!")
                .setPositiveButton("Confirm", (dialog, which) -> postImage())
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void postImage() {

        if (selectedImage == null) {
            if (current != null && current.getImage() != null && !current.getImage().isEmpty())
                post(current.getImage());
            else post("");
        } else {

            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(0);
            progressDialog.show();

            Random r = new Random();
            int i = (r.nextInt(90000) + 9999);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

            StorageReference storageRef = firebaseStorage.getReference().child(String.format("clubs/club%s%s.jpg", String.valueOf(System.currentTimeMillis()), String.valueOf(i)));

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

        progressDialog.setIndeterminate(true);
        progressDialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("name", clubTitle.getText().toString());
        jsonParams.put("description", clubDescription.getText().toString());
        jsonParams.put("bio", clubBio.getText().toString());
        jsonParams.put("image", imageUrl);
        if (websiteLink != null) jsonParams.put("website", websiteLink);

        List<String> social = new ArrayList<>();
        if (fbLink != null && !fbLink.isEmpty()) social.add(fbLink);
        if (instLink != null && !instLink.isEmpty()) social.add(instLink);
        if (twitterLink != null && !twitterLink.isEmpty()) social.add(twitterLink);
        jsonParams.put("pages", social);

        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        ClubRoutes service = RetrofitClientInstance.getRetrofitInstance().create(ClubRoutes.class);

        Call<ResponseBody> call;
        if (editMode) call = service.patchClub(token, currentPor.getClubId(), body);
        else call = service.addClub(token, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        utils.showFinishAlertDialog(EditClubDetailActivity.this, "Alert!!!", response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Log.e("failure", String.valueOf(response.code()) + response.errorBody().string());
                            utils.showSimpleAlertDialog(EditClubDetailActivity.this, "Alert!!!", response.errorBody().string() + " Error: " + response.code());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("failure", t.getMessage());
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(EditClubDetailActivity.this, "Save failed, please check internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }

}
