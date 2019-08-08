package com.grobo.notifications.services.maintenance;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.grobo.notifications.R;
import com.grobo.notifications.network.MaintenanceRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class MaintenanceFragment extends Fragment {

    public MaintenanceFragment() {}

    private RecyclerView recyclerView;
    private MaintenanceRecyclerAdapter adapter;
    private View emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance, container, false);

        emptyView = view.findViewById(R.id.lost_found_empty_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_lost_found);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateRecycler();
            }
        });
        swipeRefreshLayout.setRefreshing(true);

        recyclerView = view.findViewById(R.id.maintenance_fragment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MaintenanceRecyclerAdapter(getContext(), (MaintenanceRecyclerAdapter.OnMaintenanceSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);

        populateRecycler();

        return view;
    }

    private void populateRecycler() {

        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");
        Log.e("token", token);

        MaintenanceRoutes service = RetrofitClientInstance.getRetrofitInstance().create(MaintenanceRoutes.class);

        Call<MaintenanceItem.MaintenanceSuper> call = service.getAllMaintenance(token);

        call.enqueue(new Callback<MaintenanceItem.MaintenanceSuper>() {
            @Override
            public void onResponse(Call<MaintenanceItem.MaintenanceSuper> call, Response<MaintenanceItem.MaintenanceSuper> response) {
                if (response.isSuccessful()) {

                    if (response.body() != null &&response.body().getMaintenances() != null) {
                        List<MaintenanceItem> allItems = response.body().getMaintenances();

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
            public void onFailure(Call<MaintenanceItem.MaintenanceSuper> call, Throwable t) {
                Log.e("failure", t.getMessage());
                Toast.makeText(getContext(), "Fetch failed", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

}
