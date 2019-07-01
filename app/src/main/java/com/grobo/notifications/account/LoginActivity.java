package com.grobo.notifications.account;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.TransitionInflater;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.grobo.notifications.R;
import com.grobo.notifications.database.Person;
import com.grobo.notifications.feed.Converters;
import com.grobo.notifications.main.MainActivity;
import com.grobo.notifications.network.UserRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

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
        SignUpFragment.OnSignUpInteractionListener {


    private FragmentManager manager;
    private SharedPreferences prefs;
    private ProgressDialog progressDialog;
    UserRoutes service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        manager = getSupportFragmentManager();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        getWindow().setStatusBarColor(Color.parseColor("#8548a3"));

        checkForPermission();
        service = RetrofitClientInstance.getRetrofitInstance().create(UserRoutes.class);

        setBaseFragment(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void setBaseFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.frame_account) != null) {

            if (savedInstanceState != null) {
                return;
            }

            LoginFragment firstFragment = new LoginFragment();
            firstFragment.setArguments(getIntent().getExtras());
            manager.beginTransaction()
                    .add(R.id.frame_account, firstFragment).commit();
        }
    }

    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    12345);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1234) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoginSelected(String email, String password) {
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
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful()) {
                    Person person = response.body();
                    Log.e("response", person.getUser().getEmail());
                    parseData(person);
                } else {
                    Toast.makeText(LoginActivity.this, "Signup failed, error " + response.code(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("responsebad", t.toString());
            }
        });
    }

    private void parseData(Person person) {
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.putString(USER_YEAR, person.getUser().getBatch())
                .putString(USER_BRANCH, person.getUser().getBranch())
                .putString(USER_NAME, person.getUser().getName())
                .putString(WEBMAIL, person.getUser().getEmail())
                .putString(ROLL_NUMBER, person.getUser().getInstituteId())
                .putString(PHONE_NUMBER, person.getUser().getPhone())
                .putString(USER_TOKEN, person.getToken())
                .putString(USER_MONGO_ID, person.getUser().getStudentMongoId());

        String porString = Converters.stringFromArray(person.getUser().getPor());
        prefsEditor.putString(USER_POR, porString);

        if (person.getUser().getPor().size() != 0) {
            prefsEditor.putBoolean(IS_ADMIN, true);
        }

        prefsEditor.putBoolean(LOGIN_STATUS, true);
        prefsEditor.apply();

        Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    @Override
    public void onSignUpSelected(String email, String password) {

        if (validateWithWebmail(email, password)) {
            Fragment current = manager.findFragmentById(R.id.frame_account);

            Fragment next = new SignUpFragment();
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            bundle.putString("password", password);
            next.setArguments(bundle);

            current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_left));
            next.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right));

            showFragmentWithTransition(next);
        }
    }

    private void showFragmentWithTransition(Fragment newFragment) {

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_account, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.commit();
    }

    private boolean validateWithWebmail(String email, String password) {


        return true;
    }

    @Override
    public void onFinishSelected(Map<String, Object> jsonParams) {

        progressDialog.setMessage("Signing Up");
        progressDialog.show();

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

        Call<Person> call = service.register(body);
        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful()) {
                    Person person = response.body();
                    Log.e("response", person.getUser().getEmail());
                    parseData(person);
                } else {
                    Toast.makeText(LoginActivity.this, "Signup failed, error " + response.code(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("responsebad", t.toString());
            }
        });
    }
}
