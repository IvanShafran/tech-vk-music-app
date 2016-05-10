package ru.technotrack.music.music_playing;

import android.app.Notification;
import android.app.PendingIntent;
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
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import ru.technotrack.music.MainActivity;
import ru.technotrack.music.R;
import ru.technotrack.music.Settings;
import ru.technotrack.music.model.Track;

public class MusicService extends Service
        implements IMusicService,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private final IBinder mBinder = new MusicServiceBinder();

    private ArrayList<Track> mPlaylist;
    private int mPlayingTrack;
    private Callback mCallback;
    private MediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private boolean mIsTrackPrepared;

    private int mAudioFocusState;
    private boolean mIsMusicPlaying;

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

    private final static String TAG = "MUSIC_SERVICE";
    public final static String STARTFOREGROUND_ACTION = "STARTFOREGROUND_ACTION";
    public final static String STOPFOREGROUND_ACTION = "STOPFOREGROUND_ACTION";
    public final static String PLAY_ACTION = "PLAY_ACTION";
    public final static String PREV_ACTION = "PREV_ACTION";
    public final static String NEXT_ACTION = "NEXT_ACTION";
    public final static String MAIN_ACTION = "MAIN_ACTION";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

            String action = intent.getAction();

            switch (action) {
                case STARTFOREGROUND_ACTION:
                    Log.d(TAG, STARTFOREGROUND_ACTION);
                    showNotification();
                    break;
                case STOPFOREGROUND_ACTION:
                    Log.d(TAG, STOPFOREGROUND_ACTION);
                    stopForeground(true);
                    stopSelf();
                    break;
                case PLAY_ACTION:
                    Log.d(TAG, PLAY_ACTION);

                    if (mPlaylist == null) {
                        Toast.makeText(this,
                                "You didn't choose any playlist", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (mIsMusicPlaying) {
                        pause();
                    } else {
                        play();
                    }
                    break;
                case PREV_ACTION:
                    Log.d(TAG, PREV_ACTION);
                    if (mPlaylist == null) {
                        Toast.makeText(this,
                                "You didn't choose any playlist", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (mPlayingTrack == 0 && !Settings.getInstance().isRepeatPlaylist()) {
                        Toast.makeText(this, "This is first track in playlist!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    setPrevTrack();
                    break;
                case NEXT_ACTION:
                    Log.d(TAG, NEXT_ACTION);
                    if (mPlaylist == null) {
                        Toast.makeText(this,
                                "You didn't choose any playlist", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (mPlayingTrack == mPlaylist.size() - 1
                            && !Settings.getInstance().isRepeatPlaylist()) {
                        Toast.makeText(this, "This is last track in playlist!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    setNextTrack();
                    break;
            }
        }

        updateNotificationViewsState();

        return START_STICKY;
    }

    private void updateNotificationViewsState() {
        if (mIsMusicPlaying) {
            mViews.setImageViewResource(R.id.status_bar_play,
                    R.drawable.ic_pause_black_48dp);
            mBigViews.setImageViewResource(R.id.status_bar_play,
                    R.drawable.ic_pause_black_48dp);
        } else {
            mViews.setImageViewResource(R.id.status_bar_play,
                    R.drawable.ic_play_arrow_black_48dp);
            mBigViews.setImageViewResource(R.id.status_bar_play,
                    R.drawable.ic_play_arrow_black_48dp);
        }

        if (mPlaylist != null) {
            Track track = mPlaylist.get(mPlayingTrack);
            mViews.setTextViewText(R.id.status_bar_track_name, track.getName());
            mBigViews.setTextViewText(R.id.status_bar_track_name, track.getName());

            mViews.setTextViewText(R.id.status_bar_artist_name, track.getArtist());
            mBigViews.setTextViewText(R.id.status_bar_artist_name, track.getArtist());
        }

        status.bigContentView = mBigViews;
        status.contentView = mViews;
        startForeground(123, status);
    }

    Notification status;
    private final String LOG_TAG = "NotificationService";
    private RemoteViews mViews;
    private RemoteViews mBigViews;

    private void showNotification() {
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        mViews = views;
        RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);
        mBigViews = bigViews;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, MusicService.class);
        previousIntent.setAction(PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction(PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, MusicService.class);
        closeIntent.setAction(STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        views.setTextViewText(R.id.status_bar_track_name, "Song Title");
        bigViews.setTextViewText(R.id.status_bar_track_name, "Song Title");

        views.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_artist_name, "Artist Name");

        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.ic_music_note_black_48dp;
        status.contentIntent = pendingIntent;

        updateNotificationViewsState();
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
                        if (mIsMusicPlaying) {
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
    public void setPlaylist(ArrayList<Track> playlist) {
        mPlaylist = playlist;
        mIsTrackPrepared = false;
    }

    @Override
    public ArrayList<Track> getPlaylist() {
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
    public void setNextTrack() {
        if (mCallback != null) {
            mCallback.onPausePlaying(mPlayingTrack);
        }

        ++mPlayingTrack;
        mIsTrackPrepared = false;
        if (mPlayingTrack == mPlaylist.size()) {
            if (Settings.getInstance().isRepeatPlaylist()) {
                mPlayingTrack = 0;
            } else {
                mPlayingTrack = mPlaylist.size() - 1;
            }
        }

        if (mIsMusicPlaying) {
            play();
        }
    }

    @Override
    public void setPrevTrack() {
        if (mCallback != null) {
            mCallback.onPausePlaying(mPlayingTrack);
        }

        --mPlayingTrack;
        mIsTrackPrepared = false;
        if (mPlayingTrack == -1) {
            if (Settings.getInstance().isRepeatPlaylist()) {
                mPlayingTrack = mPlaylist.size() - 1;
            } else {
                mPlayingTrack = 0;
            }
        }

        if (mIsMusicPlaying) {
            play();
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
        mIsMusicPlaying = false;

        if (mPlayingTrack >= mPlaylist.size()) {
            if (mCallback != null) {
                mCallback.onPlaylistEnd(mPlaylist);

                if (Settings.getInstance().isRepeatPlaylist()) {
                    mPlayingTrack = 0;
                    startWithPreparingTrack();
                }
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
        mIsMusicPlaying = true;

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

        updateNotificationViewsState();
    }

    @Override
    public void pause() {
        mIsMusicPlaying = false;

        if (mMediaPlayer != null) {

            mWifiLock.release();
            mMediaPlayer.pause();

            if (mCallback != null) {
                mCallback.onPausePlaying(mPlayingTrack);
            }
        } else {
            Log.e(TAG, "pause / null MediaPlayer");
        }

        updateNotificationViewsState();
    }

    @Override
    public void stop() {
        if (mCallback != null && mIsMusicPlaying) {
            mCallback.onEndPlaying(mPlayingTrack);
        }

        mIsMusicPlaying = false;

        releaseMediaPlayer();
        updateNotificationViewsState();
    }

    @Override
    public boolean isPlaying() {
        return mIsMusicPlaying;
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
