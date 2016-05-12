package ru.technotrack.music.model;

import java.util.ArrayList;

public interface IMusicStorage {
    boolean isTrackAdded(Track track);
    void addTrack(Track track);
    void deleteTrack(Track track);
    ArrayList<Track> getTracks();

    void loadTracks();
    void writeTracks();
}
