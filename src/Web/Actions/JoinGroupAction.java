package Web.Actions;

import Web.Beans.JoinGroupBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class JoinGroupAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String id;

    @Override
    public String execute() throws Exception{ //function that is to be executed when user presses button to submit LoginBean credentials
        String username = (String)session.get("username");
        String reply = this.getJoinGroupBean().joinGroup(username, id);
        if(!reply.equals("success")){
            addActionError(reply);
            return "FAILED";
        }
        else {
            addActionMessage("Group application sent to group owner(s)");
            return "SUCCEED";
        }

    }

    public JoinGroupBean getJoinGroupBean(){
        JoinGroupBean bean = new JoinGroupBean();
        return bean;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public void setId (String id){
        this.id=id;
    }
    public String getId (){
        return id;
    }
}
