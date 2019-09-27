package com.grobo.notifications.mail;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.util.ArrayList;
import java.util.List;

public class EmailRecyclerAdapter extends RecyclerView.Adapter<EmailRecyclerAdapter.ViewHolder> {

    private List<EmailItem> mValues;

    public EmailRecyclerAdapter() {
        mValues = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_email_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        EmailItem current = mValues.get(position);
        holder.mIdView.setText(String.valueOf(current.getId()));
        holder.mContentView.setText(current.getContent());

//        holder.mView.setOnClickListener(v -> {
//
//        });
    }

    @Override
    public int getItemCount() {
        if (mValues != null)
            return mValues.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mIdView;
        TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
        }
    }

    public void setEmailValues(List<EmailItem> emails) {
        mValues = emails;
        notifyDataSetChanged();
    }

    public void addEmail(EmailItem email) {
        if (mValues == null)
            mValues = new ArrayList<>();
        int l = mValues.size();
        Log.e("size", l + " ");
        mValues.add(l, email);

        notifyItemInserted(l);
    }

}
