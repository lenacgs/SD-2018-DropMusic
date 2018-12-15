package Web.Actions;

import Web.Beans.SearchBean;
import Web.Services.SearchService;
import Web.models.SearchModel;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private SearchModel inputObject;
    private SearchService service;

    List<Object> results;

    @Override
    public String execute(){
        getSearchBean();
        setResults(getService().search(this.getInputObject(), session));
        return SUCCESS;
    }



    public SearchBean getSearchBean() {
        if (!session.containsKey("searchBean"))
            this.setRegisterBean(new SearchBean());
        return (SearchBean) session.get("searchBean");
    }


    public void setResults(List<Object> results) {
        this.results = results;
    }

    public SearchService getService() {
        return service;
    }

    public void setService(SearchService service) {
        this.service = service;
    }

    public SearchModel getInputObject() {
        return inputObject;
    }

    public void setRegisterBean(SearchBean searchBean) {
        this.session.put("searchBean", searchBean);
    }



    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

    public void setSearchService(SearchService searchService) {
        this.service = searchService;
    }

    public void setInputObject(SearchModel inputObject) {
        this.inputObject = inputObject;
    }
}
