package Web.models;

public class MusicSearchModel {
    public String music;
    public String artist;

    public MusicSearchModel(String artist, String music){
        setArtist(artist);
        setMusic(music);
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getArtist() {
        return artist;
    }

    public String getMusic() {
        return music;
    }
}
