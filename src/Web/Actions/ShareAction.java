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
        System.out.println("hello");
        String fileID = this.getUserBean().getFileID(musicTitle, artistName);

        System.out.println("");


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
