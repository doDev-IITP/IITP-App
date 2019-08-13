package com.grobo.notifications.services.lostandfound;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.grobo.notifications.R;
import com.grobo.notifications.network.LostAndFoundRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class LostAndFoundFragment extends Fragment {

    public LostAndFoundFragment() {
    }

    private LostAndFoundRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost_and_found, container, false);

        emptyView = view.findViewById(R.id.lost_found_empty_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_lost_found);
        swipeRefreshLayout.setOnRefreshListener(this::populateRecycler);
        swipeRefreshLayout.setRefreshing(true);

        recyclerView = view.findViewById(R.id.lost_found_fragment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LostAndFoundRecyclerAdapter(getContext(), (LostAndFoundRecyclerAdapter.OnLostFoundSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);

        populateRecycler();

        return view;
    }

    private void populateRecycler() {

        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");
        Log.e("token", token);

        LostAndFoundRoutes service = RetrofitClientInstance.getRetrofitInstance().create(LostAndFoundRoutes.class);

        Call<LostAndFoundItem.LostNFoundSuper> call = service.getAllLostNFound(token);
        call.enqueue(new Callback<LostAndFoundItem.LostNFoundSuper>() {
            @Override
            public void onResponse(@NonNull Call<LostAndFoundItem.LostNFoundSuper> call, @NonNull Response<LostAndFoundItem.LostNFoundSuper> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getLostnfounds() != null) {

                        List<LostAndFoundItem> allItems = response.body().getLostnfounds();

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
            public void onFailure(@NonNull Call<LostAndFoundItem.LostNFoundSuper> call, @NonNull Throwable t) {
                Log.e("failure", t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

}
