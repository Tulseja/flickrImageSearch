package com.imagegallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.imagegallery.util.ImageLoader;
import com.imagegallery.data.ImageItem;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private ArrayList<ImageItem> mImagesResponse;
    private Context mContext;

    ImageListAdapter(ArrayList<ImageItem> imageListResponses, Context context) {
        mImagesResponse = imageListResponses;
        mContext = context;
    }

    public void setList(ArrayList<ImageItem> imageListResponses) {
        mImagesResponse = imageListResponses;
    }

    @NonNull
    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListAdapter.ViewHolder holder, final int position) {
        ImageLoader.getInstance(mContext.getApplicationContext()).loadImage(generateUrl(mImagesResponse.get(holder.getAdapterPosition())) , holder.mIvImage, R.mipmap.ic_launcher);

        final ImageListAdapter.ViewHolder viewholder = holder;
        holder.mIvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, viewholder.mIvImage,
                                "image_transition");
                intent.putParcelableArrayListExtra("image_list", mImagesResponse);
                intent.putExtra("image_position", position);
                mContext.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImagesResponse.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mIvImage = itemView.findViewById(R.id.iv_image);
        }

    }

    private String generateUrl(ImageItem imageItem) {
        String url = "http://farm"+imageItem.getFarm()+".static.flickr.com/"+imageItem.getServer()+"/"+imageItem.getId()+"_"+imageItem.getSecret()+".jpg";
        Log.d("Response: " ,""+ url);
        return url;
    }
}
