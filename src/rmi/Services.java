package rmi;
import java.rmi.Remote;


public interface Services extends Remote {
    void hello() throws java.rmi.RemoteException;
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
    boolean addInfo(String username, String type, String title, String artist, String genre, String duration); //used for musics and artists
    boolean addInfo(String username, String artist, String title, String musics, String year, String publisher, String genre, String description); //user for albums
    String givePermissions(String perk, String username, String newUser, String groupID)throws java.rmi.RemoteException;
    boolean uploadFile(String username, String musicTitle) throws java.rmi.RemoteException;
}