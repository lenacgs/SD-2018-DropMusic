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
        try {
            server = (Services) LocateRegistry.getRegistry(RMIPort).lookup(RMIName);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }


}
