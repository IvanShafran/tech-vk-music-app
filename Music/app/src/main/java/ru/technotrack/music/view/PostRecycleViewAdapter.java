package ru.technotrack.music.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.technotrack.music.R;
import ru.technotrack.music.model.Post;
import ru.technotrack.music.model.Track;
import ru.technotrack.music.net.ImageManager;
import ru.technotrack.music.presenter.CurrentPlaylistPresenter;

public class PostRecycleViewAdapter
        extends RecyclerView.Adapter<PostRecycleViewAdapter.ViewHolder>
        implements ICurrentPlaylistView {

    private List<Post> mPosts;
    private int mPictureWidth;
    private int mPictureHeight;
    private String mPictureType;
    private Context mContext;

    private Map<Integer, WeakReference<ImageView>> mTrackImageViews;
    private ArrayList<Track> mTracks;
    private Map<Integer, Map<Integer, Integer>> mTrackIndexInPlaylist;

    public PostRecycleViewAdapter(List<Post> posts,
                                  Context context,
                                  String pictureType,
                                  int pictureWidth, int pictureHeight) {
        mContext = context;
        mPosts = posts;
        mPictureType = pictureType;
        mPictureWidth = pictureWidth;
        mPictureHeight = pictureHeight;

        //инициализация, связанная с проигрыванием и отображением музыки
        mTrackImageViews = new HashMap<>();
        mTracks = new ArrayList<>();
        mTrackIndexInPlaylist = new HashMap<>();
        for (int i = 0; i < mPosts.size(); ++i) {
            Post post = mPosts.get(i);
            mTrackIndexInPlaylist.put(i, new HashMap<Integer, Integer>());
            for (int j = 0; j < post.getTracks().size(); ++j) {
                mTrackIndexInPlaylist.get(i).put(j, mTracks.size());
                mTracks.add(post.getTracks().get(j));
            }
         }

        CurrentPlaylistPresenter.getInstance().setPlaylistView(this);
    }

    @Override
    public void showAsPlaying(int trackIndex) {
        if (mTrackImageViews.containsKey(trackIndex)) {
            ImageView imageView = mTrackImageViews.get(trackIndex).get();
            if (imageView != null) {
                imageView.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                imageView.setTag(R.drawable.ic_pause_circle_outline_black_48dp);
            }
        }
    }

    @Override
    public void showAsNotPlaying(int trackIndex) {
        if (mTrackImageViews.containsKey(trackIndex)) {
            ImageView imageView = mTrackImageViews.get(trackIndex).get();
            if (imageView != null) {
                imageView.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                imageView.setTag(R.drawable.ic_play_circle_outline_black_48dp);
            }
        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_cardview_list_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.setText(post.getText());
        holder.setPicture(post.getPictureLink());
        for (int i = 0; i < post.getTracks().size(); ++i) {
            holder.addTrack(position, i, post.getTracks().get(i));
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

        private void setPlayPause(ImageView playPause,
                                  int postIndex, int trackInPostIndex,
                                  Track track) {

            final int indexInPlaylist = mTrackIndexInPlaylist.get(postIndex).get(trackInPostIndex);

            if (CurrentPlaylistPresenter.getInstance().isMusicPlaying() &&
                    CurrentPlaylistPresenter.getInstance().getPlaylist().equals(mTracks) &&
                    CurrentPlaylistPresenter.getInstance().getPlayingTrack() == indexInPlaylist) {
                playPause.setTag(R.drawable.ic_pause_circle_outline_black_48dp);
                playPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            } else {
                playPause.setTag(R.drawable.ic_play_circle_outline_black_48dp);
            }

            mTrackImageViews.put(indexInPlaylist, new WeakReference<>(playPause));

            playPause.setOnClickListener(new View.OnClickListener() {

                boolean isPlayState(ImageView v) {
                    return ((int) v.getTag()) == R.drawable.ic_play_circle_outline_black_48dp;
                }

                @Override
                public void onClick(View v) {
                    ImageView imageView = (ImageView) v;
                    if (isPlayState(imageView)) {
                        CurrentPlaylistPresenter.getInstance().setPlaylist(mTracks);
                        CurrentPlaylistPresenter.getInstance().onPlayPressed(indexInPlaylist);
                    } else {
                        CurrentPlaylistPresenter.getInstance().onPausePressed();
                    }
                }
            });
        }

        private void setAddDelete(ImageView addDelete, final Track track) {
            if (CurrentPlaylistPresenter.getInstance().isTrackAdded(track)) {
                addDelete.setImageResource(R.drawable.ic_delete_black_48dp);
                addDelete.setTag(R.drawable.ic_delete_black_48dp);
            } else {
                addDelete.setImageResource(R.drawable.ic_add_black_48dp);
                addDelete.setTag(R.drawable.ic_add_black_48dp);
            }

            addDelete.setOnClickListener(new View.OnClickListener() {

                private boolean isAddState(ImageView v) {
                    return ((int) v.getTag()) == R.drawable.ic_add_black_48dp;
                }

                @Override
                public void onClick(View v) {
                    ImageView imageView = (ImageView) v;
                    if (isAddState(imageView)) {
                        CurrentPlaylistPresenter.getInstance().onAddTrackPressed(track);
                        imageView.setImageResource(R.drawable.ic_delete_black_48dp);
                        imageView.setTag(R.drawable.ic_delete_black_48dp);

                        Toast.makeText(mContext, "Track was saved", Toast.LENGTH_SHORT).show();
                    } else {
                        CurrentPlaylistPresenter.getInstance().onDeleteTrackPressed(track);
                        imageView.setImageResource(R.drawable.ic_add_black_48dp);
                        imageView.setTag(R.drawable.ic_add_black_48dp);

                        Toast.makeText(mContext, "Track was deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void addTrack(int postIndex, int trackInPostIndex, Track track) {
            View view = View.inflate(mContext, R.layout.track_layout, null);
            ((TextView) view.findViewById(R.id.track_full_name))
                    .setText(String.format(mContext.getString(R.string.artist_name_separator),
                            track.getArtist(), track.getName()));


            ImageView playPause = (ImageView) view.findViewById(R.id.track_play_pause);
            setPlayPause(playPause, postIndex, trackInPostIndex, track);

            ImageView addDelete = (ImageView) view.findViewById(R.id.track_add_delete);
            setAddDelete(addDelete, track);


            mLinearLayout.addView(view);
        }

        public ViewHolder(View item) {
            super(item);

            mText = (TextView) item.findViewById(R.id.post_text);
            mImageView = (ImageView) item.findViewById(R.id.post_image);
            mLinearLayout = (LinearLayout) item.findViewById(R.id.post_tracks);
        }
    }
}
