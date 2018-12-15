package Web.Actions;

import Web.Beans.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import Web.Beans.RegisterBean;
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
                session.put("password", password);
                session.put("loggedIn", true);
                session.put("perks", perks);
                this.getUserBean().setUsername(username);
                this.getUserBean().setPassword(password);
                this.getUserBean().setPerks(perks);
                return SUCCESS;
            }else{
                return LOGIN;
            }
        }else{
            return LOGIN;
        }
    }

    public UserBean getUserBean() {
        if (!session.containsKey("userBean")) {
            this.setUserBean(new UserBean());
        }
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean) {
        this.session.put("userBean", userBean);
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
