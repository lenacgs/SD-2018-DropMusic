package rmi;
import java.rmi.Remote;


public interface Services extends Remote {
    int hello() throws java.rmi.RemoteException;
    void newClient(int port) throws java.rmi.RemoteException;
    void ping() throws java.rmi.RemoteException;
    int register (String username, String password) throws java.rmi.RemoteException;
    int login(String username, String password) throws java.rmi.RemoteException;
    boolean logout(String username) throws java.rmi.RemoteException;
    String search(String user, String keyword, String object) throws java.rmi.RemoteException;
    String details(String username, String object, String artist, String title) throws java.rmi.RemoteException;
    String details(String username, String object, String title) throws java.rmi.RemoteException;
    boolean review(String title,String artist,String user,String review,int rating) throws java.rmi.RemoteException;
    String showGroups(String username)throws java.rmi.RemoteException;
    String joinGroup(String username, String group)throws java.rmi.RemoteException;
    String newGroup(String username)throws java.rmi.RemoteException;
    boolean changeInfo(String username, String groups, String type, String s1, String s2, String s3, String s4)throws java.rmi.RemoteException;
    boolean changeInfo(String username, String groupIDs, String title, String artist, String musics, String year, String publisher, String genre, String description) throws java.rmi.RemoteException;
    boolean addInfo(String username, String groups, String type, String title, String artist, String genre, String duration)throws java.rmi.RemoteException; //used for musics and artists
    boolean addInfo(String username, String groupIDs, String title, String artist, String musics, String year, String publisher, String genre, String description) throws java.rmi.RemoteException;
    boolean givePermissions(String perk, String username, String newUser, String groupID)throws java.rmi.RemoteException;
    boolean uploadFile(String username, String musicTitle) throws java.rmi.RemoteException;
    String showRequests(String username) throws java.rmi.RemoteException;
    boolean manageRequests(String username, String newUser, String groupID, String toDo)throws java.rmi.RemoteException;
}