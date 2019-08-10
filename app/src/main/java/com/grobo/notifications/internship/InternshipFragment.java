package com.grobo.notifications.internship;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.util.ArrayList;
import java.util.List;

public class InternshipFragment extends Fragment {


    private RecyclerView recyclerView;
    public InternshipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_internship, container, false );
        recyclerView=view.findViewById( R.id.internship_view );
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
        InternshipAdapter internshipAdapter=new InternshipAdapter( getContext());
        recyclerView.setAdapter( internshipAdapter );
        List<InternshipItem> internshipItems = new ArrayList<>(  );
        internshipItems.add( new InternshipItem("Amazon","2 months","$2000-$3000","https://banner2.kisspng.com/20180426/lkw/kisspng-amazon-com-amazon-locker-gift-card-nasdaq-amzn-ret-amazon-logo-5ae209b47ee447.4444230415247630605198.jpg",(long)12521215,"Show Details >" ));
        internshipItems.add( new InternshipItem("Goldman Sachs","3 months","$2000-$4000","https://viterbicareers.usc.edu/wp-content/uploads/2019/02/Goldman-Sachs-Logo.png",(long)12521215,"Show Details >" ));
        internshipAdapter.setItemList( internshipItems );

        return view;
    }

}
