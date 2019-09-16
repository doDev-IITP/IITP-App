package com.grobo.notifications.Mess;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.R;
import com.grobo.notifications.network.MessRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.ImageViewerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Spinner mealTypeSpinner;
    private SharedPreferences prefs;

    private SparseArray<String> messes = new SparseArray<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        progressDialog = new ProgressDialog( getContext() );
        progressDialog.setIndeterminate( true );
        progressDialog.setCanceledOnTouchOutside( false );
        progressDialog.setCancelable( false );

        messes.append(1, "BH1 Mess 1");
        messes.append(2, "BH1 Mess 2");
        messes.append(3, "New hostel Mess");
        messes.append(4, "GH Mess");

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_mess, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null)
            getActivity().setTitle( "Mess" );

        if (prefs.getInt( "mess_choice", 0 ) == 0) {
            new Handler().postDelayed(this::getMessData, 200);
        } else {
            setCurrentMess(prefs.getInt("mess_choice", 0));
        }

        ImageView messMenu = view.findViewById( R.id.mess_menu );
        String url = FirebaseRemoteConfig.getInstance().getString( MESS_MENU_URL );
        Glide.with( this ).load( url ).centerInside().into( messMenu );

        messMenu.setOnClickListener( v -> {
            Intent i = new Intent( requireContext(), ImageViewerActivity.class );
            i.putExtra( "image_url", url );
            startActivity( i );
        } );

        View qrFragment = view.findViewById( R.id.mess_fr_qr );
        qrFragment.setOnClickListener( v -> transactFragment(new QRFragment()));

        LinearLayout linearLayout = view.findViewById( R.id.days );
        View cancelMeal = view.findViewById( R.id.cancel_meal_card );
        cancelMeal.setOnClickListener(view1 -> transactFragment( new CancelMealFragment() ));

        Button cancelMealButton = view.findViewById( R.id.cancel_meal_ok_button );
        cancelMealButton.setOnClickListener(v -> showUnsavedChangesDialog());

        mealTypeSpinner = view.findViewById( R.id.type_meal_spinner );
        mealTypeSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 4)
                    linearLayout.setVisibility( View.VISIBLE );
                else
                    linearLayout.setVisibility( View.GONE );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );

        super.onViewCreated( view, savedInstanceState );
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
        builder.setTitle( "Confirmation Dialog" );
        builder.setMessage( "Cancelling the meals... Please confirm!!" );
        builder.setPositiveButton( "Confirm", (dialog, which) -> cancelMealFunction());
        builder.setNegativeButton( "Cancel", (dialog, id) -> {
            if (dialog != null) dialog.dismiss();
        });
        builder.show();
    }

    private void transactFragment(Fragment frag) {

        FragmentManager manager = requireActivity().getSupportFragmentManager();

        Fragment current = manager.findFragmentById( R.id.frame_layout_home );
        if (current != null) {
            current.setExitTransition( TransitionInflater.from( requireContext() ).inflateTransition( android.R.transition.explode ) );
            frag.setEnterTransition( TransitionInflater.from( requireContext() ).inflateTransition( android.R.transition.explode) );
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace( R.id.frame_layout_home, frag )
                .addToBackStack( frag.getTag() )
                .commit();
    }

    private void getMessData() {

        progressDialog.setMessage("Loading mess data...");
        progressDialog.show();

        MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create(MessRoutes.class);
        String userId = prefs.getString(USER_MONGO_ID, "");

        Call<MessModel> call = service.getMessData( userId );
        call.enqueue( new Callback<MessModel>() {
            @Override
            public void onResponse(@NonNull Call<MessModel> call, @NonNull Response<MessModel> response) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getMess() != null && response.body().getMess().getMessChoice() != null) {

                        int m = response.body().getMess().getMessChoice();
                        prefs.edit().putInt("mess_choice", m).apply();

                        if (getView() != null) {
                            setCurrentMess(m);
                        }
                    }
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Mess data not found.", Toast.LENGTH_SHORT).show();
                    showMessSelectionPrompt();
                } else {
                    Toast.makeText(getContext(), "Failed to get mess data, error " + response.code() , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessModel> call, @NonNull Throwable t) {
                if (t.getMessage() != null) Log.e("failure", t.getMessage());
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(getContext(), "Mess data fetch failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMessSelectionPrompt(){
        LayoutInflater li = LayoutInflater.from( requireContext() );
        View promptsView = li.inflate( R.layout.dialog_mess_selection, null );
        RadioGroup radioGroup = promptsView.findViewById( R.id.radio_group1 );

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext() );
        alertDialogBuilder.setView( promptsView ).setCancelable( false )
                .setPositiveButton( "Submit", (dialog, id) -> {
                    int mess = 0;
                    int c = -1;

                    c = radioGroup.getCheckedRadioButtonId();
                    switch (c) {
                        case R.id.radio_button_mess_1:
                            mess = 1;
                            break;
                        case R.id.radio_button_mess_2:
                            mess = 2;
                            break;
                        case R.id.radio_button_mess_3:
                            mess = 3;
                            break;
                        case R.id.radio_button_mess_4:
                            mess = 4;
                            break;
                    }

                    if (c == -1) {
                        ((ViewGroup) promptsView.getParent()).removeView( promptsView );
                        alertDialogBuilder.show();
                    } else if (mess != 0) chooseMessFunction(mess);
                } );

        alertDialogBuilder.show();
    }

    private void chooseMessFunction(int mess) {

        progressDialog.setMessage("Selecting mess...");
        progressDialog.show();

        MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create(MessRoutes.class);

        String userId = prefs.getString(USER_MONGO_ID, "");
        String token = prefs.getString(USER_TOKEN, "");

        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("messChoice", mess);
        jsonParams.put("student", userId);
        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));

        Call<ResponseBody> call = service.selectMess(token, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Mess selected", Toast.LENGTH_SHORT).show();
                    prefs.edit().putInt("mess_choice", mess).apply();
                    setCurrentMess(mess);
                } else {
                    Toast.makeText(requireContext(), "Mess selection failed, error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(requireContext(), "Mess selection failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cancelMealFunction() {

        progressDialog.setMessage( "Cancelling Meal..." );
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ) );
        calendar1.add( Calendar.DATE, 1 );

        Spinner spinner = requireView().findViewById( R.id.cancel_meal_spinner );

        MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create( MessRoutes.class );

        String userId = prefs.getString( USER_MONGO_ID, "" );
        String token = prefs.getString( USER_TOKEN, "" );

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

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    alert("Your meal is successfully cancelled");
                } else if (response.code() == 401) {
                    Toast.makeText(getContext(), "Unauthorised request", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 415) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject object = new JSONObject(response.errorBody().string());
                            if (object.has("message"))
                                alert(object.getString("message"));
                            else alert("This meal has already been cancelled !!");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else alert("This meal has already been cancelled !!");

                } else alert("Meal cancellation failed, error " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Cancellation failed !!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
    }

    private void alert(String msg) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Alert!!!")
                .setMessage(msg)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (dialog != null) dialog.dismiss();
                }).show();
    }

    private void setCurrentMess(int data) {
        if (getView() != null) {
            getView().findViewById(R.id.ll_mess_choice).setVisibility(View.VISIBLE);
            TextView messChoice = getView().findViewById(R.id.tv_selected_mess);
            messChoice.setText(messes.get(data, "Not available"));
        }
    }
}

