package Web.Actions;

import Web.DropBoxApi2;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class LoginDropboxAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private static final String API_APP_KEY = "m8rp7duib3txihe";
    private static final String API_APP_SECRET = "k0a57qnvqp4dgg1";
    private String authorization;
    private OAuthService service;

    public String execute() {
        service = new ServiceBuilder()
                .provider(DropBoxApi2.class)
                .apiKey(API_APP_KEY)
                .apiSecret(API_APP_SECRET)
                .callback("http://localhost:8080/loginDropboxSuccess")
                .build();

        this.authorization = service.getAuthorizationUrl(null);
        this.session.put("service", service);
        return "REDIRECT";
    }

    public String getAuthorization() {
        return authorization;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
