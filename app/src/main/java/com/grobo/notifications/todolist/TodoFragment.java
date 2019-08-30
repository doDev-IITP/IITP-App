package com.grobo.notifications.todolist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.grobo.notifications.R;
import com.yalantis.beamazingtoday.interfaces.AnimationType;
import com.yalantis.beamazingtoday.interfaces.BatModel;
import com.yalantis.beamazingtoday.listeners.BatListener;
import com.yalantis.beamazingtoday.listeners.OnItemClickListener;
import com.yalantis.beamazingtoday.listeners.OnOutsideClickedListener;
import com.yalantis.beamazingtoday.ui.adapter.BatAdapter;
import com.yalantis.beamazingtoday.ui.animator.BatItemAnimator;
import com.yalantis.beamazingtoday.ui.callback.BatCallback;
import com.yalantis.beamazingtoday.ui.widget.BatRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoFragment extends Fragment implements OnItemClickListener, OnOutsideClickedListener, BatListener {

    public TodoFragment() {
    }

    private BatAdapter mAdapter;
    private List<BatModel> mGoals;
    private BatItemAnimator mAnimator;
    private BatRecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM YYYY", Locale.getDefault());
        requireActivity().setTitle(dateFormat.format(date));

//        titleText.setTypeface(TypefaceUtil.getAvenirTypeface(this));

        mRecyclerView = view.findViewById(R.id.bat_recycler_view);
        mAnimator = new BatItemAnimator();

        mRecyclerView.getView().setLayoutManager(new LinearLayoutManager(requireContext()));
        mRecyclerView.getView().setAdapter(mAdapter = new BatAdapter(mGoals = new ArrayList<BatModel>() {{
            add(new Goal("first"));
            add(new Goal("second"));
            add(new Goal("third"));
            add(new Goal("fourth"));
            add(new Goal("fifth"));
            add(new Goal("sixth"));
            add(new Goal("seventh"));
            add(new Goal("eighth"));
            add(new Goal("ninth"));
            add(new Goal("tenth"));
        }}, mListener, mAnimator).setOnItemClickListener(this).setOnOutsideClickListener(this));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new BatCallback(this));
        itemTouchHelper.attachToRecyclerView(mRecyclerView.getView());
        mRecyclerView.getView().setItemAnimator(mAnimator);
        mRecyclerView.setAddItemListener(this);

        view.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.revertAnimation();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private BatListener mListener = new BatListener() {
        @Override
        public void add(String string) {
            mGoals.add(0, new Goal(string));
            mAdapter.notify(AnimationType.ADD, 0);
        }

        @Override
        public void delete(int position) {
            mGoals.remove(position);
            mAdapter.notify(AnimationType.REMOVE, position);
        }

        @Override
        public void move(int from, int to) {
            if (from >= 0 && to >= 0) {

                //if you use 'BatItemAnimator'
                mAnimator.setPosition(to);

                BatModel model = mGoals.get(from);
                mGoals.remove(model);
                mGoals.add(to, model);
                mAdapter.notify(AnimationType.MOVE, from, to);

                if (from == 0 || to == 0) {
                    mRecyclerView.getView().scrollToPosition(Math.min(from, to));
                }
            }
        }
    };

    @Override
    public void add(String string) {
        mGoals.add(0, new Goal(string));
        mAdapter.notify(AnimationType.ADD, 0);
    }

    @Override
    public void delete(int position) {
        mGoals.remove(position);
        mAdapter.notify(AnimationType.REMOVE, position);
    }

    @Override
    public void move(int from, int to) {
        if (from >= 0 && to >= 0) {
            mAnimator.setPosition(to);
            BatModel model = mGoals.get(from);
            mGoals.remove(model);
            mGoals.add(to, model);
            mAdapter.notify(AnimationType.MOVE, from, to);

            if (from == 0 || to == 0) {
                mRecyclerView.getView().scrollToPosition(Math.min(from, to));
            }
        }
    }

    @Override
    public void onClick(BatModel item, int position) {
        Toast.makeText(requireContext(), item.getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOutsideClicked() {
        mRecyclerView.revertAnimation();
    }
}
