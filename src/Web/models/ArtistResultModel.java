package Web.models;

public class ArtistResultModel {
    private String name;

    public ArtistResultModel(String name){
        setName(name);
    }

    public void setName(String name) {
        name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
