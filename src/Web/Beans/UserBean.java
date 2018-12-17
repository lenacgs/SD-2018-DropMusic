package Web.Beans;

import com.github.scribejava.core.model.Token;

import java.rmi.RemoteException;

public class UserBean extends RMIBean {
    private String username, password;
    private String accessToken, accountID;
    private int perks;

    public UserBean() {
        super();
    }

    public void saveAccountID() { //puts access token in the database
        try {
            server.saveAccountID(this.username, this.accountID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean share(String musicTitle, String artistName, String groups) {
        try {
            return server.shareMusic(this.username, groups, musicTitle, artistName);
        } catch(RemoteException exc){
            exc.printStackTrace();
        }
        return false;
    }

    public String[] getAccountIDs(String groups) {
        try {
            return server.getAccountIDs(groups, username);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean uploadDropbox(String username, String musicTitle, String artistName, String fileID) {
        try {
            return server.uploadDropbox(username, musicTitle, artistName, fileID);
        } catch (RemoteException exc) {
            exc.printStackTrace();
        }
        return false;
    }

    public String getFileID(String musicTitle, String artistName) {
        try {
            return server.getFileID(musicTitle, artistName, username);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    public String loadAccessToken() {
        try{
            return server.loadAccessToken(this.getUsername());
        } catch(RemoteException e){
            e.printStackTrace();
        }
        return null;
    }

    public void saveAccessToken() {
        try {
            server.saveAccessToken(username, accessToken);
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

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
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
