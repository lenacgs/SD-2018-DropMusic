package dropmusic.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;
import dropmusic.model.login;

public class LoginAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;
    private int perks;

    @Override
    public String execute() throws Exception{
        if(this.username != null && !username.equals("")){
            perks = this.getLogin().login(username,password);
            if(perks < 3 && perks > 0){
                session.put("username", username);
                session.put("loggedin", true);
                return SUCCESS;
            }else if(perks == 4){
                return LOGIN;
            }else if(perks == 5){
                return LOGIN;
            }else{
                return LOGIN;
            }

        }else{
            return LOGIN;
        }

    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }


    public login getLogin(){
        if(!session.containsKey("login")){
            this.setLogin(new login());
        }
        return (login) session.get("login");
    }

    public void setLogin(login login){
        this.session.put("login", login);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
