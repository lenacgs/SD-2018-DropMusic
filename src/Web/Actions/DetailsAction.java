package Web.Actions;

import Web.Beans.DetailsBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class DetailsAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String id;

    @Override
    public String execute() throws Exception{ //function that is to be executed when user presses button to submit LoginBean credentials
        String username = (String)session.get("username");
        String reply = this.getDetailsBean().details(username, (String)session.get("object"), id);
        if(reply.equals("Something went wrong... maybe the "+session.get("object")+" you entered does not exist!")){
            addActionError("Something went wrong. Please try again later...");
            return "FAILED";
        }
        else{
            session.put("details",reply);
            return "SUCCEED";
        }
    }

    public DetailsBean getDetailsBean(){
        DetailsBean bean = new DetailsBean();
        return bean;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
