package ru.technotrack.music;

import java.util.List;

public interface IMusicService {
    enum Error {
        INTERNET_PROBLEM, WRONG_LINK
    }

    interface Callback {
        //вызывается, когда песня начинает проигрываться
        void onStartPlaying(Track track);

        //когда песня приостанваливается
        void onPausePlaying(Track track);

        //когда песня закончилась
        void onEndPlaying(Track track);

        //вызывется, когда плейлист закончился
        void onPlaylistEnd(List<Track> playlist);

        //вызывается в случае ошибки
        void onError(Error error);
    }

    //Установить плейлист для воспроизведения.
    //Песни в плейлисте воспроизводятся по порядку
    void setPlaylist(List<Track> playlist);

    List<Track> getPlaylist();

    //перейти к определённому треку в плейлисте
    //true, если такой трек есть в плейлисте
    //!!! переключится только указатель на песню
    // сама она играть не начнёт
    boolean gotoTrackInPlaylist(Track track);

    //включить воспроизведение
    void play();

    //приостановаить воспроизведение
    void pause();

    //остановить воспроизведение
    //вызывать, когда долгое время не будет проигрываться музыка
    //например, когда теряется аудиофокус
    void stop();

    //всё в миллисекундах
    int getCurrentPosition();
    int getDuration();
    void seekTo(int position);

    boolean isPlaying();

    //приглушение, когда другие приложения этого просят
    void startDuckMode();
    void finishDuckMode();

    //установить callback для получения событий, которые можно/нужно обработать в GUI
    void setCallback(Callback callback);
}
