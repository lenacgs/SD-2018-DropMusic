package Interface;

import java.io.Serializable;
import java.util.*;

public class Artist implements Serializable {
    private String name;
    private Description description;
    private ArrayList<Music> musics;
    private ArrayList<Album> albums;
    private ArrayList<String> concerts; //each concert should be "concertVenue-city-country-year-month-day-hour"
    private String genre;
    private static final long serialVersionUID = 4L;

    public Artist(String name, String genre) {
        albums = new ArrayList<>();
        musics = new ArrayList<>();
        concerts = new ArrayList<>();
        this.genre = genre;
        this.name = name;
    }

    public Artist(String name, Description description, ArrayList<String> concerts, String genre) {
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

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<Music> musics) {
        this.musics = musics;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    public ArrayList<String> getConcerts() {
        return concerts;
    }

    public void setConcerts(ArrayList<String> concerts) {
        this.concerts = concerts;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}