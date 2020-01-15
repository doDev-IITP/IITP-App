package com.grobo.notifications.admin.positions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.admin.AddNotificationFragment;
import com.grobo.notifications.utils.MistakeFragment;

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
        View view = inflater.inflate(R.layout.fragment_coordinator, container, false);

        CardView notification = view.findViewById(R.id.coordinator_notification_cv);
        CardView events = view.findViewById(R.id.coordinator_events_cv);
        CardView projects = view.findViewById(R.id.coordinator_projects_cv);

        notification.setOnClickListener(this);
        events.setOnClickListener(this);
        projects.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
//        bundle.putString("club", args.getString("club", ""));
//        bundle.putString("power", args.getString("power", ""));
        Fragment next;
        switch (v.getId()) {
            case R.id.coordinator_notification_cv:
                next = new AddNotificationFragment();
                next.setArguments(bundle);
                transactFragment(next);
                break;
            case R.id.coordinator_projects_cv:
                next = new MistakeFragment();
                next.setArguments(bundle);
                transactFragment(next);
                break;
            case R.id.coordinator_events_cv:
                Toast.makeText(getContext(), "Coming soon...", Toast.LENGTH_SHORT).show();
                break;
            default:
                next = new MistakeFragment();
        }

    }

    private void transactFragment(Fragment frag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
                .replace(R.id.frame_layout_admin, frag)
                .addToBackStack("later_fragment")
                .commit();
    }
}
