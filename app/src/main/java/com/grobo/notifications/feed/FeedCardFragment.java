package com.grobo.notifications.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import static com.grobo.notifications.feed.FeedDetailActivity.IMAGE_TRANSITION_NAME;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;

public class FeedCardFragment extends Fragment implements FeedDragLayout.GotoDetailListener {

    private ImageView imageView;

    private FeedItem currentFeed;
    private Context context;
    private String myMongoId;
    private FeedViewModel viewModel;
    private String feedId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null)
            this.context = getContext();

        myMongoId = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_MONGO_ID, "");

        if (viewModel == null)
            viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        currentFeed = viewModel.getFeedById(feedId);

        Iconify.with(new FontAwesomeModule());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_card, null);
        FeedDragLayout feedDragLayout = rootView.findViewById(R.id.drag_layout);
        feedDragLayout.setGotoDetailListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateData();
    }

    private void populateData() {
        if (getView() != null) {
            View view = getView();

            imageView = view.findViewById(R.id.image);
            ViewCompat.setTransitionName(imageView, IMAGE_TRANSITION_NAME);

            TextView title = view.findViewById(R.id.title);
            TextView poster = view.findViewById(R.id.poster_name);
            TextView likesCount = view.findViewById(R.id.react_count);
            ImageView likeIcon = view.findViewById(R.id.react);

            if (currentFeed != null) {
                Glide.with(context)
                        .load(currentFeed.getEventImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.baseline_dashboard_24)
                        .into(imageView);

                title.setText(currentFeed.getEventName());
                poster.setText(String.format("Posted by:\n%s", currentFeed.getDataPoster().getName()));

                if (currentFeed.getLikes() != null)
                    likesCount.setText(String.valueOf(currentFeed.getLikes().size()));
                else likesCount.setText("0");

                if (currentFeed.getLikes().contains(myMongoId))
                    likeIcon.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_heart).colorRes(R.color.deep_red));
                else
                    likeIcon.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_heart_o).colorRes(R.color.deep_red));

                view.findViewById(R.id.ll_feed_card_body).setOnClickListener(v -> {
                    Bundle b = new Bundle();
                    b.putString("feedId", currentFeed.getId());
                    FeedLikesDialogFragment fragment = new FeedLikesDialogFragment();
                    fragment.setArguments(b);
                    fragment.show(getChildFragmentManager(), fragment.getTag());
                });

                likeIcon.setOnClickListener(v -> {
                    FeedUtils.reactOnFeed(context, currentFeed.getId());
                    if (currentFeed.getLikes().contains(myMongoId)) {
                        likeIcon.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_heart_o).colorRes(R.color.deep_red));
                        currentFeed.getLikes().remove(myMongoId);
                        likesCount.setText(String.valueOf(currentFeed.getLikes().size()));
                        viewModel.insert(currentFeed);
                    } else {
                        likeIcon.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_heart).colorRes(R.color.deep_red));
                        currentFeed.getLikes().add(myMongoId);
                        likesCount.setText(String.valueOf(currentFeed.getLikes().size()));
                        viewModel.insert(currentFeed);}
                });
            }
        }
    }

    @Override
    public void gotoDetail() {
        Activity activity = (Activity) getContext();
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair<>(imageView, IMAGE_TRANSITION_NAME)
        );

        Intent intent = new Intent(activity, FeedDetailActivity.class);
        intent.putExtra("feedId", currentFeed.getId());
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public void bindData(String feedId) {
        this.feedId = feedId;
        populateData();
    }

    public String getBoundFeedId() {
        return feedId;
    }
}
