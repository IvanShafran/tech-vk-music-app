package ru.technotrack.music;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StubMusicStorage implements IMusicStorage {
    private static Map<UUID, Track> sTracks = new HashMap<>();

    @Override
    public UUID putTrack(Track track) {
        track.setId(UUID.randomUUID());
        sTracks.put(track.getId(), track);
        return track.getId();
    }

    @Override
    public boolean deleteTrack(UUID id) {
        if (sTracks.containsKey(id)) {
            sTracks.remove(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<UUID, Track> getTracks() {
        return sTracks;
    }
}
