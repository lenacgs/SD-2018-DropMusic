package Interface;

import java.io.Serializable;
import java.util.*;

public class Music implements Serializable {
    private static final long serialVersionUID = 4L;
    private String title;
    private Artist artist;
    private String genre;
    private float duration;
    private ArrayList<String> editors = new ArrayList<>();
    private ArrayList<Integer> groups = new ArrayList<>();


    public Music(String title, Artist artist, String genre, float duration) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
    }

    public Music(String title, Artist artist, String genre) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void add_editor(String editor){ this.editors.add(editor); }

    public void add_groups(int group) { this.groups.add(group);}

    public void setTitle(String title) {
        this.title = title;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }
}