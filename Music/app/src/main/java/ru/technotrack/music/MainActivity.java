package ru.technotrack.music;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AudioManager.OnAudioFocusChangeListener {

    private IMusicService mMusicService;

    enum AudioFocusState {
        GAIN, LOSS, LOSS_TRANSIENT, LOSS_TRANSIENT_DUCK
    }
    private AudioFocusState mAudioFocusState;
    private boolean mIsMusicWasPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_player:
                break;
            case R.id.nav_search:
                break;
            case R.id.nav_settings:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                switch (mAudioFocusState) {
                    case LOSS:
                        break;

                    case LOSS_TRANSIENT:
                        if (mIsMusicWasPlaying) {
                            mMusicService.play();
                        }
                        break;

                    case LOSS_TRANSIENT_DUCK:
                        mMusicService.finishDuckMode();
                        break;
                }
                mAudioFocusState = AudioFocusState.GAIN;
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                mMusicService.stop();
                mAudioFocusState = AudioFocusState.LOSS;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mIsMusicWasPlaying = mMusicService.isPlaying();
                mMusicService.pause();
                mAudioFocusState = AudioFocusState.LOSS_TRANSIENT;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mMusicService.startDuckMode();
                mAudioFocusState = AudioFocusState.LOSS_TRANSIENT_DUCK;
                break;
        }
    }
}
