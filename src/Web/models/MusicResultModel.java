package Web.models;

public class MusicResultModel {
    private String name;
    private String artist;

    public MusicResultModel(String name, String artist){
        setName(name);
        setArtist(artist);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

}
