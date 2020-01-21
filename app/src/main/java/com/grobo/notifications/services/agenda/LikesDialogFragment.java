package com.grobo.notifications.services.agenda;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.grobo.notifications.R;
import com.grobo.notifications.feed.DataPoster;
import com.grobo.notifications.network.AgendaRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class LikesDialogFragment extends BottomSheetDialogFragment {

    private Context context;
    private DataPoster agendaPoster;
    private String agendaId;
    private LikesRecyclerAdapter adapter;

    public LikesDialogFragment() {
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

        if (getArguments() != null && getArguments().containsKey("agenda_poster") && getArguments().containsKey("agenda_id")) {
            agendaPoster = getArguments().getParcelable("agenda_poster");
            agendaId = getArguments().getString("agenda_id");
        } else {
            Toast.makeText(context, "Error in agenda!!!", Toast.LENGTH_LONG).show();
        }

        if (getContext() != null)
            context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_likes_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView posterName = view.findViewById(R.id.poster_name);
        TextView posterInstituteId = view.findViewById(R.id.poster_institute_id);
        posterName.setText(agendaPoster.getName());
        posterInstituteId.setText(agendaPoster.getInstituteId());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new LikesRecyclerAdapter(context);
        recyclerView.setAdapter(adapter);

        populateRecycler();
    }

    private void populateRecycler() {

        List<DataPoster> allData = new ArrayList<>();

        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_TOKEN, "0");

        AgendaRoutes service = RetrofitClientInstance.getRetrofitInstance().create(AgendaRoutes.class);
        Call<ResponseBody> call = service.getAgendaReacts(token, agendaId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        JSONObject root = new JSONObject(response.body().string());
                        JSONArray likes = root.getJSONArray("likes");

                        for (int i = 0; i < likes.length(); i++) {
                            Gson gson = new Gson();
                            DataPoster like = gson.fromJson(likes.get(i).toString(), DataPoster.class);
                            allData.add(like);
                        }

                        adapter.setItemList(allData);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error in loading!!!", Toast.LENGTH_LONG).show();
            }
        });

    }
}
