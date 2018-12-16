package Web.Beans;

import java.rmi.RemoteException;

public class DetailsBean extends RMIBean{

    public DetailsBean(){
        super();
        lookup();
    }

    public String albumDetails(String username, String object, String id, String artist){
        int trys=0;
        String reply;
        while(true) {
            try {
                if(trys>=30)
                    return null;
                System.out.println("a");
                reply = server.details(username, object, id, artist);
                System.out.println("b");
                System.out.println(reply);
                return reply;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }

    public String artistDetails(String username, String object, String id){
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
