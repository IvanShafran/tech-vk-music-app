package ru.technotrack.music.view;

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

import ru.technotrack.music.R;
import ru.technotrack.music.model.Post;
import ru.technotrack.music.model.Track;
import ru.technotrack.music.net.ImageManager;

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
        //почему бы и не на треть экрана
        int height = size.y / 3;
        //отступ у картинки от краёв
        width -= 2 * getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);

        mAdapter = new PostRecycleViewAdapter(getPosts(), this.getContext(),
                mType,
                width, height);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();

        Post post = new Post();
        post.setText("Немного расслабляющей музыки перед завтрашним матаном. На на на на");
        post.setPictureLink("https://pp.vk.me/c543103/v543103503/dd29/zAf3dFezoqs.jpg");
        Track track = new Track();
        track.setArtist("William Fitzsimmons");
        track.setName("I Kissed A Girl");
        track.setLink("https://cs1-37v4.vk-cdn.net/p22/bd953fabf94e29.mp3?extra=j2KleBWmeZZoo4nYFZPBzs4bE2p_JjZKPSOqSuINK9vpXMPSgeFJ-zi9jrZzAb4SjGlKRA4H6qCaIb0M_JhcOt5wreLgDHcrKcq_-CGcWI3aM_J4rVAP_noApPXHcrb9Mi8tKcZf12Eb,192");

        List<Track> tracks = new ArrayList<>();
        tracks.add(track);

        track = new Track();
        track.setName("Kill The Humans");
        track.setLink("https://psv4.vk.me/c5009/u5698326/audios/b19613ab9b11.mp3?extra=S7o7cHCdzzofj7hnQGKFCGZfoQNNTQdEJSwpPQ81ngs3fmSHyMFXLKzS9WOyMynhjmQHHVCCZ8BnLtEE2N5-573hqtfLw10jx4TjeXAys0hkhkmcTxDMEoqgdh-Djvmu2kV94Bsoh9On,239");
        track.setArtist("Hypnogaja");
        tracks.add(track);

        post.setTracks(tracks);

        posts.add(post);

        return posts;
    }
}
