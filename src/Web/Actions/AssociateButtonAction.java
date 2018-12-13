package Web.Actions;

import java.util.Map;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import Web.DropBoxApi2;
import com.github.scribejava.core.oauth.OAuthService;



public class AssociateButtonAction extends ActionSupport implements SessionAware{
    // Access codes #1: per application used to get access codes #2
    private static final String API_APP_KEY = "m8rp7duib3txihe";
    private static final String API_APP_SECRET = "k0a57qnvqp4dgg1";

    private Map<String, Object> session;
    private String authorization;

    public String execute() {
        System.out.println("associatebutton");
        OAuthService service = new ServiceBuilder()
                .provider(DropBoxApi2.class)
                .apiKey(API_APP_KEY)
                .apiSecret(API_APP_SECRET)
                .callback("http://localhost:8080/associateDropbox")
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
