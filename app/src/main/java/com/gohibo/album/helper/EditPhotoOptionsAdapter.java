package com.gohibo.album.helper;

import android.content.Context;
import android.graphics.Bitmap;
import de.hdodenhof.circleimageview.CircleImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gohibo.album.R;

import java.util.ArrayList;

/**
 * Created by yuvi on 19-03-2018.
 */

class EditPhotoOptionsAdapter extends RecyclerView.Adapter<com.gohibo.album.helper.EditPhotoOptionsViewHolder> {
    private ArrayList<EditPhotoOptionsHelper> list;

    private Context context;
    private EditPhotoActivityHelper helper;

    public EditPhotoOptionsAdapter(ArrayList<com.gohibo.album.helper.EditPhotoOptionsHelper> data, Context context, EditPhotoActivityHelper helper) {
        list = data;
        this.context = context;
        this.helper = helper;
    }

    @Override
    public com.gohibo.album.helper.EditPhotoOptionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_options, parent, false);
        com.gohibo.album.helper.EditPhotoOptionsViewHolder holder = new com.gohibo.album.helper.EditPhotoOptionsViewHolder(view, helper);
        return holder;
    }
    @Override
    public void onBindViewHolder(final com.gohibo.album.helper.EditPhotoOptionsViewHolder holder, int position) {

        CircleImageView imageView = holder.coverImageView;
        AppCompatTextView textView = holder.textView;

        if(list.get(position) != null){

            imageView.setImageBitmap(list.get(position).getImageResource());
            textView.setText(list.get(position).getOptionName());
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

class EditPhotoOptionsViewHolder extends RecyclerView.ViewHolder {

    CircleImageView coverImageView;
    AppCompatTextView textView;

    EditPhotoOptionsViewHolder(View v, final EditPhotoActivityHelper helper) {
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
        coverImageView = v.findViewById(R.id.option_thumb);
        textView = v.findViewById(R.id.option_text);
    }
}

class EditPhotoOptionsHelper {
    Bitmap bitmap;
    String optionName;

    Bitmap getImageResource() {
        return bitmap;
    }
    String getOptionName() {
        return optionName;
    }
    void setImageResource(Bitmap imageResourceId) {
        this.bitmap = imageResourceId;
    }
    void setOptionName(String imageResourceId) {
        this.optionName = imageResourceId;
    }
}
