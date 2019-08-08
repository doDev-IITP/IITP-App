package com.grobo.notifications.admin;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;
import com.grobo.notifications.feed.Converters;

import java.util.HashMap;
import java.util.List;

import static com.grobo.notifications.utils.Constants.COORDINATOR;
import static com.grobo.notifications.utils.Constants.CR;
import static com.grobo.notifications.utils.Constants.LEAD;
import static com.grobo.notifications.utils.Constants.SECRETARY;
import static com.grobo.notifications.utils.Constants.SUB_COORDINATOR;
import static com.grobo.notifications.utils.Constants.USER_POR;
import static com.grobo.notifications.utils.Constants.VP;

public class XPortalFragment extends Fragment {


    public XPortalFragment() {}

    PorAdapter adapter;
    OnPORSelectedListener callback;
    HashMap<String, String> powers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powers = new HashMap<>();
        powers.put(SUB_COORDINATOR, "Sub Coordinator");
        powers.put(LEAD, "Lead");
        powers.put(COORDINATOR, "Coordinator");
        powers.put(SECRETARY, "Secretary");
        powers.put(CR, "Class Representative");
        powers.put(VP, "Vice President");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xportal, container, false);

        ListView listView = view.findViewById(R.id.lv_fragment_portal);

        final List<String> itemsList = Converters.arrayFromString(PreferenceManager
                .getDefaultSharedPreferences(getContext()).getString(USER_POR, ""));



        adapter = new PorAdapter(getContext(), itemsList);
        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                callback.onPORSelected(itemsList.get(position));
//            }
//        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPORSelectedListener) {
            callback = (OnPORSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface OnPORSelectedListener {
        void onPORSelected(String club, String power);
    }

    public class PorAdapter extends ArrayAdapter<String> {

        PorAdapter(@NonNull Context context, List<String> strings) {
            super(context, 0, strings);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItemView = convertView;
            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_por, parent, false);
            }

            final String curr = getItem(position);
            final String[] array = curr.split("_", 2);

            final String finalPower = powers.get(array[0]);

            TextView text = listItemView.findViewById(R.id.card_por_text);
            text.setText( finalPower + "  " + array[1]);

            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onPORSelected(array[1], array[0]);
                }
            });

            return listItemView;
        }
    }

}
