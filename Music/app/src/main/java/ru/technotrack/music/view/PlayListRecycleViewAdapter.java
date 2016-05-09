package ru.technotrack.music.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.technotrack.music.R;
import ru.technotrack.music.model.Track;
import ru.technotrack.music.presenter.CurrentPlaylistPresenter;

public class PlayListRecycleViewAdapter
        extends RecyclerView.Adapter<PlayListRecycleViewAdapter.ViewHolder>
        implements ICurrentPlaylistView {

    private ArrayList<Track> mTracks;
    private Context mContext;
    private Map<Integer, WeakReference<ImageView>> mTrackImageViews;

    public PlayListRecycleViewAdapter(Context context, ArrayList<Track> tracks) {
        mContext = context;
        mTracks = tracks;
        mTrackImageViews = new HashMap<>();
        CurrentPlaylistPresenter.getInstance().setPlaylistView(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setTrack(position);
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mText;
        private ImageView mPlayPause;
        private ImageView mAddDelete;
        private int mTrackPosition;

        private void setPlayPause(final int indexInPlaylist, Track track) {

            if (CurrentPlaylistPresenter.getInstance().isMusicPlaying() &&
                    CurrentPlaylistPresenter.getInstance().getPlaylist().equals(mTracks) &&
                    CurrentPlaylistPresenter.getInstance().getPlayingTrack() == indexInPlaylist) {

                mPlayPause.setTag(R.drawable.ic_pause_circle_outline_black_48dp);
                mPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            } else {
                mPlayPause.setTag(R.drawable.ic_play_circle_outline_black_48dp);
            }

            mTrackImageViews.put(indexInPlaylist, new WeakReference<>(mPlayPause));

            mPlayPause.setOnClickListener(new View.OnClickListener() {

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

        private void setAddDelete(final Track track) {
            if (CurrentPlaylistPresenter.getInstance().isTrackAdded(track)) {
                mAddDelete.setImageResource(R.drawable.ic_delete_black_48dp);
                mAddDelete.setTag(R.drawable.ic_delete_black_48dp);
            } else {
                mAddDelete.setImageResource(R.drawable.ic_add_black_48dp);
                mAddDelete.setTag(R.drawable.ic_add_black_48dp);
            }

            mAddDelete.setOnClickListener(new View.OnClickListener() {

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

        public void setTrack(int trackPosition) {
            mTrackPosition = trackPosition;

            Track track = mTracks.get(mTrackPosition);
            mText.setText(String.format(mContext.getString(R.string.artist_name_separator),
                    track.getArtist(), track.getName()));

            setPlayPause(trackPosition, track);
            setAddDelete(track);
        }

        public ViewHolder(View item) {
            super(item);

            mText = (TextView) item.findViewById(R.id.track_full_name);
            mPlayPause = (ImageView) item.findViewById(R.id.track_play_pause);
            mAddDelete = (ImageView) item.findViewById(R.id.track_add_delete);
        }
    }
}
