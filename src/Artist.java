import java.util.*;

public class Artist {
    private String name;
    private ArrayList<Album> albums;
    private Description description;
    private ArrayList<Music> musics;
    private ArrayList<Album> albuns;
    private ArrayList<String> concerts; //each concert should be "month/day/year - concert venue, city, country"
    private String genre;

    public Artist(String name, String genre) {
        albuns = null;
        description = null;
        musics = null;
        albuns  = null;
        concerts = null;
        this.genre = genre;
        this.name = name;
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

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
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

    public ArrayList<Album> getAlbuns() {
        return albuns;
    }

    public void setAlbuns(ArrayList<Album> albuns) {
        this.albuns = albuns;
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
