package com.grobo.notifications.main;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

import java.util.HashMap;
import java.util.Map;

public class LinksFragment extends Fragment {

    public LinksFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(getActivity()!=null)
        getActivity().setTitle( "Quick Links" );
        super.onViewCreated( view, savedInstanceState );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_links, container, false);

        HashMap<Integer ,String> map = new HashMap<>();

        map.put(R.id.tv_webmail,"https://mail.iitp.ac.in");
        map.put(R.id.tv_lib_res,"http://library.iitp.ac.in/");
        map.put(R.id.tv_lib_catalogue,"http://172.16.52.134:8380/opac/");
        map.put(R.id.tv_reg,"https://172.16.1.230/student/login2.asp");
        map.put(R.id.tv_prev_ques,"http://172.16.52.180/oldpapers");
        map.put(R.id.tv_intranet,"http://172.16.1.6/");
        map.put(R.id.tv_late_fee,"https://www.onlinesbi.com/sbicollect/icollecthome.htm");
        map.put(R.id.tv_institute_repo,"http://idr.iitp.ac.in/jspui");
        map.put(R.id.tv_inter_rel,"https://172.16.1.4/ir/");

        for(final Map.Entry<Integer, String> pair: map.entrySet()){
            TextView textView = rootView.findViewById(pair.getKey());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    browserIntent(pair.getValue());
                }
            });
        }

        return rootView;
    }

    private void browserIntent(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));
    }

}
