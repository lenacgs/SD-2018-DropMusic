package Web.Actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;
import Web.Beans.LoginBean;

public class LoginAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;

    @Override
    public String execute() throws Exception{ //function that is to be executed when user presses button to submit LoginBean credentials
        if(this.username != null && !username.equals("")){
            int perks = this.getLoginBean().loginUser(username,password); //LoginBean.loginUser(username, password) returns perks of this user
            if (perks <= 3 && perks > 0){
                session.put("username", username);
                session.put("loggedIn", true);
                session.put("perks", perks);
                return SUCCESS;
            } else {
                return LOGIN;
            }
        } else {
            return LOGIN;
        }
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }


    public LoginBean getLoginBean(){
        if(!session.containsKey("loginBean")){
            this.setLoginBean(new LoginBean());
        }
        return (LoginBean) session.get("loginBean");
    }

    public void setLoginBean(LoginBean loginBean){
        this.session.put("loginBean", loginBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
