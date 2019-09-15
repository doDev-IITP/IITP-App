package com.grobo.notifications.Mess;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.grobo.notifications.services.lostandfound.LostAndFoundRecyclerAdapter;
import com.grobo.notifications.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        RecyclerView recyclerView = view.findViewById( R.id.recycler_cancel );
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
        CancelMealAdapter cancelMealAdapter = new CancelMealAdapter( getContext(), (CancelMealAdapter.OnCancelSelectedListener) getActivity() );
        recyclerView.setAdapter( cancelMealAdapter );
        calendar1.add( Calendar.DATE, 1 );
        db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( Constants.WEBMAIL, "" ) ).collection( "cancel" ).orderBy( "timestamp", Query.Direction.DESCENDING ).addSnapshotListener( (queryDocumentSnapshots, e) -> {

            if (e == null) {
                messModels.clear();
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().size() > 0) {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    List<MessModel> documentSnapshots = queryDocumentSnapshots.toObjects( MessModel.class );
                    for (int i = 0; i < documentSnapshots.size(); i++) {
                        MessModel documentSnapshot = documentSnapshots.get( i );
                        DocumentSnapshot snapshot = documents.get( i );
                        if (documentSnapshot != null && documentSnapshot.getDays() != null)
                            for (int j = documentSnapshot.getDays().size() - 1; j >= 0; j--) {
                                Timestamp timestamp = documentSnapshot.getDays().get( j );
                                MessModel messModel = new MessModel();
                                messModel.setFull( documentSnapshot.isFull() );
                                List<Timestamp> day = new ArrayList<>();
                                day.add( timestamp );
                                messModel.setDocumentId( snapshot.getId() );
                                messModel.setMeals( documentSnapshot.getMeals() );
                                messModel.setTimestamp( documentSnapshot.getTimestamp() );
                                messModel.setDays( day );
                                messModels.add( messModel );
                            }

                    }


                }
                cancelMealAdapter.ItemList( messModels );
            }

        } );


        return view;
    }


}
