package Web.Actions;

import Web.Beans.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class MyFilesAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    String[][] results;

    public String execute() {
        String [] files = this.getUserBean().getTransferredMusics(); //each index has one file: title,artist,url,sharedby

        if (files == null) {
            session.put("message", "You don't have access to any music files");
            return "fail";
        }

        results = new String[files.length][4];

        for (int i=0; i<files.length; i++) {
            results[i] = files[i].split(",");
            results[i][2]+="&raw=1";
        }


        session.put("message", "");
        return SUCCESS;
    }

    public String[][] getResults() {
        return results;
    }

    public void setResults(String[][] results) {
        this.results = results;
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
