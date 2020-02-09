package com.grobo.notifications.services.agenda;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.grobo.notifications.R;
import com.grobo.notifications.network.AgendaRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class AgendaActivity extends AppCompatActivity implements AgendaRecyclerAdapter.OnAgendaSelectedListener {

    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        getSupportActionBar().setTitle("Agenda");

        manager = getSupportFragmentManager();

        setBaseFragment();
    }

    private void setBaseFragment() {
        if (findViewById(R.id.frame_agenda) != null) {
            AgendaFragment firstFragment = new AgendaFragment();
            firstFragment.setArguments(getIntent().getExtras());
            manager.beginTransaction().add(R.id.frame_agenda, firstFragment).commit();
        }
    }

    @Override
    public void onAgendaSelected(Agenda agenda) {
        Bundle b = new Bundle();
        b.putString("agenda_id", agenda.getId());
        b.putParcelable("agenda_poster", agenda.getPoster());

        LikesDialogFragment fragment = new LikesDialogFragment();
        fragment.setArguments(b);

        fragment.show(manager, fragment.getTag());

    }

    @Override
    public void onReactSelected(String agendaId) {

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        AgendaRoutes service = RetrofitClientInstance.getRetrofitInstance().create(AgendaRoutes.class);
        Call<ResponseBody> call = service.reactOnAgenda(token, agendaId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 201)
                    Toast.makeText(AgendaActivity.this, "Reacted", Toast.LENGTH_SHORT).show();
                else if (response.code() == 202)
                    Toast.makeText(AgendaActivity.this, "Unreacted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });

    }

    @Override
    public void onShareSelected(String agendaId) {
        String link = getResources().getString(R.string.iitp_web) + "agenda/" + agendaId;

        utils.shareIntent(this, link);
    }
}
