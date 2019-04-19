package com.gohibo.album.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gohibo.album.R;

import java.util.ArrayList;

/**
 * Created by yuvi on 13-03-2018.
 */

class EditPhotoFiltersAdapter extends RecyclerView.Adapter<com.gohibo.album.helper.EditPhotoFiltersViewHolder> {
    private ArrayList<com.gohibo.album.helper.EditPhotoFiltersHelper> list;

    private Context context;
    private EditPhotoActivityHelper helper;

    public EditPhotoFiltersAdapter(ArrayList<com.gohibo.album.helper.EditPhotoFiltersHelper> data, Context context, EditPhotoActivityHelper helper) {
        list = data;
        this.context = context;
        this.helper = helper;
    }

    @Override
    public com.gohibo.album.helper.EditPhotoFiltersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_filters, parent, false);
        com.gohibo.album.helper.EditPhotoFiltersViewHolder holder = new com.gohibo.album.helper.EditPhotoFiltersViewHolder(view, helper);
        return holder;
    }
    @Override
    public void onBindViewHolder(final com.gohibo.album.helper.EditPhotoFiltersViewHolder holder, int position) {

        AppCompatImageView imageView = holder.coverImageView;
        AppCompatTextView textView = holder.textView;

        if(list.get(position) != null){

            imageView.setImageBitmap(list.get(position).getImageResource());
            textView.setText(list.get(position).getFilterName());
            //holder.coverImageView.setTag(list.get(position).getImageResourceId());
        }else{
            notifyItemRemoved(position);
        }
        //holder.coverImageView.setImageResource(list.get(position).getImageResourceId());

    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}

class EditPhotoFiltersViewHolder extends RecyclerView.ViewHolder {

    AppCompatImageView coverImageView;
    AppCompatTextView textView;

    EditPhotoFiltersViewHolder(View v, final EditPhotoActivityHelper helper) {
        super(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.onRecyclerFilterSelected(getPosition());
            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return helper.onRecyclerFilterLongSelected(getPosition());
            }
        });
        coverImageView = v.findViewById(R.id.sample_filter);
        textView = v.findViewById(R.id.filter_text);
    }
}

class EditPhotoFiltersHelper {
    Bitmap bitmap;
    String filterName;

    Bitmap getImageResource() {
        return bitmap;
    }
    String getFilterName() {
        return filterName;
    }
    void setImageResource(Bitmap imageResourceId) {
        this.bitmap = imageResourceId;
    }
    void setfilterName(String imageResourceId) {
        this.filterName = imageResourceId;
    }
}
