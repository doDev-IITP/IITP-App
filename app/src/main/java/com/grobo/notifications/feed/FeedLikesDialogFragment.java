package com.grobo.notifications.feed;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grobo.notifications.R;
import com.grobo.notifications.network.FeedRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class FeedLikesDialogFragment extends BottomSheetDialogFragment {

    private Context context;
    private String feedId;
    private LikesRecyclerAdapter adapter;

    public FeedLikesDialogFragment() {
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogLikes;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(this.requireContext(), this.getTheme());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("feedId")) {
            feedId = getArguments().getString("feedId");
        } else {
            Toast.makeText(context, "Error in feed!!!", Toast.LENGTH_LONG).show();
        }

        if (getContext() != null)
            context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_likes_dialog, container, false);
    }

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_load);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new LikesRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        populateRecycler();
    }

    private void populateRecycler() {

        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_TOKEN, "0");

        FeedRoutes service = RetrofitClientInstance.getRetrofitInstance().create(FeedRoutes.class);
        Call<List<DataPoster>> call = service.getFeedReacts(token, feedId);
        call.enqueue(new Callback<List<DataPoster>>() {
            @Override
            public void onResponse(@NonNull Call<List<DataPoster>> call, @NonNull Response<List<DataPoster>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DataPoster> allData = response.body();
                    adapter.setItemList(allData);
                }
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<List<DataPoster>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error in loading!!!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    static class LikesRecyclerAdapter extends RecyclerView.Adapter<LikesRecyclerAdapter.ViewHolder> {

        private List<DataPoster> itemList;

        LikesRecyclerAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_agenda_likes, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            if (itemList != null) {

                DataPoster current = itemList.get(position);
                holder.name.setText(current.getName());
                holder.instituteId.setText(current.getInstituteId());

            } else {
                holder.name.setText("Loading ...");
            }
        }

        @Override
        public int getItemCount() {
            if (itemList != null)
                return itemList.size();
            else return 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            TextView instituteId;
            CardView root;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                root = itemView.findViewById(R.id.card_root);
                name = itemView.findViewById(R.id.name);
                instituteId = itemView.findViewById(R.id.institute_id);
            }
        }

        void setItemList(List<DataPoster> dataPosters) {
            itemList = dataPosters;
            notifyDataSetChanged();
        }
    }
}
