package Web.Beans;

import com.github.scribejava.core.model.Token;

import java.rmi.RemoteException;

public class UserBean extends RMIBean {
    private String username, password;
    private String accessToken;
    private int perks;

    public UserBean() {
        super();
    }

    public void saveToken() { //puts access token in the database
        try {
            server.saveToken(this.username, this.accessToken);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean addMusic(String username, String groups, String type, String title, String artist, String genre, String duration) {
        String ans = null;
        try {
            ans = server.addInfo(username, groups, type, title, artist, genre, duration);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (ans.equals("New song successfully added!")) return true;
        return false;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getPerks() {
        return perks;
    }

    public void setPerks(int perks) {
        this.perks = perks;
    }
}
