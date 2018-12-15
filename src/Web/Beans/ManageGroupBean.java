package Web.Beans;

import java.rmi.RemoteException;

public class ManageGroupBean extends RMIBean {
    public ManageGroupBean(){
        super();
        lookup();
    }

    public String showRequests(String username){
        int trys=0;
        String requests;
        while(true) {
            try {
                if(trys>=30)
                    return null;
                requests = server.showRequests(username);
                return requests;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }

    public boolean manageRequest(String username, String newUser, String groupID, String toDo){
        boolean reply;
        int trys=0;
        while(true) {
            try {
                if(trys>=30)
                    return false;
                reply = server.manageRequests(username, newUser, groupID, toDo);
                return reply;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
