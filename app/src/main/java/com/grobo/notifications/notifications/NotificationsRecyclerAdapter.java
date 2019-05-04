package com.grobo.notifications.notifications;

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
import com.grobo.notifications.utils.DateTimeUtil;

import java.util.List;

public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notificationList;
    final private OnNotificationSelectedListener callback;

    public NotificationsRecyclerAdapter(Context context, OnNotificationSelectedListener listener){
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationViewHolder holder, int position) {
        if (notificationList != null) {
            final Notification current = notificationList.get(position);

            holder.notificationTitle.setText(current.getTitle());
            holder.notification.setText(current.getBody());
            Glide.with(context)
                    .load(current.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(holder.notificationImage);

            if(current.isStarred()){
                holder.notificationStar.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_black_24dp));
            } else {
                holder.notificationStar.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
            }

            holder.notificationTime.setText(DateTimeUtil.getDate(current.getTimeStamp()));

            holder.notificationStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onStarSelected(current);
                }
            });

            holder.notificationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onNotificationSelected(current);
                }
            });


        } else {
            holder.notification.setText("Loading ...");
        }
    }

    @Override
    public int getItemCount() {
        if (notificationList != null)
            return notificationList.size();
        else return 0;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView notification;
        ImageView notificationImage;
        ImageView notificationStar;
        TextView notificationTime;
        TextView notificationTitle;
        LinearLayout notificationLayout;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.tv_notification_card);
            notificationImage = itemView.findViewById(R.id.iv_notification_card);
            notificationStar = itemView.findViewById(R.id.iv_notification_star);
            notificationTime = itemView.findViewById(R.id.tv_notification_time);
            notificationTitle = itemView.findViewById(R.id.tv_notification_title);
            notificationLayout = itemView.findViewById(R.id.ll_card_notification);
        }
    }

    void setNotificationList(List<Notification> notifications){
        notificationList = notifications;
        notifyDataSetChanged();
    }

    public interface OnNotificationSelectedListener {
        void onStarSelected(Notification notification);
        void onNotificationSelected(Notification notification);
    }

}
