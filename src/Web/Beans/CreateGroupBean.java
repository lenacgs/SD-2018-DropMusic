package Web.Beans;

import java.rmi.RemoteException;

public class CreateGroupBean extends RMIBean {

    public CreateGroupBean(){
        super();
    }

    public String createGroup(String username) {
        int trys=0;
        String groupID;
        while(true) {
            try {
                if(trys>=30)
                    return "-1";
                groupID = server.newGroup(username);
                return groupID;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
