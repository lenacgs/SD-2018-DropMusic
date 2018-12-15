package Web.Beans;

import rmi.Services;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIBean {
    protected Services server = null;
    private String RMIName = "Sporting";
    private int RMIPort = 7000;

    public RMIBean() {
    }

    void lookup() {
        try {
            server = (Services) LocateRegistry.getRegistry(RMIPort).lookup(RMIName);
            System.out.println("fez lookup");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    int reestablishConnection(int trys){
        try {
            trys++;
            lookup();
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            System.out.println(".");
        }
        return trys;
    }
}
