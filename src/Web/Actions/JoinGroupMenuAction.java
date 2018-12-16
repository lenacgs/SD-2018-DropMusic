package Web.Actions;

import Web.Beans.JoinGroupBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class JoinGroupMenuAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() throws Exception{

        String username = (String)session.get("username");
        String group = this.getJoinGroupBean().showGroups(username);
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

    public JoinGroupBean getJoinGroupBean(){
        JoinGroupBean bean = new JoinGroupBean();
        return bean;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
