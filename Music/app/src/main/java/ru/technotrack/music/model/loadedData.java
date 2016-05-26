package ru.technotrack.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Kirill on 24.05.2016.
 */
public class LoadedData{
    private List<Track> mSavedTracks;
    private List<Track> mLastPlaylistTracks;
    private Track mLastTrack;

    public LoadedData(List<Track> mSavedTracks, List<Track> mLastPlaylistTracks, Track mLastTrack) {
        this.mSavedTracks = mSavedTracks;
        this.mLastPlaylistTracks = mLastPlaylistTracks;
        this.mLastTrack = mLastTrack;
    }

    public List<Track> getSavedTracks() {
        return mSavedTracks;
    }

    public List<Track> getLastPlaylistTracks() {
        return mLastPlaylistTracks;
    }

    public Track getLastTrack() {
        return mLastTrack;
    }
}
