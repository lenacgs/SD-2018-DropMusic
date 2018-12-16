package Web.Beans;

import java.rmi.RemoteException;

public class RegisterBean extends RMIBean {

    public RegisterBean () {
        super();
        lookup();
    }

    public int registerUser(String username, String password){
        int perks, trys=0;
        while(true) {
            try {
                if(trys>=30)
                    return -1;
                perks = server.register(username, password);
                return perks;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
