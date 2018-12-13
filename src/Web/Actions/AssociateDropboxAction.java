package Web.Actions;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;

import java.util.Map;

public class AssociateDropboxAction {
    private Map<String, Object> session;
    private OAuthService service;


    public String execute() {
        this.service = (OAuthService) session.get("service");
        Token accessToken = service.getAccessToken(null, null);
        session.put("accessToken", accessToken);
        if(accessToken.isEmpty()) return "FAIL";
        return "SUCCESS";
    }
}
