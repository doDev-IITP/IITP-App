package com.grobo.notifications.services.agenda;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grobo.notifications.R;
import com.grobo.notifications.network.AgendaRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.utils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Random;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class NewAgendaFragment extends Fragment implements Spinner.OnItemSelectedListener {


    public NewAgendaFragment() {
    }

    private static final int SELECT_PICTURE = 1;

    private String category = null;
    private String bodyText;
    private Bitmap selectedImage = null;
    private ImageView imagePreview;
    private Context context;
    private EditText otherTextInput;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null)
            context = getContext();

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_agenda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Spinner categorySpinner = view.findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context, R.array.agenda_categories_spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setOnItemSelectedListener(this);

        final EditText body = view.findViewById(R.id.problem_body);
        Button imageSelector = view.findViewById(R.id.image_selector_button);
        Button submitButton = view.findViewById(R.id.submit_button);
        imagePreview = view.findViewById(R.id.image_preview);

        otherTextInput = view.findViewById(R.id.input_other_category);

        imageSelector.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_PICTURE);
        });

        submitButton.setOnClickListener(v -> {
            bodyText = body.getText().toString();

            if (bodyText.isEmpty()) body.setError("Please describe the agenda");
            else if (category == null) {
                Toast.makeText(context, "Please select a valid category!", Toast.LENGTH_LONG).show();
            } else if (category.equals("Other") && otherTextInput.getText().toString().isEmpty()) {
                otherTextInput.setError("Please enter a valid category");
            } else {
                showConfirmation();
            }
        });

    }

    private void showConfirmation() {
        new AlertDialog.Builder(context)
                .setTitle("Confirmation Dialog")
                .setMessage("Posting this...\nPlease confirm!!")
                .setPositiveButton("Confirm", (dialog, which) -> postImage())
                .setNegativeButton("Cancel", (dialog, id) -> {
                    if (dialog != null) dialog.dismiss();
                }).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            category = null;
        } else {
            category = (String) parent.getSelectedItem();
            if (category.equals("Other")) {
                otherTextInput.setVisibility(View.VISIBLE);
            } else {
                otherTextInput.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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
                            if (imageSize > 2 * 1000 * 1000) {
                                utils.showSimpleAlertDialog(context, "Alert!!!", "Please select an image with size less than 2 MB !");
                            } else {
                                imagePreview.setImageBitmap(tempImage);
                                selectedImage = tempImage;
                            }

                            returnCursor.close();
                        }

                    } catch (Exception e) {
                        utils.showSimpleAlertDialog(context, "Alert!!!", "Image reading error!");
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Canceled!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postImage() {

        if (selectedImage == null) postAgenda("");
        else {

            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(0);
            progressDialog.show();

            Random r = new Random();
            int i = (r.nextInt(90000) + 9999);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

            StorageReference storageRef = firebaseStorage.getReference().child(String.format("agendas/agenda%s%s.jpg", String.valueOf(System.currentTimeMillis()), String.valueOf(i)));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, baos);
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
                postAgenda(imageUrl.toString());
            }).addOnFailureListener(e -> {
                utils.showSimpleAlertDialog(context, "Alert!", "File upload failed !!!");
                progressDialog.dismiss();
            });
        }
    }

    private void postAgenda(String imageUrl) {

        progressDialog.dismiss();
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("imageUrl", imageUrl);
        jsonParams.put("problem", bodyText);
        jsonParams.put("category", category.equals("Other") ? otherTextInput.getText().toString() : category);

        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_TOKEN, "0");

        AgendaRoutes service = RetrofitClientInstance.getRetrofitInstance().create(AgendaRoutes.class);
        Call<ResponseBody> call = service.postAgenda(token, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Agenda successfully posted", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null)
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    Log.e("failure", String.valueOf(response.code()));
                    Toast.makeText(context, "Post failed, please try again! Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(context, "Post failed, please try again!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
