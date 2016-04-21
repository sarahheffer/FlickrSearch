package com.sarahheffer.flickrsearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sarahheffer.flickrsearch.R;
import com.sarahheffer.flickrsearch.app.FlickrSearchApp;
import com.sarahheffer.flickrsearch.models.Image;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerGridAdapter.ViewHolder> {

    @Inject
    Picasso picasso;

    private List<Image> imageList;
    private OnItemClickListener listener;
    private int imageSize;
    private int numSpans;

    public RecyclerGridAdapter(Context context, List<Image> images, int numSpans) {
        ((FlickrSearchApp) context.getApplicationContext()).getAppComponent().inject(this);
        this.imageList = images;
        this.numSpans = numSpans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, null);
        ViewHolder viewHolder = new ViewHolder(view);
        imageSize = parent.getMeasuredWidth()/numSpans;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image image = imageList.get(position);
        picasso.load(image.getImageURL()).resize(imageSize, imageSize).centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Image image);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.imageView)
        ImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            listener.onItemClick(imageList.get(position));
        }
    }
}
