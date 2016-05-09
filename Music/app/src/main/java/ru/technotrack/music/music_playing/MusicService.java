package ru.technotrack.music.music_playing;

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

import ru.technotrack.music.model.Track;

public class MusicService extends Service
        implements IMusicService,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private final static String TAG = "MusicService";

    private final IBinder mBinder = new MusicServiceBinder();

    private List<Track> mPlaylist;
    private int mPlayingTrack;
    private Callback mCallback;
    private MediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private boolean mIsTrackPrepared;

    private int mAudioFocusState;
    private boolean mIsMusicWasPlaying;

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
    public void onCreate() {
        super.onCreate();

        requestAudioFocus();
    }

    private void requestAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e(TAG, "could not get audio focus");
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                switch (mAudioFocusState) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        if (mIsMusicWasPlaying) {
                            play();
                        }
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        finishDuckMode();
                        break;
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                stop();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mIsMusicWasPlaying = isPlaying();
                pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                startDuckMode();
                break;
        }

        mAudioFocusState = focusChange;
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
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = null;

        if (mWifiLock != null) {
            mWifiLock.release();
        }
        mWifiLock = null;

        mIsTrackPrepared = false;
    }

    @Override
    public void setPlaylist(List<Track> playlist) {
        mPlaylist = playlist;
    }

    @Override
    public List<Track> getPlaylist() {
        return mPlaylist;
    }

    @Override
    public boolean setTrackInPlaylist(int trackIndex) {
        if (mPlaylist == null || trackIndex < 0 || trackIndex >= mPlaylist.size()) {
            return false;
        }

        if (mIsTrackPrepared && mPlayingTrack == trackIndex) {
            return true;
        } else {
            if (mCallback != null) {
                mCallback.onPausePlaying(mPlayingTrack);
            }
            mPlayingTrack = trackIndex;
            mIsTrackPrepared = false;
            return true;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsTrackPrepared = true;
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mCallback != null) {
            mCallback.onEndPlaying(mPlayingTrack);
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
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepareAsync();

            if (mCallback != null) {
                mCallback.onStartPlaying(mPlayingTrack);
            }
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

        if (mAudioFocusState != AudioManager.AUDIOFOCUS_GAIN) {
            requestAudioFocus();
        }

        if (mIsTrackPrepared) {
            mMediaPlayer.start();
            if (mCallback != null) {
                mCallback.onStartPlaying(mPlayingTrack);
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
                mCallback.onPausePlaying(mPlayingTrack);
            }
        } else {
            Log.e(TAG, "pause / null MediaPlayer");
        }
    }

    @Override
    public void stop() {
        if (mCallback != null && isPlaying()) {
            mCallback.onEndPlaying(mPlayingTrack);
        }
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
    public int getPlayingTrack() {
        return mPlayingTrack;
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
