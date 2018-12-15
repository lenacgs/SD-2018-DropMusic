package Web.Actions;

import Web.Beans.JoinGroupMenuBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class JoinGroupMenuAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() throws Exception{ //function that is to be executed when user presses button to submit LoginBean credentials

        String username = (String)session.get("username");
        String group = this.getJoinGroupMenuBean().showGroups(username);
        String groups[] = group.split(",");
        if(group==null) {
            addActionError("There are no groups available for you to join");
            return "FAILED";
        }
        else {
            session.put("groups",groups);
            return "SUCCEED";
        }
    }

    public JoinGroupMenuBean getJoinGroupMenuBean(){
        JoinGroupMenuBean bean = new JoinGroupMenuBean();
        return bean;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
