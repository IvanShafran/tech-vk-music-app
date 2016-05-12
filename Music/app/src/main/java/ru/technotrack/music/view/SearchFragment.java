package ru.technotrack.music.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.adapter.rxjava.HttpException;
import ru.technotrack.music.R;
import ru.technotrack.music.model.Post;
import ru.technotrack.music.server.ConnectionService;
import ru.technotrack.music.server.IAPI;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchFragment extends Fragment {
    private EditText mInputAuthor;
    private EditText mInputName;
    private Button mSubmitButton;
    private IAPI mService;

    public SearchFragment() {
        mService = ConnectionService.provideService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mInputAuthor = (EditText) view.findViewById(R.id.input_author);
        mInputName = (EditText) view.findViewById(R.id.input_name);
        mSubmitButton = (Button) view.findViewById(R.id.submit_button);


        mSubmitButton.setOnClickListener((v) -> {
            PostListFragment fragment = new PostListFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,
                    fragment).commit();
            Observable<IAPI.PostJSON[]> observable = mService.getPosts();
                    //mService.getPosts(mInputName.getText().toString(),
                    //        mInputAuthor.getText().toString());
            observable.map(parsePosts)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((newPosts) -> {
                        fragment.hideProgress();
                        fragment.updatePosts(Arrays.asList(newPosts));
                    }, mOnError)    ;
            //getActivity().getSupportActionBar().setTitle(R.string.nav_search);
        });
        return view;
    }

    private Func1<IAPI.PostJSON[], Post[]> parsePosts = posts -> {
        Post[] parsed = new Post[posts.length];
        for (int i = 0; i < posts.length; i++) {
            parsed[i] = (Post.parse(posts[i]));
        }
        return parsed;
    };

    private Action1<Throwable> mOnError = (exception) -> {
        if (exception instanceof HttpException) {
            try {
                String encodedMessage = new String(((HttpException) exception).response().errorBody().bytes());
                String[] message = encodedMessage.split("\"");
                if (message.length >= 4) {
                    String toastMessage = message[1] + ": " + message[3];
                    Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), encodedMessage, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


}
