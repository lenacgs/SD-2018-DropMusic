package Web.Actions;

import com.opensymphony.xwork2.ActionSupport;
import Web.Beans.RegisterBean;
import Web.Beans.LoginBean;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;

public class RegisterAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private String username = null, password = null;
    private Map<String, Object> session;

    @Override
    public String execute() throws Exception{
        if(this.username != null && !username.equals("")){
            int perks = this.getRegisterBean().registerUser(username,password);
            if(perks <= 3 && perks > 0){
                session.put("username", username);
                session.put("loggedin", true);
                session.put("perks", perks);
                return SUCCESS;
            }else{
                return LOGIN;
            }
        }else{
            return LOGIN;
        }
    }

    public RegisterBean getRegisterBean() {
        if (!session.containsKey("registerBean"))
            this.setRegisterBean(new RegisterBean());
        return (RegisterBean) session.get("registerBean");
    }


    public void setRegisterBean(RegisterBean registerBean) {
        this.session.put("registerBean", registerBean);
    }


    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }


}
