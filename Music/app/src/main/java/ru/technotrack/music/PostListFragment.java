package ru.technotrack.music;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class PostListFragment extends Fragment {

    private final static String POST_ARGUMENT = "POST_ARGUMENT";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Post> mPosts;

    public static PostListFragment newInstance(ArrayList<Post> posts) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(POST_ARGUMENT, posts);

        PostListFragment fragment = new PostListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycle_list_layout, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        String mType;
        if (getContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {

            mType = ImageManager.BITMAP_POST_PICTURE_PORTRAIT;
        } else {
            mType = ImageManager.BITMAP_POST_PICTURE_LANDSCAPE;
        }

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y / 3;
        width -= Utils.getDevicePixels(getContext(), 30); //у cardview такой margin

        mAdapter = new PostRecycleViewAdapter(getPosts(), this.getContext(),
                mType,
                width, height);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();
        Post post = new Post();
        post.setText("asajaskjlasjkjaskjlkaskjl");
        post.setPictureLink("https://pp.vk.me/c543103/v543103715/13644/Xbeg-5apRVA.jpg");
        Track track = new Track();
        track.setArtist("Иван");
        track.setName("душевная");
        track.setLink("123");

        List<Track> tracks = new ArrayList<>();
        tracks.add(track);

        track = new Track();
        track.setName("душевная");
        track.setLink("123");
        track.setArtist("Ваня");
        tracks.add(track);

        track = new Track();
        track.setName("душевная");
        track.setLink("123");
        track.setArtist("Вано");
        tracks.add(track);

        post.setTracks(tracks);

        posts.add(post);
        posts.add(post);
        posts.add(post);

        return posts;
    }
}
