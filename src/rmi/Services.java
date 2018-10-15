package rmi;
import java.rmi.Remote;


public interface Services extends Remote {
    void hello() throws java.rmi.RemoteException;
    void ping() throws java.rmi.RemoteException;
    int testerLogin() throws java.rmi.RemoteException;
    boolean tester() throws java.rmi.RemoteException;
}