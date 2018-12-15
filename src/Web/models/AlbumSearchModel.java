package Web.models;

public class AlbumSearchModel {
    private String album;
    private String artist;

    public AlbumSearchModel(String album, String artist){
        setAlbum(album);
        setArtist(artist);
    }

    public void setAlbum(String album){
        this.album = album;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public String getAlbum(){
        return this.album;
    }

    public String getArtist(){
        return this.artist;
    }
}
