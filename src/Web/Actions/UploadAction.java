package Web.Actions;

import Web.Beans.UserBean;
import Web.DropBoxApi2;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class UploadAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private static final String API_APP_KEY = "m8rp7duib3txihe";
    private static final String API_APP_SECRET = "k0a57qnvqp4dgg1";
    private ArrayList<String> musics = new ArrayList<>();

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

    //list files que o user tem na dropbox
    public String execute() {
        if (!this.session.containsKey("service")) {
            OAuthService service = new ServiceBuilder()
                    .provider(DropBoxApi2.class)
                    .apiKey(API_APP_KEY)
                    .apiSecret(API_APP_SECRET)
                    .callback("http://localhost:8080/menu")
                    .build();

            this.session.put("service", service);
        }

        if (this.getUserBean().getAccessToken() == null) {
            session.put("message", "You haven't linked your dropbox account :(");
            return "fail";
        }

        OAuthRequest request = new OAuthRequest(Verb.POST,"https://api.dropboxapi.com/2/files/list_folder", (OAuthService)this.session.get("service"));
        request.addHeader("authorization", "Bearer " + this.getUserBean().getAccessToken());
        request.addPayload("{\n" +
                "    \"path\": \"\"," +
                "    \"recursive\": false," +
                "    \"include_media_info\": false," +
                "    \"include_deleted\": false," +
                "    \"include_has_explicit_shared_members\": false," +
                "    \"include_mounted_folders\": true" +
                "}");
        request.addHeader("Content-Type",  "application/json");
        Response response = request.send();

        System.out.println("Got it! Lets see what we found...");
        System.out.println("HTTP RESPONSE: =============");
        System.out.println(response.getCode());
        System.out.println(response.getBody());
        System.out.println("END RESPONSE ===============");

        JSONObject obj = new JSONObject(response.getBody());
        JSONArray contents = (JSONArray) obj.get("entries");
        for (int i=0; i<contents.length(); i++) {

            JSONObject item = (JSONObject) contents.get(i);
            String path = (String) item.get("name");
            String ID = (String) item.get("id");
            int j = i+1;
            this.musics.add(j+" - "+path); //adds music name to the list that will be seen in the jsp
            System.out.println(j+" - "+path + " - " + ID);
        }

        return SUCCESS;
    }

    public ArrayList<String> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<String> musics) {
        this.musics = musics;
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

}
