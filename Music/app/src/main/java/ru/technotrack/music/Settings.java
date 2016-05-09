package ru.technotrack.music;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    private static Settings ourInstance = new Settings();

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
    }

    private static final String REPEAT_PLAYLIST = "REPEAT_PLAYLIST";
    private static final String SETTING_FILENAME = "TechMusicVkAppSettings";

    private Context mContext;
    private boolean mIsRepeatPlaylist;

    public boolean isRepeatPlaylist() {
        return mIsRepeatPlaylist;
    }

    public void setIsRepeatPlaylist(boolean isRepeatPlaylist) {
        mIsRepeatPlaylist = isRepeatPlaylist;
    }

    void loadSettings(Context context) {
        mContext = context;

        SharedPreferences preferences
                = context.getSharedPreferences(SETTING_FILENAME, Context.MODE_PRIVATE);
        setIsRepeatPlaylist(preferences.getBoolean(REPEAT_PLAYLIST, false));
    }

    void writeSettings() {
        SharedPreferences.Editor editor
                = mContext.getSharedPreferences(SETTING_FILENAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(REPEAT_PLAYLIST, isRepeatPlaylist());
        editor.apply();
    }
}
