package com.grobo.notifications.Mess;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.firestore.model.value.ServerTimestampValue;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.R;
import com.grobo.notifications.network.MessRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.Constants;
import com.grobo.notifications.utils.ImageViewerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.MESS_MENU_URL;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class MessFragment extends Fragment {

    public MessFragment() {
    }

    private ProgressDialog progressDialog;
    private LinearLayout messLL;
    private Spinner spinner;
    private Spinner mealTypeSpinner;
    private ImageView messMenu;
    private FirebaseRemoteConfig remoteConfig;
    private List<MessModel> messCancelList;
    private int full, c = -1, mess;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        progressDialog = new ProgressDialog( getContext() );
        progressDialog.setIndeterminate( true );
        progressDialog.setCanceledOnTouchOutside( false );
        progressDialog.setCancelable( false );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate( R.layout.fragment_mess, container, false );

        messLL = rootView.findViewById( R.id.ll_mess_details );
        LinearLayout linearLayout = rootView.findViewById( R.id.days );
        LinearLayout linearLayout1 = rootView.findViewById( R.id.cancelled_meals );
        linearLayout1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactFragment( new CancelMealFragment() );
            }
        } );
        //   messSelectionLL = rootView.findViewById(R.id.ll_mess_selection);

        // selectedMess = rootView.findViewById(R.id.mess_selected_mess);
        messCancelList = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();


        if (PreferenceManager.getDefaultSharedPreferences( requireContext() ).getInt( "mess_choice", 0 ) == 0) {
            LayoutInflater li = LayoutInflater.from( requireContext() );
            View promptsView = li.inflate( R.layout.prompt, null );
            RadioGroup radioGroup = promptsView.findViewById( R.id.radio_group1 );


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    requireContext() );
            alertDialogBuilder.setView( promptsView );
            alertDialogBuilder
                    .setCancelable( false )
                    .setPositiveButton( "Submit",
                            (dialog, id) -> {
                                // get user input and set it to result
                                // edit text

                                c = radioGroup.getCheckedRadioButtonId();
                                mess = c % 4;
                                if (mess == 0)
                                    mess = 4;
                                // Toast.makeText( requireContext(), "a"+m, Toast.LENGTH_SHORT ).show();
                                if (c == -1) {
                                    ((ViewGroup) promptsView.getParent()).removeView( promptsView );
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }


                            } );

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


            PreferenceManager.getDefaultSharedPreferences( requireContext() ).edit().putInt( "mess_choice", mess ).apply();


            MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create( MessRoutes.class );

            String userId = PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( USER_MONGO_ID, "" );
            String token = PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( USER_TOKEN, "" );

            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put( "messChoice", mess );
            jsonParams.put( "student", userId );
            RequestBody body = RequestBody.create( (new JSONObject( jsonParams )).toString(), okhttp3.MediaType.parse( "application/json; charset=utf-8" ) );

            Call<ResponseBody> call = service.selectMess( token, body );

            call.enqueue( new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText( requireContext(), "mess selected", Toast.LENGTH_SHORT ).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Toast.makeText( requireContext(), "faileddd", Toast.LENGTH_SHORT ).show();
                }
            } );


        }


        spinner = rootView.findViewById( R.id.cancel_meal_spinner );
        mealTypeSpinner = rootView.findViewById( R.id.type_meal_spinner );
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource( requireContext(), R.array.spinner_items, android.R.layout.simple_spinner_item );
        spinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter( spinnerAdapter );
        ArrayAdapter<CharSequence> mealTypeAdapter = ArrayAdapter.createFromResource( requireContext(), R.array.spinner_meals, android.R.layout.simple_spinner_item );
        mealTypeAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        mealTypeSpinner.setAdapter( mealTypeAdapter );

        Button cancelMealButton = rootView.findViewById( R.id.cancel_meal_ok_button );
        cancelMealButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnsavedChangesDialog();
            }
        } );
//        populateData();
        mealTypeSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                full = i;
                if (i == 4)
                    linearLayout.setVisibility( View.VISIBLE );
                else
                    linearLayout.setVisibility( View.GONE );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );

        return rootView;

    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        builder.setTitle( "Confirmation Dialog" );
        builder.setMessage( "Cancelling the meals... Please confirm!!" );
        builder.setPositiveButton( "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelMealFunction();
            }
        } );
        builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        } );

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void transactFragment(Fragment frag) {


        Fragment current = getFragmentManager().findFragmentById( R.id.frame_layout_home );
        if (current != null) {
            current.setExitTransition( TransitionInflater.from( requireContext() ).inflateTransition( android.R.transition.fade ) );
            frag.setEnterTransition( TransitionInflater.from( requireContext() ).inflateTransition( android.R.transition.fade ) );
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace( R.id.frame_layout_home, frag )
                .addToBackStack( null )
                .commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null)
            getActivity().setTitle( "Mess" );

        ImageView messMenu = view.findViewById( R.id.mess_menu );
        String url = FirebaseRemoteConfig.getInstance().getString( MESS_MENU_URL );
        Glide.with( this ).load( url ).centerInside().into( messMenu );

        messMenu.setOnClickListener( v -> {
            Intent i = new Intent( requireContext(), ImageViewerActivity.class );
            i.putExtra( "image_url", url );
            startActivity( i );
        } );

        View qrFragment = view.findViewById( R.id.mess_fr_qr );

        qrFragment.setOnClickListener( v -> {
            QRFragment frag = new QRFragment();
            FragmentTransaction fragmentManager = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentManager.replace( R.id.frame_layout_home, frag )
                    .addToBackStack( frag.getTag() )
                    .commit();
        } );

        super.onViewCreated( view, savedInstanceState );
    }

