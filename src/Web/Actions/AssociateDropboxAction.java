package Web.Actions;

import Web.Beans.LoginBean;
import Web.Beans.UserBean;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.*;
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

        /*//estava a tentar ir buscar a account ID mas esta merda, não funciona, vá-se lá saber porquê
        OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/users/get_current_account", service);
        request.addHeader("Authorization", "Bearer "+accessToken.getToken());
        request.addHeader("Content-type", "application/json");
        Response response = request.send();
        ParameterList paramList = request.getBodyParams();
        System.out.println("GETTING ACCOUNT INFO!!!------------------------------------");
        System.out.println("Request sent: "+accessToken.getToken());
        System.out.println(response.getBody());
        System.out.println("------------------------------------------------------------");*/
        session.put("accessToken", accessToken.getToken());
        session.put("message", "Success linking Dropbox account!");

        this.getUserBean().setAccessToken(accessToken.getToken());
        this.getUserBean().saveToken();
        return "SUCCESS";
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
