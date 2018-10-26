package Interface;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Music implements Serializable {
    private static final long serialVersionUID = 4L;
    private String title;
    private Artist artist;
    private String genre;
    private float duration;
    private CopyOnWriteArrayList<String> editors = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Integer> groups = new CopyOnWriteArrayList<>();
    private String pathToFile; //é usado apenas quando alguém associa um ficheiro a esta música
    private int[] accessToDownload; //guarda os groupIDs dos grupos que têm acesso ao download
    private User whoShared; //user que deu upload do ficheiro, caso aplicável


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

    public User getWhoShared() {
        return whoShared;
    }

    public void setWhoShared(User whoShared) {
        this.whoShared = whoShared;
    }

    public String getPathToFile() {

        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public int[] getAccessToDownload() {
        return accessToDownload;
    }

    public void setAccessToDownload(int[] accessToDownload) {
        this.accessToDownload = accessToDownload;
    }

    public CopyOnWriteArrayList<Integer> getGroups() { return this.groups;  }

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