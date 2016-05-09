package ru.technotrack.music.presenter;

import android.content.Context;

import java.util.ArrayList;

import ru.technotrack.music.model.Track;
import ru.technotrack.music.view.ICurrentPlaylistView;

public interface ICurrentPlaylistPresenter {
    void setPlaylist(ArrayList<Track> tracks);
    ArrayList<Track> getPlaylist();

    void setPlaylistView(ICurrentPlaylistView playlistView);

    boolean isMusicPlaying();
    int getPlayingTrack();

    void onPlayPressed(int trackIndex);
    void onPausePressed();

    boolean isTrackAdded(Track track);
    void onAddTrackPressed(Track track);
    void onDeleteTrackPressed(Track track);
    ArrayList<Track> getSavedTracks();

    void onCreate(Context context);
    void onDestroy(Context context);
}
