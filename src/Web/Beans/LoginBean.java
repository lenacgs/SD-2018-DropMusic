package Web.Beans;

import rmi.Services;

import java.rmi.RemoteException;

public class LoginBean extends RMIBean {

    public LoginBean(){
        super();
    }

    public int loginUser(String username, String password){
        int perks;
        try{
            perks = server.login(username, password);
            return perks;
        }catch (RemoteException e){
            e.printStackTrace();
            return -1;
        }

    }

    public Services getRMIServer(){
        return this.server;
    }
}
