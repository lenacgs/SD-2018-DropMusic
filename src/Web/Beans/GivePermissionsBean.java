package Web.Beans;

import java.rmi.RemoteException;

public class GivePermissionsBean extends RMIBean {

    public GivePermissionsBean(){
        super();
        lookup();
    }

    public boolean givePermissions(String perk, String username, String newUser, String groupId){
        boolean reply;
        int trys=0;
        while(true) {
            try {
                if(trys>=30)
                    return false;
                reply = server.givePermissions(perk, username, newUser, groupId);
                return reply;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
