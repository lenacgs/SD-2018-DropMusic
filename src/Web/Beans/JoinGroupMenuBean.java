package Web.Beans;

import java.rmi.RemoteException;

public class JoinGroupMenuBean extends RMIBean {

    public JoinGroupMenuBean(){
        super();
        lookup();
    }

    public String showGroups(String username){
        String groups;
        int trys=0;
        while(true) {
            try {
                if(trys>=30)
                    return null;
                groups = server.showGroups(username);
                return groups;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
