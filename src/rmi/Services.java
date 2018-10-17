package rmi;
import java.rmi.Remote;


public interface Services extends Remote {
    void hello() throws java.rmi.RemoteException;
    void ping() throws java.rmi.RemoteException;
    int testerLogin() throws java.rmi.RemoteException;
    boolean tester() throws java.rmi.RemoteException;
    boolean logout(String username) throws java.rmi.RemoteException;
    String search(String keyword, String object) throws java.rmi.RemoteException;
    String details(String object, String title) throws java.rmi.RemoteException;
    boolean review(String title,String user,String review,int rating) throws java.rmi.RemoteException;
}