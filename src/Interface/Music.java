package Interface;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Music implements Serializable {
    private static final long serialVersionUID = 4L;
    private String title;
    private String artist;
    private String genre;
    private float duration;
    private CopyOnWriteArrayList<String> editors = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Integer> groups = new CopyOnWriteArrayList<>();


    public Music(String title, String artist, String genre, float duration) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
    }

    public Music(String title, String artist, String genre) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
    }

    public CopyOnWriteArrayList<Integer> getGroups() { return this.groups;  }

    public String getTitle() {
        return title;
    }

    public void add_editor(String editor){ this.editors.add(editor); }

    public void add_groups(int group) {
        if(!groups.contains(group))
            this.groups.add(group);
        else
            return;
    }

    public void setGroups(CopyOnWriteArrayList <Integer> groups){
        this.groups=groups;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() { return artist; }

    public void setArtist(String artist) {
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