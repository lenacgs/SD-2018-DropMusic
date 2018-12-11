package dropmusic.action;

import com.opensymphony.xwork2.ActionSupport;
import dropmusic.model.login;
import dropmusic.model.register;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

public class RegisterAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;
    private int perks;

    @Override
    public String execute() throws Exception{
        if(this.username != null && this.password != null && !this.username.equals("") && !this.password.equals("")){
            perks = new register().register(username, password);
            if(perks > 0 && perks < 4){
                this.getLogin();
                session.put("username",username);
                session.put("perks", perks);
                session.put("loggedin",true);
                return SUCCESS;
            }else{
                return ERROR;
            }
        }else{
            return ERROR;
        }
    }





    public login getLogin(){
        if(!session.containsKey("login")){
            this.setLogin(new login());
        }
        return (login) session.get("login");
    }
    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setLogin(login login){
        this.session.put("login", login);
    }

    @Override
    public void setSession(Map<String,Object> session){
        this.session = session;
    }
}
