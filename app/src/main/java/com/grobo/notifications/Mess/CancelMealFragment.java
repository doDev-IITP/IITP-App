package com.grobo.notifications.Mess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import com.grobo.notifications.R;

public class CancelMealFragment extends Fragment {

    public CancelMealFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_cancel_meal_fragment, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        CardView breakfast = view.findViewById( R.id.breakfast_cv );
        CardView lunch = view.findViewById( R.id.lunch_cv );
        CardView snacks = view.findViewById( R.id.snacks_cv );
        CardView dinner = view.findViewById( R.id.dinner_cv );
        CardView full = view.findViewById( R.id.full_cv );

        Fragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();

        breakfast.setOnClickListener(view15 -> {
            bundle.putInt( "meal", 1 );
            fragment.setArguments( bundle );
            transactFragment( fragment );
        });

        lunch.setOnClickListener(view14 -> {
            bundle.putInt( "meal", 2 );
            fragment.setArguments( bundle );
            transactFragment( fragment );
        });
        snacks.setOnClickListener(view13 -> {
            bundle.putInt( "meal", 3 );
            fragment.setArguments( bundle );
            transactFragment( fragment );
        });
        dinner.setOnClickListener(view12 -> {
            bundle.putInt( "meal", 4 );
            fragment.setArguments( bundle );
            transactFragment( fragment );
        });
        full.setOnClickListener(view1 -> {
            bundle.putInt( "meal", 5 );
            fragment.setArguments( bundle );
            transactFragment( fragment );
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void transactFragment(Fragment frag) {

        FragmentManager manager = requireActivity().getSupportFragmentManager();

        Fragment current = manager.findFragmentById( R.id.frame_layout_home );
        if (current != null) {
            current.setExitTransition( TransitionInflater.from( requireContext() ).inflateTransition( android.R.transition.explode ) );
            frag.setEnterTransition( TransitionInflater.from( requireContext() ).inflateTransition( R.transition.default_transition ) );
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace( R.id.frame_layout_home, frag )
                .addToBackStack(frag.getTag())
                .commit();
    }


}
