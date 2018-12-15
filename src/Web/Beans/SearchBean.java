package Web.Beans;

import java.rmi.RemoteException;

public class SearchBean extends RMIBean{

    public SearchBean(){
        super();
    }

    public String search(String username, String keyword, String option){
        try{
            return this.server.search(username, keyword, option);
        }catch (RemoteException e){
            e.printStackTrace();
            return null;
        }
    }

}
