import java.util.*;

public class User {
    private String username;
    private String password;
    private ArrayList<Group> defaultShareGroups; //lista de grupos a quem o utilizador partilha sempre por omissão
    private ArrayList<Music> transferredMusics; //lista de musicas que o user transferiu para o servidor (e que pode enventualmente partilhar com outros userss/grupos)

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        //this.defaultShareGroups = public group;
        this.transferredMusics = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Group> getDefaultShareGroups() {
        return defaultShareGroups;
    }

    public ArrayList<Music> getTransferredMusics() {
        return transferredMusics;
    }

    public void addToDefaultShareGroups(Group group) {
        this.defaultShareGroups.add(group);
    }

    public void addToTransferredMusics(Music music) {
        this.transferredMusics.add(music);
    }
}
