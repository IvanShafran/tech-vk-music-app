package ru.technotrack.music;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;


public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ((Button) view.findViewById(R.id.settings_clear_cache_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        clearCache();
                    }
                }
        );

        Switch switchRepeatPlaylist =
                (Switch) view.findViewById(R.id.settings_switch_repeat_playlist);

        switchRepeatPlaylist.setChecked(Settings.getInstance().isRepeatPlaylist());

        (switchRepeatPlaylist)
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Settings.getInstance().setIsRepeatPlaylist(isChecked);
                    }
                });

        return view;
    }

    private void clearCache() {
        try {
            if (trimCache(getContext())) {
                Toast.makeText(getContext(), "Cache was cleared", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Cache wasn't cleared", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                return deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }

        return false;
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

}
