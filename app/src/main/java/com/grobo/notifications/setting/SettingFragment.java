package com.grobo.notifications.setting;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.grobo.notifications.R;
import com.grobo.notifications.utils.MistakeFragment;

public class SettingFragment extends Fragment {


    public SettingFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("About");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        rootView.findViewById(R.id.about_app).setOnClickListener(view -> transactFragment(new AboutAppFragment(), view));
        rootView.findViewById(R.id.secreteraies).setOnClickListener(view -> browserIntent("https://www.iitp.ac.in/hostel/#studentRepresentatives"));
        rootView.findViewById(R.id.faq).setOnClickListener(view -> Toast.makeText(getContext(), "Coming Soon..", Toast.LENGTH_SHORT).show());
        rootView.findViewById(R.id.contribute).setOnClickListener(view -> transactFragment(new MistakeFragment(), view));
        rootView.findViewById(R.id.feedback).setOnClickListener(view -> browserIntent("https://forms.gle/HdTdTK9VnA25FvGy6"));
        rootView.findViewById(R.id.suggest_name).setOnClickListener(view -> browserIntent("https://forms.gle/a9iDgGrQomDAL9A5A"));


        return rootView;
    }

    private void transactFragment(Fragment frag, View view) {

        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_nav_setting_to_nav_about);

//        Fragment current = getActivity().getSupportFragmentManager().findFragmentById( R.id.frame_layout_main );
//        current.setExitTransition( TransitionInflater.from( getContext() ).inflateTransition( android.R.transition.slide_left ) );
//        frag.setEnterTransition( TransitionInflater.from( getContext() ).inflateTransition( android.R.transition.slide_right ) );
//        Objects.requireNonNull( getActivity() ).getSupportFragmentManager().beginTransaction()
//                .replace( R.id.frame_layout_main, frag )
//                .addToBackStack( "later_fragment" )
//                .commit();
    }

    private void browserIntent(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));
    }


}
