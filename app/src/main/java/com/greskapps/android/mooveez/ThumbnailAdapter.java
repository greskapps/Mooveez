package com.greskapps.android.mooveez;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {

    final private movieClickListener mClickListener;
    private List<Thumbnail> thumbnail_list;
    private Context context;

    public ThumbnailAdapter(List<Thumbnail> rec_list, Context context, movieClickListener listener) {
        this.thumbnail_list = rec_list;
        this.context = context;
        this.mClickListener = listener;
    }

    @Override
    public ThumbnailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ThumbnailAdapter.ViewHolder holder, int position) {
        final Thumbnail thumb = thumbnail_list.get(position);

        //here we set the listener if we need one
        Picasso.get().load("https://image.tmdb.org/t/p/w500" + thumb.getUrl()).into(holder.imageUrl);
    }

    @Override
    public int getItemCount() {
        return thumbnail_list.size();
    }

    //listener
    public interface movieClickListener {
        void onMovieItemClick(Thumbnail thumb);
    }

    //the view holder with the contents
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageUrl;

        public ViewHolder(final View itemView) {
            super(itemView);
            imageUrl = itemView.findViewById(R.id.movie_item_thumb_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Thumbnail thumbSend = thumbnail_list.get(clickedPosition);
            mClickListener.onMovieItemClick(thumbSend);
        }
    }


}
