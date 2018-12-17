package Web.Actions;

import Web.Beans.GivePermissionsBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import ws.WebSocketAnnotation;

import java.util.Map;

public class GivePermissionsAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String groupId, newUser, perk;

    @Override
    public String execute() throws Exception{ //function that is to be executed when user presses button to submit LoginBean credentials
        String username = (String)session.get("username");
        boolean reply = this.getGivePermissionsBean().givePermissions(perk, username, newUser, groupId);
        if(reply){
            addActionMessage(newUser+" permissions successfully updated");
            WebSocketAnnotation websocket = new WebSocketAnnotation();
            String notification = "Your permissions on group " + groupId + " have been upgraded to " + perk + "!";
            websocket.sendNotification(notification, newUser);
            return "SUCCESS";
        }
        else{
            addActionError("Couldn't give '"+perk+"' permissions either because you are not owner of that group or that group does not exist");
            return "FAILED";
        }

    }

    public GivePermissionsBean getGivePermissionsBean(){
        GivePermissionsBean bean = new GivePermissionsBean();
        return bean;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getNewUser() {
        return newUser;
    }

    public void setNewUser(String newUser) {
        this.newUser = newUser;
    }

    public String getPerk() {
        return perk;
    }

    public void setPerk(String perk) {
        this.perk = perk;
    }
}
