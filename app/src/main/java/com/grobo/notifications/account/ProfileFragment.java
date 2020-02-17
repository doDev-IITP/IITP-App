package com.grobo.notifications.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.account.por.PORRecyclerAdapter;
import com.grobo.notifications.network.PorRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.network.UserRoutes;
import com.grobo.notifications.utils.utils;

import org.json.JSONArray;
import org.json.JSONException;
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

import static com.grobo.notifications.utils.Constants.IS_QR_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.IS_TT_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.PHONE_NUMBER;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_IMAGE;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.USER_NAME;
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
    private ImageView addButton;
    private ProgressDialog progressDialog;

    private ImageView profilePic;
    private static final int SELECT_PICTURE = 783;
    private Bitmap selectedImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        if (getContext() != null)
            context = getContext();

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

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

        TextView roll = view.findViewById(R.id.tv_profile_roll);
        roll.setText(prefs.getString(ROLL_NUMBER, ROLL_NUMBER));

        profilePic = view.findViewById(R.id.iv_profile_dp);
        profilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_PICTURE);
        });
        Glide.with(this)
                .load(prefs.getString(USER_IMAGE, ""))
                .centerCrop()
                .placeholder(R.drawable.profile_photo)
                .into(profilePic);

        TextView phone = view.findViewById(R.id.tv_profile_phone);
        phone.setText(prefs.getString(PHONE_NUMBER, PHONE_NUMBER));

        Button button = view.findViewById(R.id.profile_logout_button);
        button.setOnClickListener(v -> logout());

        addButton = view.findViewById(R.id.iv_add_por);
        addButton.setOnClickListener(v -> {
            callback.onAddPorSelected();
        });

        recyclerView = view.findViewById(R.id.rv_pors);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PORRecyclerAdapter((PORRecyclerAdapter.OnPORSelectedListener) context);
        recyclerView.setAdapter(adapter);

        getPorData();
    }

    private void getPorData() {

        if (getView() != null) {

            ProgressBar porProgressBar = getView().findViewById(R.id.progress_bar_pors);
            TextView textNoPor = getView().findViewById(R.id.tv_no_por);

            PorRoutes service = RetrofitClientInstance.getRetrofitInstance().create(PorRoutes.class);

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
                                        int code = por.getInt("code");
                                        String position = por.getString("position");

                                        JSONArray array = por.getJSONArray("access");
                                        List<Integer> access = new ArrayList<>();
                                        for (int j = 0; j < array.length(); j++) access.add(array.getInt(j));

                                        porItemList.add(new PORItem(porId, clubId, clubName, code, position, access));
                                    }

                                    adapter.setItemList(porItemList);

                                    recyclerView.setVisibility(View.VISIBLE);
                                    textNoPor.setVisibility(View.GONE);

                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    textNoPor.setVisibility(View.VISIBLE);
                                }

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        addButton.setVisibility(View.VISIBLE);
                    } else
                        Toast.makeText(context, "Update failed!!", Toast.LENGTH_SHORT).show();

                    porProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    if (t.getMessage() != null)
                        Log.e("failure", t.getMessage());
                    Toast.makeText(context, "Update failed!!", Toast.LENGTH_SHORT).show();
                    porProgressBar.setVisibility(View.GONE);
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
                .putString(USER_IMAGE, "")
                .apply();

        callback.onLogout();
    }

    interface OnLogoutCallback {
        void onLogout();

        void onAddPorSelected();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Uri returnUri = data.getData();

                        Bitmap tempImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), returnUri);

                        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
                        if (returnCursor != null) {
                            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();

                            long imageSize = returnCursor.getLong(sizeIndex);
                            if (imageSize > 1000 * 1000) {
                                utils.showSimpleAlertDialog(context, "Alert!!!", "Please select an image with size less than 1 MB !");
                            } else {
                                profilePic.setImageBitmap(tempImage);
                                selectedImage = tempImage;
                                showPostDialog();
                            }

                            returnCursor.close();
                        }

                    } catch (Exception e) {
                        utils.showSimpleAlertDialog(context, "Alert!!!", "Image reading error!");
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "Canceled!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPostDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Confirmation Dialog")
                .setMessage("Are you sure you want to update your profile picture?")
                .setPositiveButton("Confirm", (dialog, which) -> postImage())
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void postImage() {

        if (selectedImage == null) {
            utils.showSimpleAlertDialog(context, "Alert!!!", "No image found!");
        } else {
            progressDialog.setMessage("Uploading image...");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(0);
            progressDialog.show();

            Random r = new Random();
            int i = (r.nextInt(90000) + 9999);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

            StorageReference storageRef = firebaseStorage.getReference().child(String.format("profile/profile%s%s.jpg", String.valueOf(System.currentTimeMillis()), String.valueOf(i)));

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
                Map<String, Object> jsonParams = new ArrayMap<>();
                jsonParams.put("image", imageUrl.toString());
                updateProfile(jsonParams);
            }).addOnFailureListener(e -> {
                utils.showSimpleAlertDialog(context, "Alert!", "File upload failed !!!");
                progressDialog.dismiss();
            });
        }
    }

    private void updateProfile(Map<String, Object> jsonParams) {

        progressDialog.setIndeterminate(true);
        progressDialog.show();

        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_TOKEN, "0");

        UserRoutes service = RetrofitClientInstance.getRetrofitInstance().create(UserRoutes.class);
        Call<ResponseBody> call = service.updateProfile(token, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
                    if (jsonParams.containsKey("image"))
                        prefs.edit().putString(USER_IMAGE, (String) jsonParams.get("image")).apply();

                } else {
                    try {
                        if (response.errorBody() != null)
                            Log.e("failure", String.valueOf(response.code()) + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    utils.showSimpleAlertDialog(context, "Alert!!!", "Save failed! Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("failure", t.getMessage());
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(context, "Update failed, please check internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }


}
