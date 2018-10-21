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

    public int register (String username, String password) throws java.rmi.RemoteException{
        String request = "type | register ; username | "+username+" ; password | "+password;
        //se o register foi aprovado
        String userCountRequest = "type | data_count ; object | user";
        //dependendo se for o primeiro ou nao fica com o perk de owner da plataforma ou user normal
        //se o register foi recusado retorna 4
        return 1; //alterar quando comunicar com o multicast dependendo da resposta
    }

    public int login(String username, String password) throws java.rmi.RemoteException{
        String request = "type | login ; username | "+username+" ; password | "+password;
        //se o login foi aprovado
        String perkRequest = "type | perks ; username | "+username;

        //se o login foi rejeitado return 4
        return 1; //alterar quando comunicar com o multicast dependendo da resposta ao perkRequest
    }

    public boolean logout(String username) throws java.rmi.RemoteException{
        //envia informação aos multicasts que este user já nao está online
        String request = "type | logout ; username | "+username;
        //return true ou false consoante a resposta
        return true;
    }

    public String search(String keyword, String object) throws java.rmi.RemoteException{
        //faz request aos multicasts para Search
        String request = "type | search ; keyword | "+keyword+" ; object | "+object;

        //envio do request

        //wait for answer

        //execute operation

        //return to client
        return request;
    }

    public String details(String object, String title) throws java.rmi.RemoteException{
        String request = "type | get_info ; object | "+object+" ; title | "+title;
        //return a info vinda do multicast
        return request;
    }

    public boolean review(String title,String user,String review,int rating) throws java.rmi.RemoteException{
        String request = "type | review ; album_title | "+title+" ; username | "+user+" ; text | "+review+" ; rate | "+rating;
        //return true ou false se bateu ou nao
        return true;
    }

    public String newGroup(String username)throws java.rmi.RemoteException{
        String request = "type | new_group ; username ! "+username;
        //return o Id do grupo que foi criado. Se deu merda return null
        return request;
    }

    public String showGroups(String username)throws java.rmi.RemoteException{
        String request = "type | groups ; username | "+username;
        //a resposta consiste em todos os grupo onde nao esta este user para este se poder juntar
        //quando chega a resposta le a lista de grupos disponivel e envia.
        //se a lista estiver vazia retorna null.
        String groups="grupo1,grupo2,grupo3"; // fica com a lista de grupos para apresentar.
        return groups;
    }

    public boolean joinGroup(String username, String group)throws java.rmi.RemoteException{
        String request = "type | join_group ; username | "+username+" ; group | "+group;
        return true;
    }

    public String changeInfo(String object, String objectName, String text, String username, String groupID)throws java.rmi.RemoteException{
        String request = "type | change_info ; object | "+object+" ; object_name | "+objectName+" ; new_info | "+text+" ; username | "+username+" ; group | "+groupID;
        //multicasts tem que verificar se esse user é editor ou owner deste grupo e depois sim fazer as alteracoes.
        //return true se foram bem feitas, return false se o user nao e editor ou owner desse grupo ou se o grupo nao existir
        //coloquei a retornar uma string para ver se o request esta a ser bem processado. alterar isto
        return request;
    }

    public String givePermissions(String perk, String username, String newUser, String groupID)throws java.rmi.RemoteException{
        String request = "type | grant_perks ; perk | "+perk+" ; username | "+username+" ; new_user | "+newUser+" ; group | "+groupID;
        //multicasts tem que verificar se esse user é editor ou owner deste grupo e depois sim fazer as alteracoes.
        //return true se foram bem feitas, return false se o user nao e editor ou owner desse grupo ou se o grupo nao existir
        //coloquei a retornar uma string para ver se o request esta a ser bem processado. alterar isto
        return request;
    }

}
