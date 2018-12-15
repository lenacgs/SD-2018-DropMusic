package Web.Actions;

import Web.Beans.SearchBean;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class SearchAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private String keyword;
    private String option;

    @Override
    public String execute() throws Exception{
        if(this.keyword != null && !this.keyword.equals("") && this.option != null){
            String ans = this.getRegisterBean().search((String)session.get("username"),keyword, option);
            String[]splitted = ans.split(" ; ");

        }else{

        }
        return SUCCESS;
    }


    public void setOption(String option){
        this.option = option;
    }

    public void setKeyword(String keyword){
        this.keyword = keyword;
    }


    public SearchBean getRegisterBean() {
        if (!session.containsKey("searchBean"))
            this.setRegisterBean(new SearchBean());
        return (SearchBean) session.get("searchBean");
    }


    public void setRegisterBean(SearchBean searchBean) {
        this.session.put("searchBean", searchBean);
    }


    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
