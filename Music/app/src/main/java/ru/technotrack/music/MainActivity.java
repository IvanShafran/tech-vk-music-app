package ru.technotrack.music;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ru.technotrack.music.model.Track;
import ru.technotrack.music.presenter.CurrentPlaylistPresenter;
import ru.technotrack.music.view.PlayListFragment;
import ru.technotrack.music.view.SearchFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String SAVED_TRACKS = "SavedTracks";
    public static final String LAST_PLAYLIST_TRACKS = "LastPlayListTracks";
    public static final String LAST_TRACK = "LastTrack";

    private List<Track> mSavedTracks;
    private List<Track> mLastPlaylistTracks;
    private Track mLastTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mSavedTracks = intent.getParcelableArrayListExtra(SAVED_TRACKS);
        mLastPlaylistTracks = intent.getParcelableArrayListExtra(LAST_PLAYLIST_TRACKS);
        mLastTrack = intent.getParcelableExtra(LAST_TRACK);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CurrentPlaylistPresenter.getInstance().onCreate(this);
        Settings.getInstance().loadSettings(this);

        navigationView.setCheckedItem(R.id.nav_saved_tracks);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, PlayListFragment
                        .newInstance(CurrentPlaylistPresenter
                                .getInstance().getSavedTracks()))
                .commit();
        getSupportActionBar().setTitle(R.string.nav_saved_music);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_current_playlist:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, PlayListFragment
                                .newInstance(CurrentPlaylistPresenter.
                                        getInstance().getPlaylist()))
                        .commit();

                getSupportActionBar().setTitle(R.string.nav_current_playlist);
                break;
            case R.id.nav_saved_tracks:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, PlayListFragment
                                .newInstance(CurrentPlaylistPresenter
                                        .getInstance().getSavedTracks()))
                        .commit();
                getSupportActionBar().setTitle(R.string.nav_saved_music);
                break;
            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,
                        new SearchFragment()).commit();
                getSupportActionBar().setTitle(R.string.nav_search);
                break;
            case R.id.nav_settings:
                getSupportActionBar().setTitle(R.string.nav_settings);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,
                        new SettingsFragment()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Settings.getInstance().writeSettings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CurrentPlaylistPresenter.getInstance().onDestroy(this);
        Settings.getInstance().writeSettings();
    }
}
