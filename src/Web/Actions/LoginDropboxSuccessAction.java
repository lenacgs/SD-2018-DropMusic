package Web.Actions;

import Web.Beans.LoginBean;
import Web.Beans.UserBean;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.awt.*;
import java.util.Map;

public class LoginDropboxSuccessAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private OAuthService service;
    private String code;

    public String execute() {
        this.service = (OAuthService) session.get("service");
        Verifier codeV = new Verifier(code);
        Token accessToken = service.getAccessToken(null, codeV);

        String res = this.getLoginBean().loginDropboxUser(accessToken.getToken());
        if (res.equals("FAIL")) {
            session.put("message", "Error loggin in with Dropbox account");
            return "FAIL";
        }
        String[] resx = res.split(",");
        //res[0]=perks, res[1]=username, res[2]=accessToken
        this.getUserBean().setPerks(Integer.parseInt(resx[0]));
        this.getUserBean().setUsername(resx[1]);
        this.getUserBean().setAccessToken(resx[2]);

        session.put("accessToken", accessToken);
        session.put("message", "Logged in with dropbox successfully!");

        System.out.println("-----LOGGED IN WITH DROPBOX: "+this.getUserBean().getUsername());
        return "SUCCESS";
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

    public UserBean getUserBean(){
        if(!session.containsKey("userBean")){
            this.setUserBean(new UserBean());
        }
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean){
        this.session.put("userBean", userBean);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
