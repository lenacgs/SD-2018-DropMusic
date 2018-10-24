package Interface;

import java.util.*;
import java.io.*;

public class User implements Serializable{
    private static final long serialVersionUID = 4L;
    private String username;
    private String password;
    private int perks;
    private ArrayList<Group> defaultShareGroups; //lista de grupos a quem o utilizador partilha sempre por omissão
    private ArrayList<Music> transferredMusics; //lista de musicas que o user transferiu para o servidor (e que pode enventualmente partilhar com outros userss/grupos)

    public User(String username, String password, int perks) {
        this.username = username;
        this.password = password;
        this.perks = perks;
        this.defaultShareGroups = new ArrayList<>();
        //this.defaultShareGroups = public group;
        this.transferredMusics = new ArrayList<>();
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

    public int getPerks() { return perks;}

    public void setPerks(int perks) { this.perks = perks; }

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
