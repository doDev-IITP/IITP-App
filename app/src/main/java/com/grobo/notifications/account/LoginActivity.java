package com.grobo.notifications.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.transition.TransitionInflater;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.grobo.notifications.R;
import com.grobo.notifications.database.Person;
import com.grobo.notifications.feed.Converters;
import com.grobo.notifications.main.MainActivity;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.network.UserRoutes;

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

    private GoogleApiClient mCredentialClient;
    private int RC_SAVE = 1;
    private int RC_READ = 10;

    UserRoutes service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        manager = getSupportFragmentManager();
        prefs = PreferenceManager.getDefaultSharedPreferences( this );

        getWindow().setStatusBarColor( Color.parseColor( "#8548a3" ) );

        service = RetrofitClientInstance.getRetrofitInstance().create( UserRoutes.class );

        setBaseFragment( savedInstanceState );
        final String call = getIntent().getStringExtra( "call" );

        progressDialog = new ProgressDialog( this );
        progressDialog.setIndeterminate( true );
        progressDialog.setCanceledOnTouchOutside( false );
        CredentialsOptions options =
                new CredentialsOptions.Builder().forceEnableSaveDialog().build();
        mCredentialClient = new GoogleApiClient.Builder( this )
                .addApi( Auth.CREDENTIALS_API, options )
                .setAccountName( "youremail" )
                .build();
        mCredentialClient.connect();
        CredentialRequest mCredentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported( true )
                .setAccountTypes( IdentityProviders.GOOGLE, IdentityProviders.TWITTER )
                .build();

        Auth.CredentialsApi.request( mCredentialClient, mCredentialRequest ).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(CredentialRequestResult credentialRequestResult) {
                        if (credentialRequestResult.getStatus().isSuccess() && call == null) {
                            // Handle successful credential requests
                            login( credentialRequestResult.getCredential().getId(), credentialRequestResult.getCredential().getPassword() );
                        } else if (credentialRequestResult.getStatus().isSuccess()) {
                        } else {
                            // Handle unsuccessful and incomplete credential requests
                            try {
                                credentialRequestResult.getStatus().startResolutionForResult( LoginActivity.this, RC_READ );
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText( LoginActivity.this, "No credentials", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );

        service = RetrofitClientInstance.getRetrofitInstance().create(UserRoutes.class);

        setBaseFragment(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

    }


    private void setBaseFragment(Bundle savedInstanceState) {
        if (findViewById( R.id.frame_account ) != null) {

            if (savedInstanceState != null) {
                return;
            }

            LoginFragment firstFragment = new LoginFragment();
            firstFragment.setArguments( getIntent().getExtras() );
            manager.beginTransaction()
                    .add( R.id.frame_account, firstFragment ).commit();
        }
    }

    @Override
    public void onLoginSelected(final String email, final String password) {
        Credential credential = new Credential.Builder( email )
                .setPassword( password )  // Important: only store passwords in this field.
                // Android autofill uses this value to complete
                // sign-in forms, so repurposing this field will
                // likely cause errors.
                .build();

        save( credential );
        new CountDownTimer( 2000, 1000 ) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                login( email, password );
            }
        }.start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == RC_READ) {
            if (resultCode == RESULT_OK) {
                Log.e( "ye", "SAVE: OK" );
                Credential credential = data.getParcelableExtra( Credential.EXTRA_KEY );
                login( credential.getId(), credential.getPassword() );
            } else {
                Log.e( "ye", "SAVE: Canceled by user" );
            }
        }
    }

    private void save(Credential credential) {
        Auth.CredentialsApi.save( mCredentialClient, credential ).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            // Credentials were saved
                        } else {
                            if (status.hasResolution()) {
                                // Try to resolve the save request. This will prompt the user if
                                // the credential is new.
                                try {
                                    Toast.makeText( LoginActivity.this, "new saviing", Toast.LENGTH_SHORT ).show();
                                    status.startResolutionForResult( LoginActivity.this, RC_SAVE );
                                } catch (IntentSender.SendIntentException e) {
                                    // Could not resolve the request
                                }
                            }
                        }
                    }
                } );
