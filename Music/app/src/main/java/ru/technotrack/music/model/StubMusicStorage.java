package ru.technotrack.music.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StubMusicStorage implements IMusicStorage {
    private Map<String, Track> mTracks = new HashMap<>();
    private TrackDB mDatabase;

    public static final int SAVED_TRACK = 0;
    public static final int LAST_PLAYLIST_TRACKS = 1;
    public static final int LAST_TRACK = 2;

    public StubMusicStorage(TrackDB database) {
        this.mDatabase = database;
    }

    @Override
    public void addTrack(Track track) {
        mTracks.put(track.getLink(), track);
    }

    @Override
    public void deleteTrack(Track track) {
        if (mTracks.containsKey(track.getLink())) {
            mTracks.remove(track.getLink());
        }
    }

    @Override
    public boolean isTrackAdded(Track track) {
        return mTracks.containsKey(track.getLink());
    }

    @Override
    public ArrayList<Track> getTracks() {
        return new ArrayList<>(mTracks.values());
    }

    @Override
    public void loadSavedTracks() {
        /*Track track = new Track();
        track.setArtist("Moby");
        track.setName("Mistake");
        track.setLink("https://cs1-48v4.vk-cdn.net/p22/477a7457da0186.mp3?extra=5On9Ksqq7MqGpC0bmTMb5tCr_1Qt9PgcIMaRPof1chdje-1npGfO5WSVXRwllAaTHNBiK3TXI4D7wNJuQHja8YnZEECT4XyeWjqY9S39e-W-ga26-h8tQDz49KVwu0-VcWy74hA1tJxU,224");

        mTracks.put(track.getLink(), track);

        track = new Track();
        track.setArtist("Israel IZ Kamakawiwo'ole");
        track.setName("White Sandy Beach of Hawai'i");
        track.setLink("https://cs1-19v4.vk-cdn.net/p34/673fafd883d27b.mp3?extra=rLRVZEw7sPVT-ADYAg5UUx7UYqIXa1kBtGpFgLtvv8A6TUAOYcVWRjAmI0OdX7jZh6lfG2Qlxn28vs2eAKl9KKW81CIVxeB8xSl9CfdVE5STE5W5PQKLRXAfN4FsPqQQWPecYdwvwXU5,157");
        mTracks.put(track.getLink(), track);*/
        List<Track> loadedTracks = mDatabase.getSavedTracks();
        for (Track track : loadedTracks) {
            mTracks.put(track.getLink(), track);
        }
    }

    @Override
    public void writeSavedTracks() {
        mDatabase.deleteAllTracks();
        for (Map.Entry<String, Track> pair : mTracks.entrySet()) {
            mDatabase.addTrack(pair.getValue(), SAVED_TRACK);
        }
    }

    @Override
    public List<Track> loadLastPlaylistTracks() {
        return mDatabase.getLastPlaylistTracks();
    }

    @Override
    public void writeLastPlaylistTracks(List<Track> tracks) {
        for (Track track: tracks) {
            mDatabase.addTrack(track, LAST_PLAYLIST_TRACKS);
        }
    }

    @Override
    public Track loadLastTrack() {
        return mDatabase.getLastPlayedTrack();
    }

    @Override
    public void writeLastTrack(Track track) {
        mDatabase.addTrack(track, LAST_TRACK);
    }
}
