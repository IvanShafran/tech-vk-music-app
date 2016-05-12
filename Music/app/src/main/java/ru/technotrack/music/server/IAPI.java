package ru.technotrack.music.server;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Интерфейс описания запросов к серверу.
 */
public interface IAPI {
    class PostJSON {
        public String text;
        public String time;
        public TrackJSON[] tracks;
        public int likes;
        public PublicJSON vk_public;
    }

    class TrackJSON {
        public String name;
        public String band;
        public String url;
    }

    class PublicJSON {
        public String name;
        public String avatar;
    }

    @GET("post/")
    //Observable<PostJSON[]> getPosts(@Path("band") String band, @Path("author") String author);
    Observable<PostJSON[]> getPosts();
}
