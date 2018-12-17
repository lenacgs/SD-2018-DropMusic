package Web.Actions;

import Web.Beans.LoginBean;
import Web.Beans.UserBean;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20ServiceImpl;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.tools.example.debug.expr.ParseException;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.struts2.interceptor.SessionAware;
import org.json.*;
import java.util.Map;

public class AssociateDropboxAction extends ActionSupport implements SessionAware{
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

        OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/users/get_current_account", service);
        request.addHeader("Authorization", "Bearer "+accessToken.getToken());
        request.addHeader("Content-type", "application/json");
        request.addPayload("null");
        Response response = request.send();

        JSONObject obj = new JSONObject(response.getBody());
        String accountID = obj.getString("account_id");

        session.put("accountID", accountID);
        session.put("accessToken", accessToken.getToken());
        session.put("message", "Success linking Dropbox account!");

        this.getUserBean().setAccountID(accountID);
        this.getUserBean().setAccessToken(accessToken.getToken());
        this.getUserBean().saveAccountID();
        this.getUserBean().saveAccessToken();
        return SUCCESS;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
