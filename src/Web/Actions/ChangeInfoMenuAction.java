package Web.Actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class ChangeInfoMenuAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() throws Exception{
        session.put("operation","change");
        return "SUCCEED";
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
