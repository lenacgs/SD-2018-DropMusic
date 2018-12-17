package Web.Actions;

import Web.Beans.AddInfoBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class AddInfoAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String group, title, artist, genre, duration, description, concerts, musics, year, publisher, object;

    @Override
    public String execute() throws Exception{
        String username = (String)session.get("username");
        String reply;
        if(object.equals("music")){
            reply = this.getAddInfoBean().addMusic(username, group, title, artist, genre, duration);
            if(reply.equals("New song successfully added!")){
                addActionMessage(reply);
                return "SUCCEED";
            }
            else{
                addActionError(reply);
                return "FAILED";
            }
        }
        else if (object.equals("artist")){
            reply = this.getAddInfoBean().addArtist(username, group, title, description, concerts, genre);
            if(reply.equals("New artist successfully added!")){
                addActionMessage(reply);
                return "SUCCEED";
            }
            else{
                addActionError(reply);
                return "FAILED";
            }
        }
        else if (object.equals("album")){
            reply = this.getAddInfoBean().addAlbum(username, group, artist, title, musics, year, publisher, genre, description);
            if(reply.equals("New album successfully added!")){
                addActionMessage(reply);
                return "SUCCEED";
            }
            else{
                addActionError(reply);
                return "FAILED";
            }
        }
        else{
            addActionError("There was an error adding the new information. Please try again later...");
            return "FAILED";
        }
    }

    public AddInfoBean getAddInfoBean(){
        AddInfoBean bean = new AddInfoBean();
        return bean;
    }


    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConcerts() {
        return concerts;
    }

    public void setConcerts(String concerts) {
        this.concerts = concerts;
    }

    public String getMusics() {
        return musics;
    }

    public void setMusics(String musics) {
        this.musics = musics;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}
