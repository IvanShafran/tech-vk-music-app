package ru.technotrack.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service
        implements IMusicService,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    private final static String TAG = "MusicService";

    private final IBinder mBinder = new MusicServiceBinder();

    private List<Track> mPlaylist;
    private int mPlayingTrack;
    private Callback mCallback;
    private MediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private boolean mIsTrackPrepared;

    public MusicService() {
    }

    public class MusicServiceBinder extends Binder {
        public IMusicService getMusicService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        releaseMediaPlayer();
        super.onDestroy();
    }

    private void createMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        mWifiLock.acquire();

        mIsTrackPrepared = false;
    }

    private void releaseMediaPlayer() {
        mMediaPlayer.release();
        mMediaPlayer = null;

        mWifiLock.release();
        mWifiLock = null;

        mIsTrackPrepared = false;
    }

    @Override
    public void setPlaylist(List<Track> playlist) {
        mPlayingTrack = 0;
        mPlaylist = playlist;
    }

    @Override
    public List<Track> getPlaylist() {
        return mPlaylist;
    }

    @Override
    public boolean gotoTrackInPlaylist(Track track) {
        if (mPlaylist == null) {
            return false;
        }

        for (int i = 0; i < mPlaylist.size(); ++i) {
            if (mPlaylist.get(i).getId().equals(track.getId())) {
                mPlayingTrack = i;
                mIsTrackPrepared = false;
                return true;
            }
        }

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsTrackPrepared = true;
        mp.start();
        if (mCallback != null) {
            mCallback.onStartPlaying(mPlaylist.get(mPlayingTrack));
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mCallback != null) {
            mCallback.onEndPlaying(mPlaylist.get(mPlayingTrack));
        }

        ++mPlayingTrack;
        mIsTrackPrepared = false;

        if (mPlayingTrack >= mPlaylist.size()) {
            if (mCallback != null) {
                mCallback.onPlaylistEnd(mPlaylist);
            }
        } else {
            startWithPreparingTrack();
        }
    }

    private void startWithPreparingTrack() {
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(mPlaylist.get(mPlayingTrack).getLink());
        } catch (IOException e) {
            if (mCallback != null) {
                mCallback.onError(Error.WRONG_LINK);
            }
        }
    }

    @Override
    public void play() {
        if (mMediaPlayer == null) {
            createMediaPlayer();
        }

        mWifiLock.acquire();

        if (mIsTrackPrepared) {
            mMediaPlayer.start();
            if (mCallback != null) {
                mCallback.onStartPlaying(mPlaylist.get(mPlayingTrack));
            }
        } else {
            startWithPreparingTrack();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {

            mWifiLock.release();
            mMediaPlayer.pause();

            if (mCallback != null) {
                mCallback.onPausePlaying(mPlaylist.get(mPlayingTrack));
            }
        } else {
            Log.e(TAG, "pause / null MediaPlayer");
        }
    }

    @Override
    public void stop() {
        releaseMediaPlayer();
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        } else {
            Log.e(TAG, "isPlaying / null MediaPlayer");
        }

        return false;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            Log.e(TAG, "getCurrentPosition / null MediaPlayer");
        }

        return 0;
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            Log.e(TAG, "getDuration / null MediaPlayer");
        }

        return 0;
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        } else {
            Log.e(TAG, "seekTo / null MediaPlayer");
        }
    }

    @Override
    public void startDuckMode() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(0.1f, 0.1f);
        } else {
            Log.e(TAG, "startDuckMode / null MediaPlayer");
        }
    }

    @Override
    public void finishDuckMode() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(1.0f, 1.0f);
        } else {
            Log.e(TAG, "finishDuckMode / null MediaPlayer");
        }
    }

    @Override
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

}
