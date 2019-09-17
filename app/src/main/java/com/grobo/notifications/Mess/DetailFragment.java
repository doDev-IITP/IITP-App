package com.grobo.notifications.Mess;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;
import com.grobo.notifications.network.MessRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;

public class DetailFragment extends Fragment {

    private int mealType;
    private ProgressDialog progressDialog;

    private View empty;
    private CancelMealAdapter cancelMealAdapter;
    private RecyclerView recyclerView;

    public DetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Bundle b = getArguments();
        if (b != null && b.containsKey("meal")) {
            mealType = b.getInt("meal");

            recyclerView = view.findViewById(R.id.recycler_cancel);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            cancelMealAdapter = new CancelMealAdapter(requireContext());
            recyclerView.setAdapter(cancelMealAdapter);

            DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(decoration);

            empty = view.findViewById(R.id.meal_empty_view);

            populateData();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private void populateData() {

        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        String userId = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(USER_MONGO_ID, "");

        MessRoutes service = RetrofitClientInstance.getRetrofitInstance().create(MessRoutes.class);
        Call<MessModel> call = service.getMessData(userId);

        call.enqueue(new Callback<MessModel>() {
            @Override
            public void onResponse(@NonNull Call<MessModel> call, @NonNull Response<MessModel> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getMess() != null) {

                        List<Long> time;

                        if (mealType == 1) time = response.body().getMess().getBreakfast();
                        else if (mealType == 2) time = response.body().getMess().getLunch();
                        else if (mealType == 3) time = response.body().getMess().getSnacks();
                        else if (mealType == 4) time = response.body().getMess().getDinner();
                        else time = response.body().getMess().getFullday();

                        if (time == null || time.size() == 0) {
                            empty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            empty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            cancelMealAdapter.setItemList(time);
                        }
                    }
                } else
                    Toast.makeText(requireContext(), "Data fetch failed, error " + response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<MessModel> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Data fetch failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