//    private void populateData() {


//        MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create(MessRoutes.class);
//
//        String userId = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_MONGO_ID, "");

//        Call<ResponseBody> call = service.getMessData(userId);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    String json = null;
//                    try {
//                        json = response.body().string();
//                        parseMessJson(json);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Log.e("json", json);
//                } else if (response.code() == 404) {
//                    Toast.makeText(getContext(), "Mess data not found.", Toast.LENGTH_SHORT).show();
//                    messSelectionLL.setVisibility(View.VISIBLE);
//                } else {
//                    Log.e("json", "failed");
//                    if (progressDialog != null && progressDialog.isShowing())
//                        progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("failure", t.getMessage());
//                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
//            }
//        });

//    }

    //    private void parseMessJson(String json) {
//
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            JSONObject mess = jsonObject.getJSONObject("mess");
//
//            JSONArray cancelledMeals = mess.getJSONArray("cancelledMeals");
//            List<String> foodData = new ArrayList<>();
//            for (int k = 0; k < cancelledMeals.length(); k++) {
//                foodData.add(cancelledMeals.getString(k));
//            }
//
//            String messString = null;
//            switch (mess.getInt("messChoice")) {
//                case 1:
//                    messString = "BH1 Mess 1";
//                    break;
//                case 2:
//                    messString = "BH1 Mess 2";
//                    break;
//                case 3:
//                    messString = "BH2 Mess";
//                    break;
//                case 4:
//                    messString = "GH Mess";
//                    break;
//            }
//            selectedMess.setText(messString);
//
//
//            messLL.setVisibility(View.VISIBLE);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
//
//
//    }
//
    private void cancelMealFunction() {

        progressDialog.setMessage( "Cancelling Meal..." );
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ) );
        calendar1.add( Calendar.DATE, 1 );

        MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create( MessRoutes.class );

        String userId = PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( USER_MONGO_ID, "" );
        String token = PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( USER_TOKEN, "" );

        Map<String, Object> jsonParams = new HashMap<>();

        if (mealTypeSpinner.getSelectedItemPosition() != 4) {
            List<Long> a = new ArrayList<>();
            a.add( calendar1.getTimeInMillis() );
            jsonParams.put( mealTypeSpinner.getSelectedItem().toString().toLowerCase(), a );
        } else {
            List<Long> a = new ArrayList<>();

            for (int j = 0; j < spinner.getSelectedItemPosition() + 2; j++) {
                a.add( calendar1.getTimeInMillis() );
                calendar1.add( Calendar.DATE, 1 );
            }

            jsonParams.put( "fullday", a );
        }

        RequestBody body = RequestBody.create( (new JSONObject( jsonParams )).toString(), okhttp3.MediaType.parse( "application/json; charset=utf-8" ) );

        Call<ResponseBody> call = service.cancelMeal( token, userId, body );

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    alert( "Your meal is successfully cancelled" );
                    progressDialog.dismiss();

                } else if (response.code() == 401) {
                    Toast.makeText( getContext(), "Unauthorised request", Toast.LENGTH_SHORT ).show();
                } else if (response.code() == 415) {
                    alert( "This meal had already been cancelled..." );
                }

                Log.e( "mess", String.valueOf( response.code() ) );

                try {
                    if (response.body() != null)
                        Log.e( "mess", response.body().string() );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (response.errorBody() != null) {
                    try {
                        Log.e( "mess", response.errorBody().string() );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText( requireContext(), "faileddd", Toast.LENGTH_SHORT ).show();
                progressDialog.dismiss();

            }
        } );


    }
//
//    private DatePickerDialog getDatePickerDialog(final EditText cancelMealDate) {
//        final Calendar calendar = Calendar.getInstance();
//
//        return new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//
////                if (day  ) {
//
//                calendar.set(Calendar.YEAR, year);
//                calendar.set(Calendar.MONTH, month);
//                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault());
//                cancelMealDate.setText(dateFormat.format(calendar.getTime()));
//                cancelMealString = dayOfMonth + "_" + month + "_" + year;
////                }
//            }
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//    }

    private void alert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
        builder.setTitle( "Alert!!!" );
        builder.setMessage( msg );
        builder.setPositiveButton( "OK", (dialog, which) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        } );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

