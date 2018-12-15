package Web.Actions;

import Web.Beans.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class AddMusicAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private String groups, title, artist, genre, duration;
    public String execute() {
        if (this.getUserBean().addMusic(this.getUserBean().getUsername(), groups, "music", title, artist, genre, duration)){
            this.session.put("message", "Music successfully added :)");
            return SUCCESS;
        }

        this.session.put("message", "Could not add music :(");
        return "fail";
    }

    public UserBean getUserBean() {
        if (!session.containsKey("userBean")) {
            this.setUserBean(new UserBean());
        }
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean) {
        this.session.put("userBean", userBean);
    }
    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
