package com.grobo.notifications.admin.positions;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.admin.clubevents.ClubEventActivity;
import com.grobo.notifications.admin.notify.NewNotificationActivity;
import com.grobo.notifications.clubs.EditClubDetailActivity;

public class CoordinatorFragment extends Fragment implements View.OnClickListener {

    public CoordinatorFragment() {
    }

    private PORItem currentPOR;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("data")) {
            currentPOR = getArguments().getParcelable("data");
            if (currentPOR == null && getActivity() != null) {
                Toast.makeText(getActivity(), "Invalid POR", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        } else {
            Toast.makeText(getActivity(), "Invalid POR", Toast.LENGTH_LONG).show();
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coordinator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (currentPOR != null && getActivity() != null) {
            getActivity().setTitle(String.format("%s (%s)", currentPOR.getClubName(), currentPOR.getPosition()));
        }

        CardView notification = view.findViewById(R.id.coordinator_notification_cv);
        CardView events = view.findViewById(R.id.coordinator_events_cv);
        CardView projects = view.findViewById(R.id.coordinator_projects_cv);
        CardView messages = view.findViewById(R.id.coordinator_messages_cv);
        CardView clubDetails = view.findViewById(R.id.coordinator_club_details_cv);

        notification.setOnClickListener(this);
        events.setOnClickListener(this);
        projects.setOnClickListener(this);
        messages.setOnClickListener(this);
        clubDetails.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("por", currentPOR);

        Fragment next = null;
        switch (v.getId()) {
            case R.id.coordinator_notification_cv:
                Intent i = new Intent(getContext(), NewNotificationActivity.class);
                i.putExtra("por", currentPOR);
                startActivity(i);
                break;
            case R.id.coordinator_events_cv:
                Intent i1 = new Intent(getContext(), ClubEventActivity.class);
                i1.putExtra("por", currentPOR);
                startActivity(i1);
                break;
            case R.id.coordinator_club_details_cv:
                Intent i2 = new Intent(getContext(), EditClubDetailActivity.class);
                i2.putExtra("por", currentPOR);
                startActivity(i2);
                break;
            default:
                Toast.makeText(getContext(), "Coming soon...", Toast.LENGTH_SHORT).show();
                break;
        }

        if (next != null) {
            next.setArguments(bundle);
            transactFragment(next);
        }
    }

    private void transactFragment(Fragment frag) {
        if (getActivity() != null)
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
                    .replace(R.id.frame_layout_admin, frag)
                    .addToBackStack(frag.getTag())
                    .commit();
    }
}
