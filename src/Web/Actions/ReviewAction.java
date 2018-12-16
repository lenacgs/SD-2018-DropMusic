package Web.Actions;

import Web.Beans.ReviewBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class ReviewAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String title, artist, review, rate;

    @Override
    public String execute() throws Exception{
        String username = (String)session.get("username");
        boolean reply = this.getReviewBean().albumReview(title, artist, username, review, rate);
        if(reply){
            addActionMessage("Album '"+title+"' reviewed successfully");
            return "SUCCEED";
        }
        else {
            addActionError("Something went wrong and the album wasn't reviewed. Maybe the album doesn't exist");
            return "FAILED";
        }
    }

    public ReviewBean getReviewBean(){
        ReviewBean bean = new ReviewBean();
        return bean;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
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

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
