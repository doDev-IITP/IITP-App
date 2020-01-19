package com.grobo.notifications.services.agenda;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.SmoothScroller;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.network.AgendaRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.utils;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class AgendaFragment extends Fragment {

    public AgendaFragment() {
    }

    private View emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private FloatingActionButton fab;

    private String intentAgendaId = null;

    private SmoothScroller smoothScroller;
    private RecyclerView recyclerView;
    private AgendaRecyclerAdapter adapter;
    private LayoutManager layoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null)
            context = getContext();

        if (getArguments() != null && getArguments().containsKey("agendaId"))
            intentAgendaId = getArguments().getString("agendaId");

        Iconify.with(new FontAwesomeModule());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agenda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyView = view.findViewById(R.id.empty_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::populateRecycler);
        swipeRefreshLayout.setRefreshing(true);

        fab = view.findViewById(R.id.new_agenda_fab);
        fab.setOnClickListener(v -> showFragmentWithTransition(new NewAgendaFragment()));

        recyclerView = view.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AgendaRecyclerAdapter(context, (AgendaRecyclerAdapter.OnAgendaSelectedListener) context);
        recyclerView.setAdapter(adapter);

        smoothScroller = new LinearSmoothScroller(context) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        populateRecycler();
    }

    private void showFragmentWithTransition(Fragment newFragment) {
        if (getActivity() != null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            Fragment current = manager.findFragmentById(R.id.frame_agenda);

            if (current != null)
                current.setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.no_transition));
            newFragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.slide_bottom));

            manager.beginTransaction().replace(R.id.frame_agenda, newFragment)
                    .addToBackStack(newFragment.getTag()).commit();
        }
    }

    private void populateRecycler() {

        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_TOKEN, "0");

        AgendaRoutes service = RetrofitClientInstance.getRetrofitInstance().create(AgendaRoutes.class);

        Call<AgendaItems> call = service.getAllAgendas(token);

        call.enqueue(new Callback<AgendaItems>() {
            @Override
            public void onResponse(@NonNull Call<AgendaItems> call, @NonNull Response<AgendaItems> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getAgendas() != null) {

                        List<Agenda> allItems = response.body().getAgendas();
                        adapter.setItemList(allItems);

                        if (allItems.size() == 0) {
                            recyclerView.setVisibility(View.INVISIBLE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.INVISIBLE);
                        }

                        if (intentAgendaId != null)
                            for (int i = 0; i < allItems.size(); i++) {
                                if (allItems.get(i).getId().equals(intentAgendaId)) {
                                    smoothScroller.setTargetPosition(i);
                                    layoutManager.startSmoothScroll(smoothScroller);
                                    break;
                                }
                            }

                    } else {
                        utils.showSimpleAlertDialog(context, "Alert!!!", "No valid items found!" + response.code());
                    }
                } else if (response.code() == 404) {
                    utils.showSimpleAlertDialog(context, "Alert!!!", "No agendas found!");
                } else {
                    utils.showSimpleAlertDialog(context, "Alert!!!", "Data fetch failed!\nError code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AgendaItems> call, @NonNull Throwable t) {
                if (t.getMessage() != null) Log.e("failure", t.getMessage());
                utils.showSimpleAlertDialog(context, "Alert!!!", "Data fetch failed, please check internet connection!");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
