package rmiClient;

import rmi.Services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Clients extends Remote {
    void notification (String message) throws RemoteException;
    String getUsername () throws RemoteException;
}
