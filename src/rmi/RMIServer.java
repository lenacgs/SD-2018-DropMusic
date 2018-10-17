package rmi;

import java.rmi.AccessException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements Services {

    private static Services s;

    private RMIServer() throws RemoteException {

    }

    public static void main(String[] args) throws RemoteException {
        try {
            s = new RMIServer();
            createRegistry();
        } catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    private static void createRegistry() throws RemoteException, InterruptedException {
        int port = 7000;
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("Sporting", s);
            System.out.println("Main RMI ready!");
        } catch (AccessException e) {
            System.out.println("main.AccessException: " + e.getMessage());
        } catch (ExportException e) {
            System.out.println("There is already a RMI Server. Changing to backup...");
            secondaryRMI();
        }
    }

    private static void secondaryRMI() throws RemoteException, InterruptedException {

        try {
            s = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting"); // liga-se ao RMI Primário
            System.out.println("Backup RMI ready!");
        } catch (ConnectException | NotBoundException e) {
            System.out.println("Attempting to become primary RMI server...");
            createRegistry(); //se não der, é porque entretanto deu cagada no primário e tenta ser ele o primário
        }
        int timer = 1;
        //iniciam os pings
        while (true){
            try {
                Thread.sleep(500);
                s.ping();
            } catch (ConnectException e) {
                if(timer>=6){
                    System.out.println("Timeout exceeded. Attempting to be Main Server");
                    s = new RMIServer();
                    createRegistry();
                    break;
                }
                else {
                    try {
                        s = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting"); // liga-se ao RMI Primário
                    } catch (ConnectException | NotBoundException exception) {
                        System.out.println("No ping received: " + timer);
                        timer++;
                    }
                }
            }
        }
    }

    public void hello() throws java.rmi.RemoteException {
        System.out.println("New client connected!");
    }

    public void ping() throws java.rmi.RemoteException {
        //esta funcao nao precisa de fazer nada pois so serve para testar a ligacao entre o primario e o secundario
    }

    public int testerLogin() throws java.rmi.RemoteException{
        return 1;
    }

    public boolean tester() throws java.rmi.RemoteException{
        return true;
        //enquanto nao temos a funcao esta serve para ir testando.
    }
    public int register (String username, String password) throws java.rmi.RemoteException{
        String request = "type | register ; username | "+username+" ; password | "+password;
        return 1; //alterar quando comunicar com o multicast
    }
    public int login(String username, String password) throws java.rmi.RemoteException{
        String request = "type | login ; username | "+username+" ; password | "+password;
        return 1; //alterar quando comunicar com o multicast
    }

    public boolean logout(String username) throws java.rmi.RemoteException{
        //envia informação aos multicasts que este user já nao está online
        return true;
    }

    public String search(String keyword, String object) throws java.rmi.RemoteException{
        //faz request aos multicasts para Search
        String request = "type | search ; keyword | "+keyword+" ; object | "+object;
        return "Toma la a resposta mano : "+request;
    }

    public String details(String object, String title) throws java.rmi.RemoteException{
        String request = "type | get_info ; object | "+object+" ; title | "+title;
        return request;
    }

    public boolean review(String title,String user,String review,int rating) throws java.rmi.RemoteException{
        String request = "type | review ; album_title | "+title+" ; username | "+user+" ; text | "+review+" ; rate | "+rating;
        return true;
    }
}
