package Web.Beans;

import java.rmi.RemoteException;

public class SearchBean extends RMIBean{

    public SearchBean(){
        super();
        lookup();
    }

    public String search(String username, String keyword, String object){
        int trys=0;
        String reply;
        while(true) {
            try {
                if(trys>=30)
                    return null;
                reply = server.search(username, keyword, object);
                return reply;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
