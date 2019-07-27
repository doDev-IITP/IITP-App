package com.grobo.notifications.internship;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;
import com.grobo.notifications.feed.FeedItem;
import com.grobo.notifications.feed.FeedRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InternshipAdapter extends RecyclerView.Adapter<InternshipAdapter.InternshipViewHolder> {

    private Context context;
    private List<InternshipItem> InternshipItemList;
    //final private InternshipAdapter.OnInternshipSelectedListener callback;


    public InternshipAdapter( Context context){
        this.context = context;
        //callback = listener;
    }

    @NonNull
    @Override
    public InternshipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_internship, parent, false);

        return new InternshipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InternshipViewHolder holder, int position) {

        InternshipItem e=InternshipItemList.get( position );
        holder.title.setText( e.getCompanyName() );
        holder.details.setText( e.getDetails() );
        holder.stipend.setText( e.getStipend() );
        holder.duration.setText( e.getDuration() );
        Date date = new Date(e.getLastDate());
        SimpleDateFormat format = new SimpleDateFormat("dd MMM YYYY, hh:mm a");
        holder.applyBy.setText(format.format(date));
        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_black_24dp));
//        Glide.with(context)
//                .load(e.getImageUrl())
//                .fitCenter()
//                .placeholder(R.drawable.baseline_dashboard_24)
//                .into(holder.imageView);
//        holder.imageView.setTransitionName("transition" + position);

    }

    @Override
    public int getItemCount() {
        if (InternshipItemList != null)
            return InternshipItemList.size();
        else return 0;
    }

    class InternshipViewHolder extends RecyclerView.ViewHolder {

        View availableIndicator;
        TextView title;
        TextView duration;
        TextView stipend;
        TextView applyBy;
        LinearLayout rootLayout;
        LinearLayout sessionLayout;
        ImageView imageView;
        TextView details;

        InternshipViewHolder(@NonNull View itemView) {
            super(itemView);
            availableIndicator = itemView.findViewById( R.id.item_available_indicator);
            duration = itemView.findViewById(R.id.item_duration_text);
            title = itemView.findViewById(R.id.item_title_text);
            stipend = itemView.findViewById(R.id.item_stipend_text);
            applyBy=itemView.findViewById( R.id.item_apply_text );
            rootLayout = itemView.findViewById(R.id.itemScheduleRootLayout);
            sessionLayout = itemView.findViewById(R.id.item_session_layout);
            imageView = itemView.findViewById(R.id.iv_notification_star);
            details=itemView.findViewById( R.id.show_details );
        }
    }

    void setItemList(List<InternshipItem> internshipItemList){
        InternshipItemList = internshipItemList;
        notifyDataSetChanged();
    }

    public interface OnInternshipSelectedListener {
        void onInternshipSelected(String id, View view, int position);
    }
}