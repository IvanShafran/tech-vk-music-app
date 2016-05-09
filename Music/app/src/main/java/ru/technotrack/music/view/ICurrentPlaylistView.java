package ru.technotrack.music.view;

public interface ICurrentPlaylistView {
    void showAsPlaying(int trackIndex);
    void showAsNotPlaying(int trackIndex);
    void showError(String error);
}
