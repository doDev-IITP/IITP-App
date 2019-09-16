package com.grobo.notifications.Mess;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grobo.notifications.R;
import com.grobo.notifications.admin.clubevents.ClubEventDetailFragment;
import com.grobo.notifications.network.MessRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.services.lostandfound.LostAndFoundRecyclerAdapter;
import com.grobo.notifications.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class CancelMealFragment extends Fragment {


    public CancelMealFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        View view = inflater.inflate( R.layout.fragment_cancel_meal_fragment, container, false );
        List<MessModel> messModels = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ) );

        CardView breakfast = view.findViewById( R.id.breakfast_cv );
        CardView lunch = view.findViewById( R.id.lunch_cv );
        CardView snacks = view.findViewById( R.id.snacks_cv );
        CardView dinner = view.findViewById( R.id.dinner_cv );
        CardView full = view.findViewById( R.id.full_cv );

        Fragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();


        breakfast.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt( "meal", 1 );
                fragment.setArguments( bundle );
                transactFragment( fragment );
            }
        } );

        lunch.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt( "meal", 2 );
                fragment.setArguments( bundle );
                transactFragment( fragment );
            }
        } );
        snacks.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt( "meal", 3 );
                fragment.setArguments( bundle );
                transactFragment( fragment );
            }
        } );
        dinner.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt( "meal", 4 );
                fragment.setArguments( bundle );
                transactFragment( fragment );
            }
        } );
        full.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt( "meal", 5 );
                fragment.setArguments( bundle );
                transactFragment( fragment );
            }
        } );
        return view;
    }

    private void transactFragment(Fragment frag) {


        Fragment current = getFragmentManager().findFragmentById( R.id.frame_layout_home );
//        if (current != null) {
//            current.setExitTransition( TransitionInflater.from( requireContext() ).inflateTransition( android.R.transition.fade ) );
//            frag.setEnterTransition( TransitionInflater.from( requireContext() ).inflateTransition( android.R.transition.fade ) );
//        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace( R.id.frame_layout_home, frag )
                .addToBackStack( null )
                .commit();
    }


}
