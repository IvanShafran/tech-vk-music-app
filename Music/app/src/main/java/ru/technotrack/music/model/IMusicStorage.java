package ru.technotrack.music.model;

import java.util.ArrayList;
import java.util.List;

public interface IMusicStorage {
    boolean isTrackAdded(Track track);
    void addTrack(Track track);
    void deleteTrack(Track track);
    ArrayList<Track> getTracks();

    void loadSavedTracks();
    void writeSavedTracks();

    //Возвратить треки с последнего плейлиста пользователя.
    List<Track> loadLastPlaylistTracks();
    //Записать треки с последнего плейлиста.
    void writeLastPlaylistTracks(List<Track> tracks);

    //Возвратить последний прослушанный трек.
    Track loadLastTrack();
    //Записать последний прослушанный трек.
    void writeLastTrack(Track track);
}
