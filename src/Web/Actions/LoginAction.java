package Web.Actions;

import Web.Beans.UserBean;
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
                session.put("password", password);
                session.put("loggedIn", true);
                session.put("perks", perks);
                session.put("message", "Login successful!");

                this.getUserBean().setUsername(username);
                this.getUserBean().setPassword(password);
                this.getUserBean().setPerks(perks);
                String possible = this.getUserBean().loadAccessToken();
                if (!possible.equals("fail")) {
                    this.getUserBean().setAccessToken(possible);
                    session.put("accessToken", possible);
                }
                return SUCCESS;
            } else {
                session.put("message", "Could not login :(");
                return LOGIN;
            }
        } else {
            session.put("message", "Could not login :(");
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

    public UserBean getUserBean() {
        if (!session.containsKey("userBean")) {
            this.setUserBean(new UserBean());
        }
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean) {
        this.session.put("userBean", userBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
