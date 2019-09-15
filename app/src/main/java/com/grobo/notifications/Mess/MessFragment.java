package com.grobo.notifications.Mess;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import org.json.JSONObject;

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

import static com.grobo.notifications.utils.Constants.MESS_MENU_URL;

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
    private int full;

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
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            progressDialog.setMessage( "Configuring..." );
            progressDialog.show();
            mAuth.signInWithEmailAndPassword( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ), PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.USER_MONGO_ID, "" ) ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        try {

                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {


                            Log.e( "hello", task.getException().toString() );
                            mAuth.createUserWithEmailAndPassword( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ), PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.USER_MONGO_ID, "" ) ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                        progressDialog.dismiss();
                                    else
                                        Log.e( "hello", task.getException().toString() );
                                }
                            } );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                        progressDialog.dismiss();
                    }
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
//        String meal = null;
//        switch (spinner.getSelectedItemPosition()) {
//            case 0:
//                meal = "1";
//                break;
//            case 1:
//                meal = "2";
//                break;
//            case 2:
//                meal = "3";
//                break;
//            case 3:
//                meal = "4";
//                break;
//        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Calendar calendar = Calendar.getInstance();
        String q = "cancel";
        Map<String, Object> map = new HashMap<>();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ) );

        calendar1.add( Calendar.DATE, 1 );
        Date today = calendar1.getTime();
        calendar1.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ) );

        Log.e( getClass().getSimpleName(), calendar1.getTime().toString() + String.valueOf( calendar1.getTime().getTime() ) );
        List<Integer> arr = new ArrayList<>();
        List<Date> dates = new ArrayList<>();
        Log.e( "date", today.toString() );
        if (full == 4) {
            db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ) ).collection( q ).whereArrayContains( "days", today ).whereEqualTo( "full", false ).get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().size() > 0) {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ) ).collection( q ).document( documentSnapshots.get( 0 ).getId() ).delete().addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            } );
                        }
                    }
                }
            } );


            db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ) ).collection( q ).whereArrayContains( "days", today ).whereEqualTo( "full", true ).get().addOnCompleteListener( task -> {
                if (task.isSuccessful()) {
                    map.put( "full", true );
                    QuerySnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.getDocuments().size() > 0) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
                        builder.setTitle( "Alert!!!" );
                        builder.setMessage( "Meal has already been cancelled for one of the selected dates..." );
                        builder.setPositiveButton( "OK", (dialog, which) -> {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        } );
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    } else {
                        List<Timestamp> dates1 = new ArrayList<>();

                        for (int j = 1; j <= Integer.parseInt( spinner.getSelectedItem().toString() ); j++) {
                            calendar1.add( Calendar.DATE, 1 );
                            dates1.add( new Timestamp( calendar1.getTime() ) );

                        }
                        map.put( "days", dates1 );
                        map.put( "timestamp", FieldValue.serverTimestamp() );
                        db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ) ).collection( q ).add( map ).addOnCompleteListener( task12 -> {

                            progressDialog.dismiss();
                            if (task.isSuccessful()) {

                                AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
                                builder.setTitle( "Alert!!!" );
                                builder.setMessage( "Your meal has been cancelled for next " + spinner.getSelectedItem().toString() + " days.." );
                                builder.setPositiveButton( "OK", (dialog, which) -> {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                } );
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else
                                Toast.makeText( getContext(), "faileeeeed", Toast.LENGTH_SHORT ).show();

                        } );

                    }


                }
            } );


        } else {
            Log.e( "time", today.toString() );
            db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ) ).collection( q ).whereArrayContains( "days", today ).get().addOnCompleteListener( task -> {
                if (task.isSuccessful()) {

                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    DocumentSnapshot documentSnapshot = null;
                    Log.e( "query", String.valueOf( queryDocumentSnapshots.size() ) );
                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().size() > 0) {
                        int z = 0;
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : documentSnapshots) {
                            if (d.get( "full" ).equals( true )) {
                                //Cannot be cancelled
                                z = 1;
                            } else
                                documentSnapshot = d;
                        }
                        if (z == 0 && documentSnapshot != null) {

                            List<Long> add = (ArrayList<Long>) documentSnapshot.get( "meals" );
                            if (add == null)
                                add = new ArrayList<>();
                            int k = 0;
                            for (int j = 0; j < add.size(); j++) {
                                if (add.get( j ) == (full + 1))
                                    k = 1;
                            }
                            if (k == 0) {
                                // Toast.makeText( getContext(), "yippeee", Toast.LENGTH_SHORT ).show();
                                add.add( (long) (full + 1) );
                                map.put( "meals", add );
                                map.put( "timestamp", FieldValue.serverTimestamp() );
                                List<Date> day = new ArrayList<>();
                                day.add( today );
                                map.put( "days", day );
                                map.put( "full", false );
                                db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ) ).collection( q ).document( documentSnapshot.getId() ).update( map ).addOnCompleteListener( task1 -> {
                                    if (task1.isSuccessful()) {
                                        progressDialog.dismiss();
                                        AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
                                        builder.setTitle( "Alert!!!" );
                                        builder.setMessage( "Your tomorrow's "+ mealTypeSpinner.getSelectedItem().toString() + "is successfully cancelled..." );
                                        builder.setPositiveButton( "OK", (dialog, which) -> {
                                            if (dialog != null) {
                                                dialog.dismiss();
                                            }
                                        } );
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }

                                } );
                            } else {
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                                //  Toast.makeText( getContext(), "This meal had already been cancelled", Toast.LENGTH_SHORT ).show();

                                AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
                                builder.setTitle( "Alert!!!" );
                                builder.setMessage( "This meal had already been cancelled..." );
                                builder.setPositiveButton( "OK", (dialog, which) -> {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                } );
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }

                        } else {
                            if (progressDialog != null)
                                progressDialog.dismiss();
                            //cancelled already
                            AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
                            builder.setTitle( "Alert!!!" );
                            builder.setMessage( "This meal had already been cancelled..." );
                            builder.setPositiveButton( "OK", (dialog, which) -> {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            } );
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            //Toast.makeText( getContext(), "cancelleed already", Toast.LENGTH_SHORT ).show();

                        }
                    } else {
                        List<Long> add = new ArrayList<>();
                        add.add( (long) full + 1 );
                        map.put( "meals", add );
                        map.put( "timestamp", FieldValue.serverTimestamp() );
                        List<Date> day = new ArrayList<>();
                        day.add( today );
                        map.put( "days", day );
                        map.put( "full", false );
                        db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ) ).collection( q ).add( map ).addOnCompleteListener( new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
                                    builder.setTitle( "Alert!!!" );
                                    builder.setMessage( "Your tomorrow's "+mealTypeSpinner.getSelectedItem().toString()+" is successfully cancelled..." );
                                    builder.setPositiveButton( "OK", (dialog, which) -> {
                                        if (dialog != null) {
                                            dialog.dismiss();
                                        }
                                    } );
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();

                                }

                                if (progressDialog != null)
                                    progressDialog.dismiss();

                            }
                        } );
                    }


//
//                        List<Long> add = (ArrayList<Long>) task.getResult().get( "meals" );
//                        if (add == null)
//                            add = new ArrayList<>();
//
//                        map.put( "date", calendar.get( Calendar.DATE ) + 1 );
//                        int k = 0;
//                        if (add != null) {
//                            for (int j = 0; j < add.size(); j++) {
//                                if (add.get( j ) == (full + 1))
//                                    k = 1;
//                            }
//
//
//                        }
//                        if (k == 0) {
//                            add.add( (long) (full + 1) );
//
//                            map.put( "meals", add );
//                            map.put( "timestamp", FieldValue.serverTimestamp() );
//                            map.put( "fullday", false );
//                            db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ) ).collection( q ).document( String.valueOf( calendar.get( Calendar.DATE ) + 1 ) ).set( map ).addOnCompleteListener( task1 -> progressDialog.dismiss() );
//                        }
                }
            } );


        }


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
}

