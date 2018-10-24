package rmi;
import java.rmi.Remote;


public interface Services extends Remote {
    int hello() throws java.rmi.RemoteException;
    void newClient(int port) throws java.rmi.RemoteException;
    void ping() throws java.rmi.RemoteException;
    int register (String username, String password) throws java.rmi.RemoteException;
    int login(String username, String password) throws java.rmi.RemoteException;
    boolean logout(String username) throws java.rmi.RemoteException;
    String search(String keyword, String object) throws java.rmi.RemoteException;
    String details(String object, String title) throws java.rmi.RemoteException;
    boolean review(String title,String user,String review,int rating) throws java.rmi.RemoteException;
    String showGroups(String username)throws java.rmi.RemoteException;
    boolean joinGroup(String username, String group)throws java.rmi.RemoteException;
    String newGroup(String username)throws java.rmi.RemoteException;
    String changeInfo(String object, String objectName, String text, String username, String groupID)throws java.rmi.RemoteException;
    String givePermissions(String perk, String username, String newUser, String groupID)throws java.rmi.RemoteException;
    boolean uploadFile(String username, String musicTitle) throws java.rmi.RemoteException;
}