package dropmusic.model;

import rmi.Services;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class register {
    private Services services;

    public register(){
        try{
            services = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting");
        }catch (NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }

    public int register(String username, String password){
        int perks;
        try{
            perks = services.register(username, password);
            return perks;
        }catch (RemoteException e){
            e.printStackTrace();
            return -1;
        }
    }
}
