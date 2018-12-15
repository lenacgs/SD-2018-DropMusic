package Web.Beans;

import java.rmi.RemoteException;

public class JoinGroupBean extends RMIBean {

    public JoinGroupBean(){
        super();
        lookup();
    }

    public String joinGroup (String username, String id){
        String reply;
        int trys=0;
        while(true) {
            try {
                if(trys>=30)
                    return null;
                reply = server.joinGroup(username,id);
                return reply;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
