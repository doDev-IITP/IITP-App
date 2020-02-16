package com.grobo.notifications.survey;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;
import com.grobo.notifications.survey.models.Survey;

import java.util.List;

public class SurveyRecyclerAdapter extends RecyclerView.Adapter<SurveyRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Survey> surveyList;


    public SurveyRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_survey, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if (surveyList != null) {
            final Survey current = surveyList.get(position);

            holder.title.setText(current.getTitle());
            holder.description.setText(current.getDescription());
            holder.poster.setText(current.getPoster().getName());

            holder.root.setOnClickListener(v -> {
                Intent intent = new Intent(context, SurveyDetailsActivity.class);
                intent.putExtra("survey", current);
                context.startActivity(intent);
            });


        } else {
            holder.title.setText("Loading ...");
        }
    }

    @Override
    public int getItemCount() {
        if (surveyList != null)
            return surveyList.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView root;
        TextView title;
        TextView description;
        LinearLayout tags;
        TextView tag1;
        TextView tag2;
        TextView poster;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.card_root);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            tags = itemView.findViewById(R.id.ll_tags);
            tag1 = itemView.findViewById(R.id.tag_1);
            tag2 = itemView.findViewById(R.id.tag_2);
            poster = itemView.findViewById(R.id.poster);
        }
    }

    void setSurveyList(List<Survey> surveys) {
        surveyList = surveys;
        notifyDataSetChanged();
    }

}
