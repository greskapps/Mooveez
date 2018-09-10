package com.greskapps.android.mooveez;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private List<Trailers> trailer_list;
    private Context context;
    final private movieClickListener mClickListener;

    //listener
    public interface movieClickListener {
        void onMovieItemClick(Trailers thumb);
    }

    public TrailersAdapter(List<Trailers> rec_list, Context context, movieClickListener listener) {
        this.trailer_list = rec_list;
        this.context = context;
        this.mClickListener = listener;
    }

    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailers, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TrailersAdapter.ViewHolder holder, int position) {
        final Trailers trailers = trailer_list.get(position);

        holder.title.setText(trailers.getTrailerName());
        holder.type.setText(trailers.getTrailerType());
        String videoName;
        videoName = "https://img.youtube.com/vi/" + trailers.getMovieID() + "/0.jpg";

        Picasso.get().load(videoName).into(holder.play);
    }

    @Override
    public int getItemCount() {
        return trailer_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageUrl;
        private TextView title;
        private TextView type;
        private ImageView play;

        public ViewHolder(final View itemView) {
            super(itemView);
            imageUrl = itemView.findViewById(R.id.trailer_thumbnail_iv);
            title = itemView.findViewById(R.id.trailer_name_tv);
            type = itemView.findViewById(R.id.type_tv);
            play = itemView.findViewById(R.id.play_btn);

            play.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Trailers trailer_send = trailer_list.get(clickedPosition);
            mClickListener.onMovieItemClick(trailer_send);
        }
    }
}
