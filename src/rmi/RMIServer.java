package rmi;

import rmiClient.Clients;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

import java.net.*;
import java.util.ArrayList;

public class RMIServer extends UnicastRemoteObject implements Services {

    private static Services s;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4322;
    private String name = "RMIServer";
    private ArrayList<Clients> clientList = new ArrayList<>();
    private int clientPort = 7000;

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
        /*Creates registry of new RMI server on port 7000
        If AccessException happens => prints message
        If ExportException happens => There is already a RMI server, then changes to backup RMI server*/
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
        /*This function is executed when a new RMI server is created but there's already a main one*/

        try {
            s = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting"); // liga-se ao RMI Primário
            System.out.println("Backup RMI ready!");
        } catch (ConnectException | NotBoundException e) {
            System.out.println("Attempting to become primary RMI server...");
            createRegistry(); //se não der, é porque entretanto deu cagada no primário e tenta ser ele o primário
        }
        int timer = 1;
        //iniciam os pings: ao fim de 5 pings, se não tiver obtido resposta do RMI primário, torna-se primário
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

    public int hello() throws java.rmi.RemoteException {
        clientPort++;
        return clientPort;
    }

    public void newClient(int port) throws java.rmi.RemoteException{
        Clients c;
        while (true) {
            try {
                c = (Clients) LocateRegistry.getRegistry(port).lookup("Benfica");
                break;
            } catch (ConnectException | NotBoundException exception) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        clientList.add(c);
    }

    public void ping() throws java.rmi.RemoteException {
        //esta funcao nao precisa de fazer nada pois so serve para testar a ligacao entre o primario e o secundario
    }

    private String dealWithRequest(String request) {
        MulticastSocket socket = null;
        String message = null;

        try {
            socket = new MulticastSocket(PORT);  // creates socket and binds it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            System.out.println("hello");
            socket.joinGroup(group); //joins multicast group
            socket.setLoopbackMode(false);
            System.out.println("This is my address: " + socket.getInterface().getHostAddress());
            //sends request to multicast address
            byte[] buffer = request.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            System.out.println("Sent to multicast address: " + request);

            //waits for answer
            buffer = new byte[256];
            packet = new DatagramPacket(buffer, buffer.length);
            socket = new MulticastSocket(4321);
            socket.joinGroup(group);
            socket.receive(packet); //bloqueante

            //callback to client
            message = new String(packet.getData(), 0, packet.getLength());

            String tokens[] = message.split(" ; ");
            String info[][] = new String[tokens.length][];
            for(int i = 0; i < tokens.length; i++) info[i] = tokens[i].split(" \\| ");

            System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            System.out.println("am i doing this?");
            return message;
        }
    }

    public int register (String username, String password) throws java.rmi.RemoteException{
        String request = "type | register ; username | "+username+" ; password | "+password;
        String ans = this.dealWithRequest(request);

        //se o register não foi aprovado
        if (ans.equals("type | status ; register | failed\n")) {
            return 4;
        }
        String tokens[] = ans.split(" ; ");
        String info[][] = new String[tokens.length][];
        for(int i = 0; i < tokens.length; i++) info[i] = tokens[i].split(" \\| ");
        int ret = Integer.parseInt(info[2][1]);
        if (ret == 1) return 1;
        else return 3;
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
        for(Clients c : clientList ){
            try {
                if (c.getUsername().equals(username))
                    clientList.remove(c);
            }catch (RemoteException e){
                clientList.remove(c);
            }
        }
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

    public boolean uploadFile (String username, String musicTitle) throws java.rmi.RemoteException{
        String request = "type | upload ; username | " + username + " ; music_title | " + musicTitle + " \n";
        String ans = dealWithRequest(request);

        //a resposta é sempre positiva... o user pesquisou por músicas que apenas ele tinha acesso, só o pôde fazer depois de ter feito login, portanto tem sempre permissão

        return true;
    }

    public boolean joinGroup(String username, String group)throws java.rmi.RemoteException{
        String request = "type | join_group ; username | "+username+" ; group | "+group;
        //processar resposta e enviar notificação ao(s) owner(s) se online
        String message = "You have a new request to join group: "+group;
        //sendNotification(message,user);
        return true;
    }

    public String changeInfo(String object, String objectName, String text, String username, String groupID)throws java.rmi.RemoteException{
        String request = "type | change_info ; object | "+object+" ; object_name | "+objectName+" ; new_info | "+text+" ; username | "+username+" ; group | "+groupID;
        //multicasts tem que verificar se esse user é editor ou owner deste grupo e depois sim fazer as alteracoes.
        //return true se foram bem feitas, return false se o user nao e editor ou owner desse grupo ou se o grupo nao existir
        //coloquei a retornar uma string para ver se o request esta a ser bem processado. alterar isto

        //processar resposta e enviar notificação a toda a gente do grupo
        request = "type | group_users ; group | "+groupID;

        //for(percorre lista de users)
        //if(user.equals(username) continue; //para nao enviar notificacao a quem fez as alteracoes
        //sendNotification(message,user);

        return request;
    }

    public String givePermissions(String perk, String username, String newUser, String groupID)throws java.rmi.RemoteException{
        String request = "type | grant_perks ; perk | "+perk+" ; username | "+username+" ; new_user | "+newUser+" ; group | "+groupID;
        //multicasts tem que verificar se esse user é editor ou owner deste grupo e depois sim fazer as alteracoes.
        //return true se foram bem feitas, return false se o user nao e editor ou owner desse grupo ou se o grupo nao existir
        //coloquei a retornar uma string para ver se o request esta a ser bem processado. alterar isto
        String message = "Your permitions on group "+groupID+" have been updated";
        sendNotification(message, newUser);
        //processar a resposta e chamar o metodo para enviar notificacao ao cliente

        return request;
    }

    private void sendNotification(String message, String user){
        for(Clients c : clientList ){
            try {
                if(c.getUsername()==null)
                    continue;
                if (c.getUsername().equals(user))
                    c.notification(message);
            }catch (RemoteException e){
                clientList.remove(c);
            }
        }
    }

}