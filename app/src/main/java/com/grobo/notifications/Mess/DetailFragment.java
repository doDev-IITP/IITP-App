package com.grobo.notifications.Mess;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grobo.notifications.R;
import com.grobo.notifications.network.MessRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private int mealType;
    private ProgressDialog progressDialog;

    public DetailFragment() {
        // Required empty public constructor

    }

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
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_detail, container, false );
        Bundle b = getArguments();
        if (b != null) {
            mealType = b.getInt( "meal" );
        }
        String[] meal = {"breakfast", "lunch", "snacks", "dinner", "full"};
        RecyclerView recyclerView = view.findViewById( R.id.recycler_cancel );
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
        CancelMealAdapter cancelMealAdapter = new CancelMealAdapter( getContext(), (CancelMealAdapter.OnCancelSelectedListener) getActivity() );
        recyclerView.setAdapter( cancelMealAdapter );
        View empty = view.findViewById( R.id.meal_empty_view );


        MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create( MessRoutes.class );

        String userId = PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( USER_MONGO_ID, "" );

        Map<String, Object> jsonParams = new HashMap<>();
        Call<MessModel> call = service.getMessData( userId );
        progressDialog.setMessage( "Fetching data..." );
        progressDialog.show();

        call.enqueue( new Callback<MessModel>() {
            @Override
            public void onResponse(@NonNull Call<MessModel> call, @NonNull Response<MessModel> response) {
                if (response.isSuccessful()) {

                    progressDialog.dismiss();
                    if (response.body() != null && response.body().getMess() != null) {

                        List<Long> time;

                        if (mealType == 1)
                            time = response.body().getMess().getBreakfast();
                        else if (mealType == 2)
                            time = response.body().getMess().getLunch();
                        else if (mealType == 3)
                            time = response.body().getMess().getSnacks();
                        else if (mealType == 4)
                            time = response.body().getMess().getDinner();
                        else
                            time = response.body().getMess().getFullday();
                        if (time == null || time.size() == 0) {
                            empty.setVisibility( View.VISIBLE );
                            recyclerView.setVisibility( View.GONE );
                        } else {
                            empty.setVisibility( View.GONE );
                            recyclerView.setVisibility( View.VISIBLE );

                            cancelMealAdapter.ItemList( time );
                            Log.e( "code", String.valueOf( response.code() ) );
                            Log.e( "msg", response.message() );
                            Log.e( "msg", response.toString() );
                            Log.e( "msg", String.valueOf( time.size() ) );
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<MessModel> call, Throwable t) {

                progressDialog.dismiss();
            }
        } );


        return view;
    }

}
