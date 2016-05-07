package ru.technotrack.music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class PostRecycleViewAdapter
        extends RecyclerView.Adapter<PostRecycleViewAdapter.ViewHolder> {

    private List<Post> mPosts;
    private int mPictureWidth;
    private int mPictureHeight;
    private String mPictureType;
    private Context mContext;

    public PostRecycleViewAdapter(List<Post> posts,
                                  Context context,
                                  String pictureType,
                                  int pictureWidth, int pictureHeight) {
        mContext = context;
        mPosts = posts;
        mPictureType = pictureType;
        mPictureWidth = pictureWidth;
        mPictureHeight = pictureHeight;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.setText(post.getText());
        holder.setPicture(post.getPictureLink());
        for (Track track : post.getTracks()) {
            holder.addTrack(track);
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mText;
        private LinearLayout mLinearLayout;
        private ImageView mImageView;

        public void setText(String text) {
            mText.setText(text);
        }

        public void setPicture(String link) {
            ImageManager.getInstance().loadBitmap(mContext,
                    link,
                    mPictureType,
                    mImageView,
                    mPictureWidth,
                    mPictureHeight);
        }

        public void addTrack(Track track) {
            View view = View.inflate(mContext, R.layout.track_layout, null);
            ((TextView) view.findViewById(R.id.track_full_name))
                    .setText(String.format(mContext.getString(R.string.artist_name_separator),
                            track.getArtist(), track.getName()));
            mLinearLayout.addView(view);
            //TODO воспроизведение и т.п.
        }

        public ViewHolder(View item) {
            super(item);

            mText = (TextView) item.findViewById(R.id.post_text);
            mImageView = (ImageView) item.findViewById(R.id.post_image);
            mLinearLayout = (LinearLayout) item.findViewById(R.id.post_tracks);
        }
    }
}
