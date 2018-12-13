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
    private String code;


    public String execute() {
        this.service = (OAuth20ServiceImpl) session.get("service");

        //exchange code given by dropbox to get an accessToken for this user
        Verifier codeV = new Verifier(code);
        Token accessToken = service.getAccessToken(null, codeV);

        if(accessToken.isEmpty()) {
            session.put("message", "Error linking Dropbox account :(");
            return "FAIL";
        }
        session.put("accessToken", accessToken);
        session.put("message", "Success linking Dropbox account!");
        return "SUCCESS";
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
