package Web.Actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;
import Web.Beans.CreateGroupBean;

public class CreateGroupAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() throws Exception{

        String username = (String)session.get("username");
        String groupID = this.getCreateGroupBean().createGroup(username);
        if (groupID.equals("-1")){
            addActionError("Couldn't create new group. Try again later...");
            return "NO";
        }
        else {
            addActionMessage("New group created: "+groupID);
            return "YES";
        }
    }

    public CreateGroupBean getCreateGroupBean(){
        CreateGroupBean bean = new CreateGroupBean();
        return bean;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
