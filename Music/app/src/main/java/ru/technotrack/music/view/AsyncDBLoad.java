package ru.technotrack.music.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import ru.technotrack.music.MainActivity;
import ru.technotrack.music.model.LoadedData;
import ru.technotrack.music.model.TrackDB;

/**
 * Created by Kirill on 13.05.2016.
 */
public class AsyncDBLoad extends AsyncTask<Void, Void, LoadedData> {
    private StartActivity mParentActivity;

    public AsyncDBLoad(StartActivity parentActivity) {
        this.mParentActivity = parentActivity;
    }

    @Override
    protected LoadedData doInBackground(Void... params) {
        TrackDB dataBase = new TrackDB(mParentActivity);
        return new LoadedData(dataBase.getSavedTracks(),
                dataBase.getLastPlaylistTracks(), dataBase.getLastPlayedTrack());
    }

    @Override
    protected void onPostExecute(LoadedData data) {
        super.onPostExecute(data);
        Intent intent = new Intent(mParentActivity.getApplicationContext(), MainActivity.class);
        intent.putParcelableArrayListExtra(MainActivity.SAVED_TRACKS,
                new ArrayList<>(data.getSavedTracks()));
        intent.putParcelableArrayListExtra(MainActivity.LAST_PLAYLIST_TRACKS,
                new ArrayList<>(data.getLastPlaylistTracks()));
        intent.putExtra(MainActivity.LAST_TRACK, data.getLastTrack());
        mParentActivity.startActivity(intent);
    }
}
