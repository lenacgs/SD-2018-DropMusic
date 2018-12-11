package dropmusic.model;
import rmi.Services;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class login {
    private Services server;

    public login(){
        try{
            server = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting");
        }catch(NotBoundException | RemoteException e){

            e.printStackTrace();
        }
    }

    public boolean retryRMIConnection(){
        int count = 0;
        while(count < 5){
            try{
                server = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting");
                return true;
            }catch(NotBoundException | RemoteException e){
                count++;
            }
        }
        return false;
    }

    public int login(String username, String password){
        int perks = -1;
        try{
            perks = server.login(username, password);
            return perks;
        }catch (RemoteException e){
            if(retryRMIConnection()){
                perks =login(username, password);
            }
        }
        return perks;
    }
}
