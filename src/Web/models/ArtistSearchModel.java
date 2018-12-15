package Web.models;

public class ArtistSearchModel {
    private String artist;

    public ArtistSearchModel(){
        setArtist(artist);
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public String getArtist(){
        return this.artist;
    }
}
