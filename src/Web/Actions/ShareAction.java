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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class ShareAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private String musicTitle, artistName, groups;
    private static final String API_APP_KEY = "m8rp7duib3txihe";
    private static final String API_APP_SECRET = "k0a57qnvqp4dgg1";

    public String execute() {
        //vai buscar o ID da música de quem a partilhou
        String fileID = this.getUserBean().getFileID(musicTitle, artistName);

        if (fileID.equals("fail")) {
            session.put("message", "Could not share this file");
            return fileID;
        }

        //precisamos de ter os accountIDs de todas as pessoas dos grupos com que se vai partilhar
        String [] accountIDs = this.getUserBean().getAccountIDs(groups);

        System.out.println("accountIDs=");
        for (String i:accountIDs) {
            System.out.println(i);
        }


        //faz a partilha "tradicional", ou seja, adiciona esta música às transferred files de cada user do grupo
        this.getUserBean().share(musicTitle, artistName, groups);
        System.out.println("here");

        if (accountIDs == null) session.put("message", "Shared music successfully :D");
        //caso haja algum account ID, temos que partilhá-lo no dropbox
        //adicionar member à file na dropbox
        if (!this.session.containsKey("service")) {
            OAuthService service = new ServiceBuilder()
                    .provider(DropBoxApi2.class)
                    .apiKey(API_APP_KEY)
                    .apiSecret(API_APP_SECRET)
                    .callback("http://localhost:8080/menu")
                    .build();

            this.session.put("service", service);
        }
        System.out.println("here1");

        String shareWith = "";

        for (String user:accountIDs) {
            shareWith += ", ";
            shareWith += "{ \".tag\": \"dropbox_id\", \"dropbox_id\": \""+user+"\"}";
        }

        shareWith = shareWith.substring(1);
        System.out.println("sharewith users=" + shareWith);

        OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/sharing/add_file_member", (OAuthService)this.session.get("service"));
        request.addHeader("authorization", "Bearer "+this.getUserBean().getAccessToken());
        request.addHeader("content-type", "application/json");

        request.addPayload("{" +
                "\"file\": \""+fileID+"\"," +
                "\"members\": [" + shareWith+
                "], \"custom_message\": \"custom message\"," +
                " \"quiet\": false, " +
                "\"access_level\": \"viewer\", " +
                "\"add_message_as_comment\": false" +
                "}");

        Response response = request.send();

        System.out.println("response=" + response.getBody());
        session.put("message", "Shared music successfully :D");

        //quando um user faz share de um ficheiro, vai criar um shared_link para esse ficheiro, que vai ser acrescentado junto da música
        //correspondente para que os outros users possam ter acesso a ele

        OAuthRequest request2 = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/sharing/create_shared_link_with_settings" , (OAuthService)this.session.get("service"));
        request2.addHeader("Authorization", "Bearer "+this.getUserBean().getAccessToken());
        request2.addHeader("content-type", "application/json");

        request2.addPayload("{\"path\": \""+fileID+"\", \"settings\": {\"requested_visibility\": \"public\"}}");
        Response response2 = request2.send();

        System.out.println("MADE REQUEST FOR CREATING SHARING LINK");
        System.out.println(response2.getCode());
        System.out.println(response2.getBody());
        System.out.println("---------------------------------------");

        JSONObject obj = new JSONObject(response2.getBody());
        String url = (String) obj.get("url");

        //save url on Music DB
        this.getUserBean().saveFileURL(url, musicTitle, artistName);

        return SUCCESS;

    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
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
