package Web.Actions;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.awt.*;
import java.util.Map;

public class LoginDropboxSuccessAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private OAuthService service;

    public String execute() {
        this.service = (OAuthService) session.get("service");
        Token accessToken = service.getAccessToken(null, null);
        session.put("accessToken", accessToken);
        if(accessToken.isEmpty()) {
            addActionMessage("Error logging in with Dropbox Account");
            return "FAIL";
        }
        return "SUCCESS";
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
