package com.grobo.notifications.admin.positions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;
import com.grobo.notifications.admin.AddNotificationFragment;
import com.grobo.notifications.admin.clubevents.ClubEventFragment;
import com.grobo.notifications.feed.FeedFragment;
import com.grobo.notifications.utils.MistakeFragment;

public class CoordinatorFragment extends Fragment implements View.OnClickListener {

    public CoordinatorFragment() {}

    private Bundle args;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator, container, false);

        CardView feed = view.findViewById(R.id.coordinator_feed_cv);
        CardView notification = view.findViewById(R.id.coordinator_notification_cv);
        CardView events = view.findViewById(R.id.coordinator_events_cv);
        CardView projects = view.findViewById(R.id.coordinator_projects_cv);

        feed.setOnClickListener(this);
        notification.setOnClickListener(this);
        events.setOnClickListener(this);
        projects.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("club", args.getString("club", ""));
        bundle.putString("power", args.getString("power", ""));
        Fragment next;
        switch (v.getId()) {
            case R.id.coordinator_feed_cv:
                next = new FeedFragment();
                break;
            case R.id.coordinator_notification_cv:
                next = new AddNotificationFragment();
                break;
            case R.id.coordinator_projects_cv:
                next = new MistakeFragment();
                break;
            case R.id.coordinator_events_cv:
                next = new ClubEventFragment();
                break;
            default:
                next = new MistakeFragment();
        }

        next.setArguments(bundle);
        transactFragment(next);
    }

    private void transactFragment(Fragment frag){
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
                .replace(R.id.frame_layout_admin, frag)
                .addToBackStack("later_fragment")
                .commit();
    }
}
