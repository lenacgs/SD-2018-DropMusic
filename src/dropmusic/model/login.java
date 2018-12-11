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

    public int login(String username, String password){
        int perks;
        try{
            perks = server.login(username, password);
            return perks;
        }catch (RemoteException e){
            e.printStackTrace();
            return -1;
        }

    }
}
