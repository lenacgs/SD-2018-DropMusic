package Web.models;

public class SearchModel {
    public String keyword = null;
    public String option = null;

    public SearchModel(String keyword, String option){
        setKeyword(keyword);
        setOption(option);
    }

    public SearchModel() { }

    public String getKeyword() {
        return keyword;
    }

    public String getOption() {
        return option;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
