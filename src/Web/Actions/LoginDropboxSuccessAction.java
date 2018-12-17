package Web.Actions;

import Web.Beans.LoginBean;
import Web.Beans.UserBean;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONObject;

import java.util.Map;

public class LoginDropboxSuccessAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private OAuthService service;
    private String code;

    public String execute() {
        this.service = (OAuthService) session.get("service");

        //exchange code given by dropbox to get an accessToken for this user
        Verifier codeV = new Verifier(code);
        Token accessToken = service.getAccessToken(null, codeV);

        if(accessToken.isEmpty()) {
            session.put("message", "Error linking Dropbox account :(");
            return "FAIL";
        }

        //get account ID from current account
        //se o user já associou ao dropbox, então já tem um accesstoken
        OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/users/get_current_account", service);
        request.addHeader("Authorization", "Bearer "+ accessToken.getToken());
        request.addHeader("Content-type", "application/json");
        request.addPayload("null");
        Response response = request.send();

        JSONObject obj = new JSONObject(response.getBody());
        String accountID = obj.getString("account_id");

        //vai buscar o user cujo accountID = ao do user que está a tentar loggar
        String ans = this.getLoginBean().loginDropboxUser(accountID);
        if (ans.equals("FAIL")) {
            session.put("message", "Error logging in with dropbox account :(");
            return LOGIN;
        }

        String[] res = ans.split(",");
        //res[0]=perks, res[1]=username, res[2]=accessToken
        this.getUserBean().setPerks(Integer.parseInt(res[0]));
        this.getUserBean().setUsername(res[1]);
        this.getUserBean().setAccessToken(accessToken.getToken());

        session.put("accessToken", this.getUserBean().getAccessToken());
        session.put("loggedIn", true);
        session.put("username", this.getUserBean().getUsername());
        session.put("perks", this.getUserBean().getPerks());
        session.put("message", "Logged in with dropbox successfully!");

        return SUCCESS;
    }
    public LoginBean getLoginBean(){
        if(!session.containsKey("loginBean")){
            this.setLoginBean(new LoginBean());
        }
        return (LoginBean) session.get("loginBean");
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