//        Auth.CredentialsApi.delete( mCredentialClient, credential ).setResultCallback( new ResultCallback<Status>() {
//            @Override
//            public void onResult(@NonNull Status status) {
//                Toast.makeText( LoginActivity.this, "deleted", Toast.LENGTH_SHORT ).show();
//            }
//        } );
    }

    private void login(String email, String password) {

        progressDialog.setMessage( "Logging In..." );
        progressDialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put( "email", email );
        jsonParams.put( "password", password );
        RequestBody body = RequestBody.create( okhttp3.MediaType.parse( "application/json; charset=utf-8" ), (new JSONObject( jsonParams )).toString() );

        Call<Person> call = service.login( body );
        call.enqueue( new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful()) {
                    Person person = response.body();
                    Log.e( "response", person.getUser().getEmail() );
                    parseData( person );
                } else {
                    Toast.makeText( LoginActivity.this, "Signup failed, error " + response.code(), Toast.LENGTH_SHORT ).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                progressDialog.dismiss();
                Log.e( "responsebad", t.toString() );
            }
        } );
    }

    private void parseData(Person person) {
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.putString( USER_YEAR, person.getUser().getBatch() )
                .putString( USER_BRANCH, person.getUser().getBranch() )
                .putString( USER_NAME, person.getUser().getName() )
                .putString( WEBMAIL, person.getUser().getEmail() )
                .putString( ROLL_NUMBER, person.getUser().getInstituteId() )
                .putString( PHONE_NUMBER, person.getUser().getPhone() )
                .putString( USER_TOKEN, person.getToken() )
                .putString( USER_MONGO_ID, person.getUser().getStudentMongoId() );

        String porString = Converters.stringFromArray( person.getUser().getPor() );
        prefsEditor.putString( USER_POR, porString );

        if (person.getUser().getPor().size() != 0) {
            prefsEditor.putBoolean( IS_ADMIN, true );
        }

        prefsEditor.putBoolean( LOGIN_STATUS, true );
        prefsEditor.apply();

        Toast.makeText( LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT ).show();
        finish();
        startActivity( new Intent( LoginActivity.this, MainActivity.class ) );
        mCredentialClient.disconnect();
    }

    @Override
    public void onSignUpSelected(String email, String password) {

        if (validateWithWebmail( email, password )) {
            Fragment current = manager.findFragmentById( R.id.frame_account );

            Fragment next = new SignUpFragment();
            Bundle bundle = new Bundle();
            bundle.putString( "email", email );
            bundle.putString( "password", password );
            next.setArguments( bundle );

            current.setExitTransition( TransitionInflater.from( this ).inflateTransition( android.R.transition.slide_left ) );
            next.setEnterTransition( TransitionInflater.from( this ).inflateTransition( android.R.transition.slide_right ) );

            showFragmentWithTransition( next );
        }
    }

    private void showFragmentWithTransition(Fragment newFragment) {

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace( R.id.frame_account, newFragment );
        fragmentTransaction.addToBackStack( "later_fragment" );
        fragmentTransaction.commit();
    }

    private boolean validateWithWebmail(String email, String password) {


        return true;
    }

    @Override
    public void onFinishSelected(Map<String, Object> jsonParams) {

        progressDialog.setMessage( "Signing Up" );
        progressDialog.show();

        RequestBody body = RequestBody.create( okhttp3.MediaType.parse( "application/json; charset=utf-8" ), (new JSONObject( jsonParams )).toString() );

        Call<Person> call = service.register( body );
        call.enqueue( new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful()) {
                    Person person = response.body();
                    Log.e( "response", person.getUser().getEmail() );
                    parseData( person );
                } else {
                    Toast.makeText( LoginActivity.this, "Signup failed, error " + response.code(), Toast.LENGTH_SHORT ).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                progressDialog.dismiss();
                Log.e( "responsebad", t.toString() );
            }
        } );
        mCredentialClient.disconnect();
    }
}
