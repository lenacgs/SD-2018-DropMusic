package Web.Beans;

import java.rmi.RemoteException;

public class DetailsBean extends RMIBean{

    public DetailsBean(){
        super();
        lookup();
    }

    public String details(String username, String object, String id){
        int trys=0;
        String reply;
        while(true) {
            try {
                if(trys>=30)
                    return null;
                reply = server.details(username, object, id);
                return reply;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
