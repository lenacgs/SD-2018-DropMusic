package dropmusic.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

public class RegisterAction extends ActionSupport{
    private static final long serialVersionUID = 4L;
    private String username = null, password = null;

    @Override
    public String execute() throws Exception{
        return SUCCESS;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }


}
