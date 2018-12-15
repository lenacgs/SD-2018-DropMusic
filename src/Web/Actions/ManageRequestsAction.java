package Web.Actions;

import Web.Beans.ManageGroupBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class ManageRequestsAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String request;
    private String toDo;

    @Override
    public String execute() throws Exception{
        String username = (String)session.get("username");
        String [] splitted = request.split(" ");
        String newUser = splitted[1].substring(1,splitted[1].length()-1);
        String groupID = splitted[0];
        boolean reply = this.getManageGroupBean().manageRequest(username,newUser,groupID,toDo);
        if(!reply){
            addActionError("Couldn't "+toDo+" the request. Please try again later...");
            return "FAILED";
        }
        else {
            if(toDo.equals("accept"))
                addActionMessage("The group request was accepted successfully");
            else
                addActionMessage("The group request was declined successfully");
            return "SUCCEED";
        }
    }

    public ManageGroupBean getManageGroupBean(){
        ManageGroupBean bean = new ManageGroupBean();
        return bean;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getRequest(){
        return request;
    }
    public void setRequest(String request){
        this.request=request;
    }

    public String getToDo(){
        return toDo;
    }
    public void setToDo(String toDo){
        this.toDo=toDo;
    }
}
