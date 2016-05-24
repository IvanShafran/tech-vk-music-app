package ru.technotrack.music.view;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.technotrack.music.R;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        AsyncDBLoad loader = new AsyncDBLoad(this);
        loader.execute();

        /*Handler handler = new Handler();
        Runnable runnable = () -> isWaited = true;
        handler.postDelayed(runnable, 2000);*/
    }
}
