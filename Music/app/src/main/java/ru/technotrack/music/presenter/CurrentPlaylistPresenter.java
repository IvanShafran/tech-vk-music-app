package ru.technotrack.music.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import ru.technotrack.music.model.IMusicStorage;
import ru.technotrack.music.model.StubMusicStorage;
import ru.technotrack.music.model.Track;
import ru.technotrack.music.model.TrackDB;
import ru.technotrack.music.music_playing.IMusicService;
import ru.technotrack.music.music_playing.MusicService;
import ru.technotrack.music.view.ICurrentPlaylistView;

public class CurrentPlaylistPresenter implements ICurrentPlaylistPresenter, MusicService.Callback {
    private static final String TAG = "PlaylistPresenter";

    private static CurrentPlaylistPresenter ourInstance = new CurrentPlaylistPresenter();
    private IMusicService mMusicService;
    private boolean mIsBoundService;
    private ICurrentPlaylistView mCurrentPlaylistView;
    private IMusicStorage mMusicStorage;
    private TrackDB mDatabase;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MusicService.MusicServiceBinder musicServiceBinder =
                    (MusicService.MusicServiceBinder) binder;
            mMusicService = musicServiceBinder.getMusicService();
            mMusicService.setCallback(CurrentPlaylistPresenter.this);
            mIsBoundService = true;

            if (mMusicService.getPlaylist() != null) {
                mTracks = mMusicService.getPlaylist();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBoundService = false;
        }
    };

    public static CurrentPlaylistPresenter getInstance() {
        return ourInstance;
    }

    private CurrentPlaylistPresenter() {
    }

    private ArrayList<Track> mTracks;

    @Override
    public void setPlaylist(ArrayList<Track> tracks) {
        mTracks = tracks;

        if (mIsBoundService) {
            mMusicService.setPlaylist(tracks);
        }
    }

    @Override
    public ArrayList<Track> getPlaylist() {
        return mTracks;
    }

    @Override
    public boolean isMusicPlaying() {
        if (mIsBoundService) {
            return mMusicService.isPlaying();
        } else {
            Log.e(TAG, "isMusicPlaying / not bound");
        }

        return false;
    }

    @Override
    public int getPlayingTrack() {
        if (mIsBoundService) {
            return mMusicService.getPlayingTrack();
        } else {
            Log.e(TAG, "getPlayingTrack / not bound");
        }

        return 0;
    }

    @Override
    public void setPlaylistView(ICurrentPlaylistView playlistView) {
        mCurrentPlaylistView = playlistView;
    }

    @Override
    public void onPlayPressed(int trackIndex) {
        if (mIsBoundService) {
            mMusicService.setTrackInPlaylist(trackIndex);
            mMusicService.play();
        }
    }

    @Override
    public void onPausePressed() {
        if (mIsBoundService) {
            mMusicService.pause();
        }
    }

    @Override
    public boolean isTrackAdded(Track track) {
        return mMusicStorage.isTrackAdded(track);
    }

    @Override
    public void onAddTrackPressed(Track track) {
        mMusicStorage.addTrack(track);
    }

    @Override
    public void onDeleteTrackPressed(Track track) {
        mMusicStorage.deleteTrack(track);
    }

    @Override
    public ArrayList<Track> getSavedTracks() {
        return mMusicStorage.getTracks();
    }

    @Override
    public void onCreate(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), MusicService.class);

        intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
        intent.setAction(MusicService.STARTFOREGROUND_ACTION);
        context.getApplicationContext()
                .startService(intent);

        context.getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mDatabase = new TrackDB(context);
        mMusicStorage = new StubMusicStorage(mDatabase);
        mMusicStorage.loadSavedTracks();
    }

    @Override
    public void onDestroy(Context context) {
        if (mIsBoundService) {
            context.getApplicationContext().unbindService(mConnection);
            mIsBoundService = false;
        }

        mMusicStorage.writeSavedTracks();
        mDatabase.close();
    }

    //Music service callback

    @Override
    public void onStartPlaying(int trackIndex) {
        if (mCurrentPlaylistView != null) {
            mCurrentPlaylistView.showAsPlaying(trackIndex);
        }
    }

    @Override
    public void onPausePlaying(int trackIndex) {
        if (mCurrentPlaylistView != null) {
            mCurrentPlaylistView.showAsNotPlaying(trackIndex);
        }
    }

    @Override
    public void onEndPlaying(int trackIndex) {
        if (mCurrentPlaylistView != null) {
            mCurrentPlaylistView.showAsNotPlaying(trackIndex);
        }
    }

    @Override
    public void onPlaylistEnd(ArrayList<Track> playlist) {

    }

    @Override
    public void onError(IMusicService.Error error) {
        if (mCurrentPlaylistView == null) {
            return;
        }

        switch (error) {
            case INTERNET_PROBLEM:
                mCurrentPlaylistView.showError("Some problem with internet");
                break;
        }
    }
}
