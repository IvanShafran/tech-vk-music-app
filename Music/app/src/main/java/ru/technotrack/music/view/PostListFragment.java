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
    private PostRecycleViewAdapter mAdapter;
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

        Bundle args = getArguments();
        mPosts = new ArrayList<>();
        if (args != null) {
            mPosts = (ArrayList<Post>) args.get(POST_ARGUMENT);
        }

        mAdapter = new PostRecycleViewAdapter(mPosts, this.getContext(),
                mType,
                width, height);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void updatePosts(List<Post> posts) {
        mAdapter.setPosts(posts);
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

        post = new Post();
        post.setText("System Of A Down \n" +
                "\n" +
                "Американская рок-группа, образованная в 1992 году в Лос-Анджелесе Сержем Танкяном и Дароном Малакяном под названием Soil, а в 1995 принявшая нынешнее название. Все участники группы имеют армянское происхождение. В период с 1998 по 2005 год группа выпустила пять студийных альбомов, каждый из которых стал платиновым (наиболее успешный — мультиплатиновый Toxicity), общим тиражом свыше 12 миллионов экземпляров. В 2006 году участники System of a Down решили временно приостановить совместную деятельность и заняться сольными проектами. 29 ноября 2010 года группа объявила о воссоединении и проведении европейского турне в 2011 году. Изначально группа должна была называться «Victims of the Down» — по стихотворению, написанному Дароном Малакяном. При совместном обсуждении участниками было решено заменить слово «victims» на более общее «system». Причиной замены также послужило желание Шаво Одаджяна расположить группу ближе к Slayer на полках музыкальных магазинов.");
        post.setPictureLink("https://pp.vk.me/c7001/v7001287/16fd7/PAangtN-sdY.jpg");
        track = new Track();
        track.setArtist("System of a down");
        track.setName("Lonely Day");
        track.setLink("https://psv4.vk.me/c4962/u18432735/audios/73675007d380.mp3?extra=uLKUHfI9e4V0EB-yvz-lltZkuZthcxX-2ayEvUuAx_F_Hb_DjRvVHte_UZKyYckIMFqPuj7J0-c44m_KMX4oG158gq4B-6_GUehWvsvzmwO0aaz1-WB4Yx1tsqS4-qcDo-vt5Mww0DIn,167");

        tracks = new ArrayList<>();
        tracks.add(track);

        track = new Track();
        track.setName("Aerials");
        track.setLink("https://cs1-29v4.vk-cdn.net/p17/54e140f8ef0cb3.mp3?extra=Rw_sLjZismD5orbiCd6jciOfPHM3bZkgZwlo_dnxPW8ArTxDySiTDvO3Pa_bxDGzrxLZPjW4k2eGDOLhzRhVF7tU-X0ReMWoyWMwJFGVCI4evKrDdbRAIEExoAQ-bq3syKG3GMdgWYyJ,233");
        track.setArtist("System of a down");
        tracks.add(track);

        post.setTracks(tracks);

        posts.add(post);

        return posts;
    }
}
