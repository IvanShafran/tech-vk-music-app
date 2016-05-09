package ru.technotrack.music.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.technotrack.music.R;
import ru.technotrack.music.model.Track;

public class PlayListFragment extends Fragment {

    private final static String TRACKS_ARGUMENT = "TRACKS_ARGUMENT";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Track> mTracks;

    public static PlayListFragment newInstance(ArrayList<Track> tracks) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(TRACKS_ARGUMENT, tracks);

        PlayListFragment fragment = new PlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTracks = getArguments().getParcelableArrayList(TRACKS_ARGUMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycle_list_layout, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PlayListRecycleViewAdapter(this.getContext(), mTracks);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}
