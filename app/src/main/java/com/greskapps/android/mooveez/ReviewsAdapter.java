package com.greskapps.android.mooveez;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    final private movieClickListener mClickListener;
    private List<Reviews> thumbnail_list;
    private Context context;

    public ReviewsAdapter(List<Reviews> rec_list, Context context, movieClickListener listener) {
        this.thumbnail_list = rec_list;
        this.context = context;
        this.mClickListener = listener;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reviews, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
        final Reviews thumb = thumbnail_list.get(position);

        holder.title.setText(thumb.getAuthor());
        holder.content.setText(thumb.getContent());
        holder.url.setText(thumb.getUrl());

        //set the listener if we need one
        //Picasso.get().load("https://image.tmdb.org/t/p/w500"+thumb.getUrl()).into(holder.imageUrl);
    }

    @Override
    public int getItemCount() {
        return thumbnail_list.size();
    }

    public interface movieClickListener {
        void onMovieItemClick(Reviews thumb);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView content;
        private TextView url;

        public ViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.review_name_tv);
            content = itemView.findViewById(R.id.review_tv);
            url = itemView.findViewById(R.id.review_url_tv);
            url.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Reviews thumbSend = thumbnail_list.get(clickedPosition);
            mClickListener.onMovieItemClick(thumbSend);
        }
    }
}
