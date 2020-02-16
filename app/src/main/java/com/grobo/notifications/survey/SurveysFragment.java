package com.grobo.notifications.survey;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.grobo.notifications.R;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.network.SurveyRoutes;
import com.grobo.notifications.survey.models.Survey;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class SurveysFragment extends Fragment {

    public SurveysFragment() {
    }

    private View emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SurveyRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private SharedPreferences prefs;
    private List<Survey> allSurveys = new ArrayList<>();

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() != null) context = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_surveys, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null)
            getActivity().setTitle("Explore");

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::populateRecyclerView);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        emptyView = view.findViewById(R.id.empty_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ViewTreeObserver observer = recyclerView.getViewTreeObserver();
        postponeEnterTransition();
        observer.addOnGlobalLayoutListener(this::startPostponedEnterTransition);

        adapter = new SurveyRecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(true);
        populateRecyclerView();
    }

    private void populateRecyclerView() {
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_TOKEN, "0");

        SurveyRoutes service = RetrofitClientInstance.getRetrofitInstance().create(SurveyRoutes.class);

        Call<List<Survey>> call = service.getAllSurveys(token);
        call.enqueue(new Callback<List<Survey>>() {
            @Override
            public void onResponse(@NonNull Call<List<Survey>> call, @NonNull Response<List<Survey>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allSurveys = response.body();
                    adapter.setSurveyList(allSurveys);
                } else {
                    Toast.makeText(context, "Load failed, error: " + response.code(), Toast.LENGTH_LONG).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Survey>> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(context, "Update failed!! Please check internet connection!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
