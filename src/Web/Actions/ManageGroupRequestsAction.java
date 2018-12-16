package Web.Actions;

import Web.Beans.ManageGroupBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class ManageGroupRequestsAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() throws Exception{
        String username = (String)session.get("username");
        String reply = this.getManageGroupBean().showRequests(username);
        if(reply==null){
            addActionError("Either you have no new requests or our system is down momentarily");
            return "FAILED";
        }
        else {
            String[] combo = reply.split(",");
            String[][] request = new String[combo.length][2];
            for(int i=0; i<combo.length;i++){
                request[i]=combo[i].split(" ");
                request[i][1]=request[i][1].substring(1,request[i][1].length()-1);
                System.out.println(request[i][0]+":"+request[i][1]);
            }
            session.put("requests",combo);
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
}
