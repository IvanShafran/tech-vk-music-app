package ru.technotrack.music.model;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;

import java.util.UUID;

import ru.technotrack.music.server.IAPI;

public class Track implements Parcelable {
    private String artist;
    private String name;
    private String link;
    private ParcelUuid id;

    public static Track parse(IAPI.TrackJSON track) {
        Track parsed = new Track();
        Log.d("KEK", "Parsed: " + track.band + " " + track.name);
        parsed.artist = track.band;
        parsed.name = track.name;
        parsed.link = track.url;
        return parsed;
    }

    public Track() {
    }

    public Track(Parcel source) {
        artist = source.readString();
        name = source.readString();
        link = source.readString();
        id = source.readParcelable(ParcelUuid.class.getClassLoader());
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public UUID getId() {
        return id.getUuid();
    }

    public void setId(UUID id) {
        this.id = new ParcelUuid(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artist);
        dest.writeString(name);
        dest.writeString(link);
        dest.writeParcelable(id, flags);
    }

    public static final Parcelable.Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
