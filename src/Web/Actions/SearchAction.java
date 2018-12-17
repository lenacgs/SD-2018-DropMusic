package Web.Actions;

import Web.Beans.SearchBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class SearchAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String object, keyword;

    @Override
    public String execute() throws Exception{ //function that is to be executed when user presses button to submit LoginBean credentials
        String username = (String)session.get("username");
        String reply = this.getSearchBean().search(username, keyword, object);
        if(reply==null){
            addActionError("Something's wrong. Please, try again later...");
            return "FAILED";
        }
        else if(reply.equals("No "+object+"s matching your keyword(s) were found")){
            addActionError(reply);
            return "FAILED";
        }
        else{
            session.put("object",object);
            String[] splitted = reply.split("\n");
            session.put("search",splitted);
            return "SUCCEED";
        }

    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keywork) {
        this.keyword = keywork;
    }

    public SearchBean getSearchBean(){
        SearchBean bean = new SearchBean();

        return bean;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
