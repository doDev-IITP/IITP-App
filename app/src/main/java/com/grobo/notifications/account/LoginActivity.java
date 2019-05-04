package com.grobo.notifications.account;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.grobo.notifications.main.MainActivity;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.grobo.notifications.utils.Constants.ALTERNATE_EMAIL;
import static com.grobo.notifications.utils.Constants.LOGIN_FAILED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.PHONE_NUMBER;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_NAME;
import static com.grobo.notifications.utils.Constants.USER_NOT_REGISTERED;
import static com.grobo.notifications.utils.Constants.USER_YEAR;
import static com.grobo.notifications.utils.Constants.WEBMAIL;

public class LoginActivity extends FragmentActivity implements NetworkFragment.LoginCallback {


    private Button loginButton;
    private EditText emailInput;
    private EditText passwordInput;
    private ProgressDialog progressDialog;

    private String email;
    private String password;
    private NetworkFragment networkFragment;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        emailInput = findViewById(R.id.login_input_webmail);
        passwordInput = findViewById(R.id.login_input_password);
        networkFragment = NetworkFragment.getInstance(getSupportFragmentManager());

        getWindow().setStatusBarColor(Color.parseColor("#8548a3"));

        loginButton = findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.getWifiInfo(LoginActivity.this)) {
                login();
                }
            }
        });

        checkForPermission();
    }

    private void login() {
        if (!validateInput()) {
            Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
            return;
        }

        loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loginButton.setEnabled(true);
            }
        });

        if (networkFragment == null) {
            networkFragment = NetworkFragment.getInstance(getSupportFragmentManager());
        }
        networkFragment.login(email, password);
    }

    private boolean validateInput() {
        boolean valid = true;

        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        if (email.isEmpty() || (email.contains("@") && !email.contains("@iitp.ac.in"))) {
            emailInput.setError("Please enter a valid username");
            valid = false;
        } else {
            emailInput.setError(null);
            if (email.contains("@iitp.ac.in")) {
                String[] splitResult = email.split("@");
                Log.e("mylogmessage", splitResult[0]);
                email = splitResult[0];
            }
        }

        if (password.isEmpty()) {
            passwordInput.setError("Please enter a password");
            valid = false;
        } else {
            passwordInput.setError(null);
        }
        return valid;
    }

    @Override
    public void onLoginSuccess(String response) {
        if (response.equals(LOGIN_FAILED) || response.equals(USER_NOT_REGISTERED)){
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        } else {
            parseLoginData(response);
            Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        progressDialog.dismiss();
    }

    private void parseLoginData(String response) {
        String rollNumber;
        String name;
        String alternateEmail;
        String phoneNumber;

        SharedPreferences.Editor prefsEditor = prefs.edit();

        if (response.contains("<a href=\"logout.php\">LOGOUT</a>")) {

            Pattern p = Pattern.compile("<input type=\"text\" name=\"rollno\" pattern=\"\\[A-Za-z0-9]\\+\" required value=\"(.*?)\">\n");
            Matcher m = p.matcher(response);
            if (m.find()) {
                rollNumber = m.group(1);
                Log.e("mylog", rollNumber);
                prefsEditor.putString(ROLL_NUMBER, rollNumber);

                StringBuilder alpha = new StringBuilder();
                StringBuilder beta = new StringBuilder();
                for (int i = 0; i < rollNumber.length()-2; i++) {
                    if(Character.isAlphabetic(rollNumber.charAt(i)))
                        alpha.append(rollNumber.charAt(i));
                    else
                        beta.append(rollNumber.charAt(i));
                }
                prefsEditor.putString(USER_YEAR, beta.toString());
                prefsEditor.putString(USER_BRANCH, alpha.toString());
            }

            p = Pattern.compile("<input type=\"text\" name=\"name\" pattern=\"\\[A-Za-z\\\\s]\\+\" required value=\"(.*?)\">");
            m = p.matcher(response);
            if (m.find()) {
                name = m.group(1);
                Log.e("mylog", name);
                prefsEditor.putString(USER_NAME, name);
            }

            p = Pattern.compile("<input type=\"email\" name=\"aemail\" required value=\"(.*?)\">");
            m = p.matcher(response);
            if (m.find()) {
                alternateEmail = m.group(1);
                Log.e("mylog", alternateEmail);
                prefsEditor.putString(ALTERNATE_EMAIL, alternateEmail);
            }

            p = Pattern.compile("required title=\"Please enter Only 10 digits. valid Mobile no., No 0/\\+91\" value=\"(.*?)\">");
            m = p.matcher(response);
            if (m.find()) {
                phoneNumber = m.group(1);
                Log.e("mylog", phoneNumber);
                prefsEditor.putString(PHONE_NUMBER, phoneNumber);
            }

            prefsEditor.putString(WEBMAIL, email + "@iitp.ac.in");
            prefsEditor.putBoolean(LOGIN_STATUS, true);

            prefsEditor.apply();

        } else {
            Toast.makeText(this, "Failed parsing profile data", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkForPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    12345);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1234: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
