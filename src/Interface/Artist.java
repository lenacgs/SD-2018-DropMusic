package Interface;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Artist implements Serializable {
    private String name;
    private Description description;
    private CopyOnWriteArrayList<Music> musics;
    private CopyOnWriteArrayList<Album> albums;
    private CopyOnWriteArrayList<String> concerts; //each concert should be "concertVenue-city-country-year-month-day-hour"
    private String genre;
    private static final long serialVersionUID = 4L;

    public Artist(String name, String genre) {
        albums = new CopyOnWriteArrayList<>();
        musics = new CopyOnWriteArrayList<>();
        concerts = new CopyOnWriteArrayList<>();
        this.genre = genre;
        this.name = name;
    }

    public Artist(String name, Description description, CopyOnWriteArrayList<String> concerts, String genre) {
        this.name = name;
        this.description = description;
        this.concerts = concerts;
        this.genre = genre;
    }

    public void addMusic(Music m) {
        this.musics.add(m);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CopyOnWriteArrayList<Album> getAlbums() {
        return albums;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public CopyOnWriteArrayList<Music> getMusics() {
        return musics;
    }

    public void setMusics(CopyOnWriteArrayList<Music> musics) {
        this.musics = musics;
    }

    public void setAlbums(CopyOnWriteArrayList<Album> albums) {
        this.albums = albums;
    }

    public CopyOnWriteArrayList<String> getConcerts() {
        return concerts;
    }

    public void setConcerts(CopyOnWriteArrayList<String> concerts) {
        this.concerts = concerts;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean checkIfContains(String keyword){
        for(Music m : musics){
            if(m.getTitle().contains(keyword))
                return true;
        }
        for(Album a : albums){
            if(a.getTitle().contains(keyword))
                return true;
        }
        return false;
    }
}