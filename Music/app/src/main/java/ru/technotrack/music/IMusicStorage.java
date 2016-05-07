package ru.technotrack.music;

import java.util.Map;
import java.util.UUID;

public interface IMusicStorage {
    UUID putTrack(Track track);
    boolean deleteTrack(UUID id);
    Map<UUID, Track> getTracks();
}
