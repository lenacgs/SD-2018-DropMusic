package Web.Beans;

import java.rmi.RemoteException;

public class LoginBean extends RMIBean {

    public LoginBean(){
        super();
        lookup();
    }

    public int loginUser(String username, String password){
        int perks,trys=0;
        while(true) {
            try {
                if(trys>=30)
                    return -1;
                perks = server.login(username, password);
                return perks;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
