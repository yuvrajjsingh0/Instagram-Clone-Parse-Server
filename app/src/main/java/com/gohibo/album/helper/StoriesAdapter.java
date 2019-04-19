package com.gohibo.album.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gohibo.album.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by yuvi on 22-01-2018.
 */

public class StoriesAdapter extends RecyclerView.Adapter<StoriesViewHolder> {
    private ArrayList<UserStoriesHelper> list;

    private Context context;

//save the context recievied via constructor in a local variable

    public StoriesAdapter(ArrayList<UserStoriesHelper> Data, Context context) {
        list = Data;
        this.context = context;
    }

    @Override
    public StoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_stories, parent, false);
        StoriesViewHolder holder = new StoriesViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final StoriesViewHolder holder, int position) {

        holder.titleTextView.setText(list.get(position).getCardName());
        ImageView imageView = holder.coverImageView;
        Picasso.with(context)
                .load(list.get(position).getImageResourceId().toString())
                .into(imageView);
        //holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
        holder.coverImageView.setTag(list.get(position).getImageResourceId());

}
    @Override
    public int getItemCount() {
        return list.size();
    }
}