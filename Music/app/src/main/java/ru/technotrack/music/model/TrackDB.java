package ru.technotrack.music.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill on 12.05.2016.
 */
public class TrackDB extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "track_table";
    private List<Track> mSavedTracks = new ArrayList<>();
    private List<Track> mLastPlaylistTracks = new ArrayList<>();
    private Track mLastPlayedTrack;
    private boolean isTracksLoaded = false;

    public TrackDB(Context context) {
        //super(context, name, factory, version);
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE IF NOT EXISTS TABLE " + TABLE_NAME + " ("
                + "id integer PRIMARY KEY AUTOINCREMENT,"
                + "artist text,"
                + "name text,"
                + "url text,"
                + "track_type integer" + ");"); //0 - saved, 1 - current post,
                                                // 2 - current audio in post
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Следующие методы вызывать только после loadSavedTracks()
    // Возвратить сохранённые пользователем треки
    public List<Track> getSavedTracks() {
        if (!isTracksLoaded) {
            loadTracks();
        }
        return mSavedTracks;
    }

    // Возвратить все треки с поста последнего прослушенного трека
    public List<Track> getLastPlaylistTracks() {
        if (!isTracksLoaded) {
            loadTracks();
        }
        return mLastPlaylistTracks;
    }

    // Возвратить последний прослушанный трек
    public Track getLastPlayedTrack() {
        if (!isTracksLoaded) {
            loadTracks();
        }
        return mLastPlayedTrack;
    }

    private void loadTracks() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int artistColIndex = cursor.getColumnIndex("artist");
            int nameColIndex = cursor.getColumnIndex("name");
            int urlColIndex = cursor.getColumnIndex("url");
            int typeColIndex = cursor.getColumnIndex("track_type");

            do {
                Track track = new Track();
                track.setArtist(cursor.getString(artistColIndex));
                track.setName(cursor.getString(nameColIndex));
                track.setLink(cursor.getString(urlColIndex));
                int trackType = cursor.getInt(typeColIndex);
                if (trackType == 0) {
                    mSavedTracks.add(track);
                } else if (trackType == 1) {
                    mLastPlaylistTracks.add(track);
                } else if (trackType == 2) {
                    mLastPlayedTrack = track;
                }
            } while (cursor.moveToNext());
        } else {
            Log.d("DB_LOAD", "Loaded 0 rows");
        }
        cursor.close();
        isTracksLoaded = true;
    }

    public void addTrack(Track track, int type) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("artist", track.getArtist());
        cv.put("name", track.getName());
        cv.put("url", track.getLink());
        cv.put("track_type", type);
        long rowId = db.insert(TABLE_NAME, null, cv);
        Log.d("DB_INSERT", "Track inserted with ID=" + rowId);
    }

    public void deleteAllTracks() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
}
