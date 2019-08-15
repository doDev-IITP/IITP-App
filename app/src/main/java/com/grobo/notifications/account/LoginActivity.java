package com.grobo.notifications.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.R;
import com.grobo.notifications.database.Person;
import com.grobo.notifications.feed.Converters;
import com.grobo.notifications.main.MainActivity;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.network.UserRoutes;
import com.grobo.notifications.timetable.TimetableUtility;
import com.grobo.notifications.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.IS_ADMIN;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.PHONE_NUMBER;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.USER_NAME;
import static com.grobo.notifications.utils.Constants.USER_POR;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;
import static com.grobo.notifications.utils.Constants.USER_YEAR;
import static com.grobo.notifications.utils.Constants.WEBMAIL;

public class LoginActivity extends FragmentActivity implements LoginFragment.OnSignInInteractionListener,
        SignUpFragment.OnSignUpInteractionListener, OtpFragment.OnOtpEnteredListener {


    private FragmentManager manager;
    private SharedPreferences prefs;
    private ProgressDialog progressDialog;

    private String email;
    private String password;
    private UserRoutes service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(Color.parseColor("#8548a3"));

        manager = getSupportFragmentManager();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        service = RetrofitClientInstance.getRetrofitInstance().create(UserRoutes.class);

        setBaseFragment();

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    private void setBaseFragment() {
        if (findViewById(R.id.frame_account) != null) {
            LoginFragment firstFragment = new LoginFragment();
            manager.beginTransaction()
                    .add(R.id.frame_account, firstFragment).commit();
        }
    }

    @Override
    public void onLoginSelected(final String email, final String password) {
        this.email = email;
        this.password = password;
        login(email, password);
    }

    private void login(String email, String password) {

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("email", email);
        jsonParams.put("password", password);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

        Call<Person> call = service.login(body);
        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(@NonNull Call<Person> call, @NonNull Response<Person> response) {
                if (response.code() == 200 && response.body() != null) {

                    Person person = response.body();
                    if (person.getUser() != null)
                        parseData(person);
                    else
                        Toast.makeText(LoginActivity.this, "User account error, please contact administrator.", Toast.LENGTH_SHORT).show();

                } else if (response.code() == 500) {

                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "User not registered", Toast.LENGTH_LONG).show();
                    showFragmentWithTransition(new SignUpFragment());

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login failed, error " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Person> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e("responsebad", t.toString());
                Toast.makeText(LoginActivity.this, "Login failure, error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseData(Person person) {

        if (person.getUser().getActive() == 0) {
            Toast.makeText(this, "You need to verify your account first", Toast.LENGTH_LONG).show();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            showFragmentWithTransition(new OtpFragment());
        } else {

            if (person.getUser().getStudentMongoId() != null) {
                SharedPreferences.Editor prefsEditor = prefs.edit();

                prefsEditor.putString(USER_YEAR, person.getUser().getBatch())
                        .putString(USER_BRANCH, person.getUser().getBranch())
                        .putString(USER_NAME, person.getUser().getName())
                        .putString(WEBMAIL, person.getUser().getEmail())
                        .putString(ROLL_NUMBER, person.getUser().getInstituteId())
                        .putString(PHONE_NUMBER, person.getUser().getPhone())
                        .putString(USER_TOKEN, person.getToken())
                        .putString(USER_MONGO_ID, person.getUser().getStudentMongoId());

                if (person.getUser().getPors() != null) {
                    String porString = Converters.stringFromArray(person.getUser().getPors());
                    prefsEditor.putString(USER_POR, porString);

                    if (person.getUser().getPors().size() != 0) {
                        prefsEditor.putBoolean(IS_ADMIN, true);
                    }
                }

                prefsEditor.putBoolean(LOGIN_STATUS, true);
                prefsEditor.apply();

                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                downloadTimetable();
            }
        }
    }

    @Override
    public void onRegisterSelected(String name, String roll, String phone) {

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("email", this.email);
        jsonParams.put("password", this.password);
        jsonParams.put("name", name);
        jsonParams.put("instituteId", roll);
        jsonParams.put("phone", phone);

        completeSignup(jsonParams);
    }

    private void completeSignup(Map<String, Object> jsonParams) {
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

        Call<Person> call = service.register(body);
        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful()) {
                    showFragmentWithTransition(new OtpFragment());
                } else if (response.code() == 403) {
                    Toast.makeText(LoginActivity.this, "User already exists, please login. Error " + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Could not register account. Error " + response.code(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("responseBad", t.toString());
                Toast.makeText(LoginActivity.this, "SignUp failure, error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void OnOtpEntered(int status, int otp) {

        progressDialog.setMessage("Verifying...");
        progressDialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("email", email);
        jsonParams.put("code", otp);

        RequestBody bodyOtp = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

        Call<Person> call1 = service.verifyOtp(bodyOtp);
        call1.enqueue(new Callback<Person>() {
            @Override
            public void onResponse
                    (@NonNull Call<Person> call, @NonNull Response<Person> response) {

                if (response.code() == 200) {
                    Toast.makeText(LoginActivity.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                    Person person = response.body();

                    if (person != null && person.getUser() != null)
                        parseData(person);
                    else
                        Toast.makeText(LoginActivity.this, "User account error, please contact administrator.", Toast.LENGTH_SHORT).show();

                } else if (response.code() == 404) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "User not registered. Error (404)", Toast.LENGTH_LONG).show();
                } else if (response.code() == 408) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "User already activated. Error (408)", Toast.LENGTH_LONG).show();
                } else if (response.code() == 409) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Invalid activation code. Error (409)", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Verification failed. Error " + response.code(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Person> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e("responsebad", t.toString());
                Toast.makeText(LoginActivity.this, "Login failure, error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showFragmentWithTransition(Fragment newFragment) {

        Fragment current = manager.findFragmentById(R.id.frame_account);

        if (current != null)
            current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_left));

        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right));
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_account, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.commit();
    }

    private void downloadTimetable() {
        final String TIMETABLE_URL = FirebaseRemoteConfig.getInstance().getString(Constants.TIMETABLE_URL);
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                String mUrl = TIMETABLE_URL + PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString(USER_YEAR, "") + "/" + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(USER_BRANCH, "").toLowerCase() + "/.json/";
                String jsonResponse = TimetableUtility.doEverything(mUrl);

                return jsonResponse;
            }

            @Override
            protected void onPostExecute(String s) {
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("jsonString", s).apply();
                progressDialog.dismiss();
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));

            }
        }.execute();
    }
}