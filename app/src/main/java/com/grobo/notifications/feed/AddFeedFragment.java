package com.grobo.notifications.feed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

public class AddFeedFragment extends Fragment {

    private OnFeedSavedListener callback;

    public AddFeedFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_feed, container, false);



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFeedSavedListener) {
            callback = (OnFeedSavedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface OnFeedSavedListener {
        void onFeedSaved(Uri uri);
    }
}
