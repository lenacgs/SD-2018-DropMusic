package Web.Actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class ManageGroupsMenuAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() throws Exception{
        int perk = (int)session.get("perks");
        if(perk!=1) {
            addActionError("You are not a group owner!");
            return "FAILED";
        }
        return "SUCCEED";
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
