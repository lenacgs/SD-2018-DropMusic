package Web.Actions;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20ServiceImpl;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class AssociateDropboxAction extends ActionSupport implements SessionAware{
    private Map<String, Object> session;
    private OAuth20ServiceImpl service;
    private String code, state;


    public String execute() {
        this.service = (OAuth20ServiceImpl) session.get("service");
        System.out.println("associatedropbox");

        //exchange code given by dropbox to get an accessToken for this user
        Verifier codeV = new Verifier(code);
        Token accessToken = service.getAccessToken(null, codeV);
        session.put("accessToken", accessToken);
        if(accessToken.isEmpty()) {
            addActionMessage("Error linking Dropbox Account");
            return "FAIL";
        }
        return "SUCCESS";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
