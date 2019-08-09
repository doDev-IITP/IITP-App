package com.grobo.notifications.services.complaints;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.grobo.notifications.R;
import com.grobo.notifications.network.ComplaintsRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class ComplaintsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ComplaintsRecyclerAdapter adapter;
    private View emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ComplaintsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaints, container, false);

        emptyView = view.findViewById(R.id.complaints_empty_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_complaints);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateRecycler();
            }
        });
        swipeRefreshLayout.setRefreshing(true);

        recyclerView = view.findViewById(R.id.complaints_fragment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ComplaintsRecyclerAdapter(getContext(), (ComplaintsRecyclerAdapter.OnComplaintSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);

        populateRecycler();

        return view;
    }

    private void populateRecycler() {

        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");
        Log.e("token", token);

        ComplaintsRoutes service = RetrofitClientInstance.getRetrofitInstance().create(ComplaintsRoutes.class);

        Call<ComplaintItem.ComplaintsSuper> call = service.getAllComplaints(token);

        call.enqueue(new Callback<ComplaintItem.ComplaintsSuper>() {
            @Override
            public void onResponse(Call<ComplaintItem.ComplaintsSuper> call, Response<ComplaintItem.ComplaintsSuper> response) {
                if (response.isSuccessful()) {

                    if (response.body() != null &&response.body().getComplaints() != null) {
                        List<ComplaintItem> allItems = response.body().getComplaints();

                        adapter.setItemList(allItems);
                        if (allItems.size() == 0) {
                            recyclerView.setVisibility(View.INVISIBLE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ComplaintItem.ComplaintsSuper> call, @NonNull Throwable t) {
                Log.e("failure", t.getMessage());
                Toast.makeText(getContext(), "Fetch failed", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

}
