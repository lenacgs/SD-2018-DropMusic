package Interface;

import com.github.scribejava.core.model.Token;

import java.util.*;
import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class User implements Serializable{
    private static final long serialVersionUID = 4L;
    private String username;
    private String password;
    private int perks;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    private String accessToken;

    private CopyOnWriteArrayList<Group> defaultShareGroups; //lista de grupos a quem o utilizador partilha sempre por omissão
    private CopyOnWriteArrayList<Music> transferredMusics; //lista de musicas que o user transferiu para o servidor (e que pode enventualmente partilhar com outros userss/grupos)
    private CopyOnWriteArrayList<Notification> notifications;

    public User(String username, String password, int perks) {
        this.username = username;
        this.password = password;
        this.perks = perks;
        this.defaultShareGroups = new CopyOnWriteArrayList<>();
        //this.defaultShareGroups = public group;

        this.transferredMusics = new CopyOnWriteArrayList<>();
        this.notifications = new CopyOnWriteArrayList<>();
    }

    public void setTransferredMusics(CopyOnWriteArrayList<Music> transferredMusics) {
        this.transferredMusics = transferredMusics;
    }

    public CopyOnWriteArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(CopyOnWriteArrayList<Notification> notifications) {
        this.notifications = notifications;
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

    public CopyOnWriteArrayList<Group> getDefaultShareGroups() {
        return defaultShareGroups;
    }

    public CopyOnWriteArrayList<Music> getTransferredMusics() {
        return transferredMusics;
    }

    public void addToDefaultShareGroups(Group group) {
        this.defaultShareGroups.add(group);
    }

    public void addToTransferredMusics(Music music) {
        this.transferredMusics.add(music);
    }
}
