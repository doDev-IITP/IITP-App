package com.grobo.notifications.Mess;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.ImageViewerActivity;

import static com.grobo.notifications.utils.Constants.MESS_MENU_URL;

public class MessFragment extends Fragment {

    public MessFragment() {
    }

    private ProgressDialog progressDialog;
    private SharedPreferences prefs;

    private SparseArray<String> messes = new SparseArray<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mess, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null)
            getActivity().setTitle("Mess");

        ImageView messMenu = view.findViewById(R.id.mess_menu);
        String url = FirebaseRemoteConfig.getInstance().getString(MESS_MENU_URL);
        Glide.with(this).load(url).centerInside().into(messMenu);

        messMenu.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), ImageViewerActivity.class);
            i.putExtra("image_url", url);
            startActivity(i);
        });

        View qrFragment = view.findViewById(R.id.mess_fr_qr);
        qrFragment.setOnClickListener(v -> {
            NavHostFragment.findNavController(MessFragment.this).navigate(R.id.nav_qr);
        });

        super.onViewCreated(view, savedInstanceState);
    }
    }

