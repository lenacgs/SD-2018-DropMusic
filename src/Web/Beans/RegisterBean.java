package Web.Beans;

import java.rmi.RemoteException;

public class RegisterBean extends RMIBean {

    public RegisterBean () {
        super();
    }

    public int registerUser(String username, String password){
        int perks;
        try{
            perks = server.register(username, password);
            return perks;
        }catch (RemoteException e){
            e.printStackTrace();
            return -1;
        }
    }
}
