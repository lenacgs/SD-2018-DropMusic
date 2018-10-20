package rmi;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import sun.net.*;
import java.net.*;

public class RMIServer extends UnicastRemoteObject implements Services {

    private static Services s;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4322;
    private String name = "RMIServer";

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

    public void hello() throws java.rmi.RemoteException {
        System.out.println("New client connected!");

        //creates RMIWorker thread to deal with client interaction
    }

    public void ping() throws java.rmi.RemoteException {
        //esta funcao nao precisa de fazer nada pois so serve para testar a ligacao entre o primario e o secundario
    }

    public String dealWithRequest(String request) {
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
        //quando chega a resposta le a lista de grupos disponivel e envia.
        //se a lista estiver vazia retorna null.
        String groups="grupo1,grupo2,grupo3"; // fica com a lista de grupos para apresentar.
        return groups;
    }

    public boolean joinGroup(String username, String group)throws java.rmi.RemoteException{
        String request = "type | join_group ; username | "+username+" ; group | "+group;
        return true;
    }
}