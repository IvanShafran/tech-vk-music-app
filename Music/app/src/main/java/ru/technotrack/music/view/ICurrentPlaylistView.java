package ru.technotrack.music.view;

import java.util.ArrayList;

import ru.technotrack.music.model.Post;

public interface ICurrentPlaylistView {
    void showAsPlaying(int trackIndex);
    void showAsNotPlaying(int trackIndex);
    void showError(String error);
}
