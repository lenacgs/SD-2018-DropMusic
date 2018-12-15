package Web.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Interface.Music;
import Web.Beans.SearchBean;
import Web.models.AlbumResultModel;
import Web.models.ArtistResultModel;
import Web.models.MusicResultModel;
import Web.models.SearchModel;

    public class SearchService{

    public SearchService(){}

    public List<Object> search(SearchModel query, Map<String, Object> session) {
        if (query instanceof SearchModel) {
            List<Object> results = new ArrayList<Object>();
            SearchBean s = (SearchBean) session.get("searchBean");
            String[] splitted = s.search((String) session.get("username"), query.getKeyword(), query.getOption()).split(" ; ");
            String[] reply = splitted[2].split(" \\| ")[1].split("\n");

            if(query.getOption().equals("music")){
                for(String ans : reply){
                    String[] aux = ans.split(", ");
                    results.add(new MusicResultModel(aux[0], aux[1]));
                }
            }else if(query.getOption().equals("artist")){
                for (String ans : reply) {
                    results.add(new ArtistResultModel(ans));
                }
            }else if(query.getOption().equals("album")){
                for(String ans : reply){
                    String[] aux = ans.split(", ");
                    results.add(new AlbumResultModel(aux[0], aux[1]));
                }
            }

            return results;
        }
        return null;
    }
}
