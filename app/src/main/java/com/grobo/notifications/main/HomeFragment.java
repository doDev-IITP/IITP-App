package com.grobo.notifications.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.grobo.notifications.R;
import com.grobo.notifications.timetable.DayFragment;
import com.grobo.notifications.timetable.TimetableActivity;
import com.grobo.notifications.utils.ViewUtils;

import java.util.Calendar;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private int dayOfWeek;

    public HomeFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle( "IITP App" );
        super.onViewCreated( view, savedInstanceState );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_home, container, false );

        Calendar calendar = Calendar.getInstance();
        dayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );

        CardView qrCard = rootView.findViewById( R.id.card_home_qr );

        int margin = ViewUtils.dpToPx( 8 );
        int screenWidth = ViewUtils.getScreenWidth( getContext() ) / 2 - (2 * margin);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( new ViewGroup.LayoutParams( screenWidth, screenWidth ) );
        int p = screenWidth / 2;
        lp.setMargins( p + margin, margin, margin, margin );
        qrCard.setLayoutParams( lp );

        View qrFragment = rootView.findViewById( R.id.home_fr_mess_qr );

        qrFragment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QRFragment frag = new QRFragment();
                transactFragment( frag );
                new CountDownTimer( 200, 1000 ) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        frag.change( true );
                    }
                }.start();


            }
        } );

        View view2 = rootView.findViewById( R.id.home_view_2 );
        view2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getActivity(), TimetableActivity.class );
                intent.putExtra( "day", dayOfWeek );
                startActivity( intent );
            }
        } );

        Fragment fragment4 = DayFragment.newInstance( (dayOfWeek) );
        Objects.requireNonNull( getActivity() ).getSupportFragmentManager().beginTransaction()
                .add( R.id.home_frame_2, fragment4 ).commit();

        return rootView;
    }

    private void transactFragment(Fragment frag) {
        FragmentTransaction fragmentManager = Objects.requireNonNull( getActivity() ).getSupportFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations( R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out )
                .replace( R.id.frame_layout_main, frag )
                .addToBackStack( "later_fragment" )
                .commit();
    }

//    TODO: improve grid layout params

}
