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

import java.util.Map;

public class UploadThisAction extends ActionSupport implements SessionAware {
    String artistName, musicTitle, index;
    private Map<String, Object> session;
    private static final String API_APP_KEY = "m8rp7duib3txihe";
    private static final String API_APP_SECRET = "k0a57qnvqp4dgg1";

    //associa o username + ID do ficheiro à música na base de dados

    public String execute() {
        //gets this file ID
        if (!this.session.containsKey("service")) {
            OAuthService service = new ServiceBuilder()
                    .provider(DropBoxApi2.class)
                    .apiKey(API_APP_KEY)
                    .apiSecret(API_APP_SECRET)
                    .callback("http://localhost:8080/menu")
                    .build();

            this.session.put("service", service);
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

        JSONObject obj = new JSONObject(response.getBody());
        JSONArray contents = (JSONArray) obj.get("entries");

        JSONObject toUpload = (JSONObject) contents.get(Integer.parseInt(index)-1);
        String ID = (String) toUpload.get("id");
        System.out.println("ID of toUpload="+ID);

        if (this.getUserBean().uploadDropbox(this.getUserBean().getUsername(), musicTitle, artistName, ID)) {
            this.session.put("message", "File successfully uploaded");
            return SUCCESS;
        }
        this.session.put("message", "Could not upload file :(");
        return "fail";
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
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
