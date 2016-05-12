package ru.technotrack.music.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.technotrack.music.server.IAPI;

public class Post implements Parcelable {
    private String text;
    private String pictureLink;
    private List<Track> tracks;

    public static Post parse(IAPI.PostJSON post) {
        Post parsed = new Post();
        Log.d("KEK", "Parsed post: " + post.text);
        parsed.text = post.text;
        // TODO: Исправить временную аватарку
        parsed.pictureLink = "http://cs621731.vk.me/v621731163/1eb8/LUWqD8I5JJw.jpg";
        parsed.tracks = new ArrayList<>();
        for (IAPI.TrackJSON track : post.tracks) {
            parsed.tracks.add(Track.parse(track));
        }
        return parsed;
    }

    public Post() {
    }

    public Post(Parcel source) {
        text = source.readString();
        pictureLink = source.readString();
        source.readTypedList(tracks, Track.CREATOR);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(pictureLink);
        dest.writeTypedList(tracks);
    }

    public static final Parcelable.Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
