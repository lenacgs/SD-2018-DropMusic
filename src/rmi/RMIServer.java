package rmi;

import rmiClient.Clients;

import javax.sql.rowset.serial.SQLOutputImpl;
import javax.sql.rowset.serial.SerialStruct;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

public class RMIServer extends UnicastRemoteObject implements Services {
    private boolean[] servers = {false, false, false};
    int replyServer = 0;
    private static Services s;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4323;
    private String name = "RMIServer";
    private CopyOnWriteArrayList<Clients> clientList = new CopyOnWriteArrayList<>();
    private int clientPort = 7000;
    private  MulticastChecker checker = new MulticastChecker(this);

    private RMIServer() throws RemoteException {
        MulticastChecker checker = new MulticastChecker(this);
        checker.start();
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

    public void newClient(int port, String clientIP) throws java.rmi.RemoteException{
        Clients c;
        System.out.println("port: "+port+" | clientIP: "+clientIP);
        while (true) {
            try {
                c = (Clients) LocateRegistry.getRegistry(clientIP, port).lookup("Benfica");
                System.out.println(c==null);
                break;
            } catch (ConnectException | NotBoundException exception) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(c.getUsername()+" na lista de clientes");
        clientList.add(c);
        String answer = dealWithRequest("type | get_notifications ; username | " +c.getUsername());
        String tokens[] = answer.split(" ; ");
        String mes[][] = new String[tokens.length][];
        for (int i = 0; i < tokens.length; i++) {
            mes[i] = tokens[i].split(" \\| ");
        }

        int counter = Integer.parseInt(mes[1][1]);
        if(counter > 0){
            System.out.println(mes[2][1]);
            sendNotification(mes[2][1], c.getUsername());
        }

    }

    public void ping() throws java.rmi.RemoteException {
        //esta funcao nao precisa de fazer nada pois so serve para testar a ligacao entre o primario e o secundario
    }

    public boolean[] getServers(){
        return this.servers;
    }

    protected String setReplyServer(String request, String operationType){
       if(operationType.equals("download") || operationType.equals("upload") || operationType.equals("get_musics") || operationType.equals("share_music")){
           request = "server | 1 ; " + request;
           return request;
       }
        int counter = 0, counter2 = 0;
        while(!this.servers[replyServer]){
            if(counter++ == 3){
                try{
                    Thread.sleep(5000);
                    if(++counter2 > 6){
                        return "type | " + operationType + " ; operation | failed";
                    }
                }catch(InterruptedException e){}
                counter = 0;
            }
            replyServer++;
            replyServer = replyServer%3;
        }
        request = "server | " + (replyServer++ + 1) + " ; " + request;
        replyServer = replyServer%3;
        return request;
    }

    private String dealWithRequest(String request) {
        MulticastSocket socket = null;
        String operationType = request.split(" ; ")[0].split(" \\| ")[1];
        request = setReplyServer(request, operationType);
        String message = "type | " + operationType + " ; operation | failed";
        int count = 0;
        //se não houver uma resposta em 5 segundos, há reenvio do request
        //isto acontece
        while (count < 6) {
            try {
                socket = new MulticastSocket(PORT);  // creates socket and binds it
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                socket.joinGroup(group); //joins multicast group
                socket.setLoopbackMode(false);
                //sends request to multicast address
                String length = "" + request.length();
                byte[] buffer = length.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                try{
                    Thread.sleep(100);
                }catch (InterruptedException e){}
                buffer = request.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                System.out.println("Sent to multicast address: " + request);

                //waits for answer
                buffer = new byte[8];
                packet = new DatagramPacket(buffer, buffer.length);
                socket = new MulticastSocket(4324);
                socket.joinGroup(group);
                socket.setSoTimeout(5000);
                socket.receive(packet); //bloqueante

                //answers to client
                message = new String(packet.getData(), 0, packet.getLength());
                int bufferlength = Integer.parseInt(message.trim());
                buffer = new byte[bufferlength];
                packet = new DatagramPacket(buffer, bufferlength);
                socket.receive(packet);
                message = new String(packet.getData(),0,packet.getLength());
                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message: " + message);
                break;
            }catch (SocketTimeoutException e) {
                if(count++ < 0){
                    request = "type | resend ; " + request;
                }
                continue;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        }
        return message;




    }

    public int register (String username, String password) throws java.rmi.RemoteException{
        String request = "type | register ; username | "+username+" ; password | "+password;
        String ans = dealWithRequest(request);

        //se o register não foi aprovado
        String tokens[] = ans.split(" ; ");
        if (ans.equals("type | status ; operation | failed")) {
            return 4;
        }

        String info[][] = new String[tokens.length][];
        for(int i = 0; i < tokens.length; i++) info[i] = tokens[i].split(" \\| ");
        int ret = Integer.parseInt(info[2][1]);
        if (ret == 1) return 1;
        else return 3;
    }

    public int login(String username, String password) throws java.rmi.RemoteException {
        String request = "type | login ; username | " + username + " ; password | " + password;
        String ans = dealWithRequest(request);

        //se o login foi aprovado
        String tokens[] = ans.split(" ; ");
        String mes[][] = new String[tokens.length][];
        for (int i = 0; i < tokens.length; i++) {
            mes[i] = tokens[i].split(" \\| ");
        }
        return Integer.parseInt(mes[2][1]);
    }

    public boolean logout(String username) throws java.rmi.RemoteException{
        //envia informação aos multicasts que este user já nao está online
        String request = "type | logout ; username | "+username;
        dealWithRequest(request);
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

    public String search(String username, String keyword, String object) throws java.rmi.RemoteException{
        //faz request aos multicasts para Search
        String request = "type | search ; username | " + username + " ; keyword | "+keyword+" ; object | "+object;
        String ans = dealWithRequest(request);

        String[]splitted = ans.split(" ; ");

        String count = splitted[1].split(" \\| ")[1];

        if (count.equals("0")){
            return "No "+object+"s matching your keyword(s) were found";
        }
        String toReturn = splitted[2].split(" \\| ")[1];
        return toReturn;
    }

    /* apresenta os detalhes do objeto requerido pelo client*/
    public String details(String username, String object, String title, String artist) throws java.rmi.RemoteException{
        String request = "type | get_info ; username | "+username+" ; object | "+object+" ; title | "+title+" ; artist_name | "+artist;
        String answer = dealWithRequest(request);
        if(answer.equals("type | status ; command | invalid"))
            return "Malformed request, please try again!";
        else if (answer.equals("type | get_info ; operation | failed"))
            return "Something went wrong... maybe the "+object+" you entered does not exist!";
        else{
            String[] splitted = answer.split(" ; ");
            String[][] split = new String[splitted.length][];
            int i=0;
            for(String s : splitted){
                split[i] = s.split(" \\| ");
                i++;
            }
            return split[1][1];
        }
    }

    public String details(String username, String object, String title) throws java.rmi.RemoteException{
        String request = "type | get_info ; username | "+username+" ; object | "+object+" ; title | "+title;
        String answer = dealWithRequest(request);
        if(answer.equals("type | status ; command | invalid"))
            return "Malformed request, please try again!";
        else if (answer.equals("type | get_info ; operation | failed"))
            return "Something went wrong... maybe the "+object+" you entered does not exist!";
        else{
            String[] splitted = answer.split(" ; ");
            String[][] split = new String[splitted.length][];
            int i=0;
            for(String s : splitted){
                split[i] = s.split(" \\| ");
                i++;
            }
            return split[1][1];
        }
    }

    /*escreve uma review a um album*/
    public boolean review(String title,String artist,String user,String review,int rating) throws java.rmi.RemoteException{
        String request = "type | review ; album_title | "+title+" ; artist_name | "+artist+" ; username | "+user+" ; text | "+review+" ; rate | "+rating;
        String answer = dealWithRequest(request);

        String[] splitted = answer.split(" ; ");
        String[][] split = new String[splitted.length][];
        int i=0;
        for(String s : splitted){
            split[i++] = s.split(" \\| ");
        }
        if(split[1][1].equals("succeeded"))
            return true;
        else
            return false;
    }

    /*cria um novo grupo*/
    public String newGroup(String username)throws java.rmi.RemoteException{
        String request = "type | new_group ; username | "+username;
        String answer = dealWithRequest(request);

        String[] splitted = answer.split(" ; ");
        String[][] split = new String[splitted.length][];
        int i = 0;
        for(String s : splitted){
            split[i++] = s.split(" \\| ");
        }
        if(split[2][1].equals("succeeded")){
            return split[1][1];
        }
        //return o Id do grupo que foi criado. Se deu merda return null
        return null;
    }

    /*função utilizada para apresentar todos os grupos existentes, quando o client pede para se juntar a um grupo*/
    public String showGroups(String username)throws java.rmi.RemoteException{
        String request = "type | groups ; username | "+username;
        String answer = dealWithRequest(request);
        String [] splitted = answer.split(" ; ");
        if(splitted[1].split(" \\| ")[1].equals("0")){
            return null;
        }
        String groups=splitted[2].split(" \\| ")[1]; // fica com a lista de grupos para apresentar.
        return groups;
    }

    /*apresenta todas as músicas "visíveis" para aquele user*/
    public String getMusics (String username) throws java.rmi.RemoteException{
        String request = "type | get_musics ; username | " + username;
        String ans = dealWithRequest(request);

        String tokens[] = ans.split(" ; ");
        String info[][] = new String[tokens.length][];
        for(int i = 0; i < tokens.length; i++) info[i] = tokens[i].split(" \\| ");

        int itemCount = Integer.parseInt(info[1][1]);
        if (itemCount > 0) {
            return info[2][1];
        }

        return "You don't have access to any music files!...";
    }

    /*função para partilhar um ficheiro com outros grupos, envia uma notificação para todos os users do grupo que recebeu acesso*/
    public boolean shareMusic (String username, String groupIDs, String music, String artist) throws java.rmi.RemoteException {
        String request = "type | share_music ; username | " + username + " ; musicTitle | " + music + " ; artistName | " + artist + " ; groupIDs | <" + groupIDs + ">";

        String ans = dealWithRequest(request);

        String info[] = ans.split(" ; ");
        if(Integer.parseInt(info[1].split(" \\| ")[1]) > 0) {
            String list = info[2].split(" \\| ")[1];
            for (String user : list.split(",")) {
                sendNotification("You have access to new music files!", user);
            }
        }

        return true;
    }

    /*funcao para upload de um novo ficheiro musical. avisa os servidores e um deles responde com o seu porto, onde o client se vai ligar diretamente por TCP*/
    public int uploadFile (String username, String musicTitle, String artistName) throws java.rmi.RemoteException{
        String request = "type | upload ; username | " + username + " ; music_title | " + musicTitle + " ; artistName | " + artistName;
        String ans = dealWithRequest(request);

        if (ans.equals("type | upload ; operation | failed")) {
            return -1;
        }

        String[] splitted = ans.split(" ; ");

        String port = splitted[1].split(" \\| ")[1];

        return Integer.parseInt(port);
    }

    public int downloadFile (String username, String musicTitle, String artistName) throws java.rmi.RemoteException {
        String request = "type | download ; username | " + username + " ; music_title | " + musicTitle + " ; artistName | " + artistName;
        String ans = dealWithRequest(request);

        if (ans.equals("type | download ; operation | failed"))
            return -1;
        String[] splitted = ans.split(" ; ");

        String port = splitted[1].split(" \\| ")[1];

        System.out.println("--"+port+"--");

        return Integer.parseInt(port);
    }

    public String joinGroup(String username, String group)throws java.rmi.RemoteException{
        String request = "type | join_group ; username | "+username+" ; group | "+group;
        String answer = dealWithRequest(request);
        String aux[] = answer.split(" ; ");
        if(aux[1].equals("operation | failed")){
            String[] reply = aux[2].split(" \\| ");
            return reply[1];
        }
        else{
            String [] splitted = answer.split(" ; ");
            String [] owners = splitted[2].split(" \\| ");

            String message = "You have a new request to join group: "+group;
            for(String s : owners[1].split(",")){
                System.out.println("Enviar notificação para "+s);
                sendNotification(message,s);
            }
            return "success";
        }
    }

    public String changeInfo(String username, String groups, String type, String s1, String s2, String s3, String s4) throws java.rmi.RemoteException{
        if (type.equals("music")) {
            String title = s1, artist = s2, genre = s3, duration = s4;

            String request = "type | change_info ; object | music ; username | " + username + " ; groups | " + groups +  " ; title | " + title + " ; artist | " + artist + " ; genre | " + genre
                    + " ; duration | " + duration;

            String ans = dealWithRequest(request);

            String aux[] = ans.split(" ; ");

            if (aux[1].equals("operation | failed")){
                String[] reply = aux[2].split(" \\| ");
                return reply[1];
            }else if(aux[1].equals("command | invalid")){
                return "Something went wrong!";
            }else{
                return "Music successfully edited!";
            }
        }

        else if (type.equals("artist")) {
            String name = s1, description = s2, concerts = s3, genre = s4;

            String request = "type | change_info ; object | artist ; username | " + username + " ; name | " + name + " ; description | " + description + " ; concerts | " + concerts + " ; genre | " + genre + " \n";

            String ans = dealWithRequest(request);

            String aux[] = ans.split(" ; ");

            if (aux[1].equals("operation | failed")){
                String[] reply = aux[2].split(" \\| ");
                return reply[1];
            }else if(aux[1].equals("command | invalid")){
                return "Something went wrong!";
            }else{
                return "Artist successfully edited!";
            }
        }
        return "Something went wrong!";
    }

    public String changeInfo(String username, String groupIDs, String artist, String title, String musics, String year, String publisher, String genre, String description) throws java.rmi.RemoteException{
        String request = "type | change_info ;  username | " + username + " ; groups | "+groupIDs+" ; artist | " + artist + " ; title | " + title + " ; musics | " + musics + " ; year | " + year + " ; publisher | "
                + publisher + " ; genre | " + genre + " ; description | " + description;

        String ans = dealWithRequest(request);

        String aux[] = ans.split(" ; ");

        if (aux[1].equals("operation | failed")){
            String[] reply = aux[2].split(" \\| ");
            return reply[1];
        }else if(aux[1].equals("command | invalid")){
            return "Something went wrong!";
        }else{
            return "Album successfully edited!";
        }
    }


    public boolean givePermissions(String perk, String username, String newUser, String groupID) throws java.rmi.RemoteException {
        String request = "type | grant_perks ; perk | " + perk + " ; username | " + username + " ; new_user | " + newUser + " ; group | " + groupID;
        //multicasts tem que verificar se esse user é editor ou owner deste grupo e depois sim fazer as alteracoes.
        //return true se foram bem feitas, return false se o user nao e editor ou owner desse grupo ou se o grupo nao existir
        //coloquei a retornar uma string para ver se o request esta a ser bem processado. alterar isto

        String ans = dealWithRequest(request);
        if (ans.equals("type | grant_perks ; operation | succeeded")) {
            String message = "Your permissions on group " + groupID + " have been updated to " + perk +"!";
            sendNotification(message, newUser);
            return true;
        }
        return false;
    }

    private void sendNotification(String message, String user) {

        System.out.println("Sending notification :" + message + ": to user " + user);
        for (Clients c : clientList) {
            try {
                if (c.getUsername() == null)
                    continue;
                if (c.getUsername().equals(user)) {
                    c.notification(message);
                    return;
                }
            } catch (RemoteException e) {
                clientList.remove(c);
                System.out.println("saiu da lista");
            }
        }
        System.out.println("Client isn't logged on, saving it for later");
        //se chegou ao fim do for e não encontrou o user, significa que ele deu logout
        //então, envia mensagem para o server para ele guardar a notificação para enviar quando o user se loggar de novo

        String request = "type | notification ; username | " + user + " ; message | " + message;
        System.out.println(request);

        dealWithRequest(request);
    }

    public String addInfo(String username, String groups, String type, String s1, String s2, String s3, String s4) { //used for musics and artist

        if (type.equals("music")) {
            String title = s1, artist = s2, genre = s3, duration = s4;

            String request = "type | add_music ; username | " + username + " ; groups | " + groups + " ; title | " + title + " ; artist | " + artist + " ; genre | " + genre
                    + " ; duration | " + duration;

            String ans = dealWithRequest(request);

            String aux[] = ans.split(" ; ");

            if (aux[1].equals("operation | failed")){
                String[] reply = aux[2].split(" \\| ");
                return reply[1];
            }else if(aux[1].equals("command | invalid")){
                return "Something went wrong!";
            }else{
                return "New song successfully added!";

            }
        }

        else if (type.equals("artist")) {
            String name = s1, description = s2, concerts = s3, genre = s4;

            String request = "type | add_artist ; username | " + username + " ; groups | " + groups + " ; name | " + name + " ; description | " + description + " ; concerts | " + concerts + " ; genre | " + genre;

            String ans = dealWithRequest(request);
            String aux[] = ans.split(" ; ");

            if (aux[1].equals("operation | failed")){
                String[] reply = aux[2].split(" \\| ");
                return reply[1];
            }else if(aux[1].equals("command | invalid")){
                return "Something went wrong!";
            }else{
                return "New artist successfully added!";
            }
        }
        return "Something went wrong! :(";
    }

    public String addInfo(String username, String groupIDs, String artist, String title, String musics, String year, String publisher, String genre, String description) { //used for albums
        String request = "type | add_album ; username | " + username + " ; groups | "+groupIDs+" ; artist | " + artist + " ; title | " + title + " ; musics | " + musics + " ; year | " + year + " ; publisher | "
                + publisher + " ; genre | " + genre + " ; description | " + description;

        String ans = dealWithRequest(request);

        String aux[] = ans.split(" ; ");

        if (aux[1].equals("operation | failed")){
            String[] reply = aux[2].split(" \\| ");
            return reply[1];
        }else if(aux[1].equals("command | invalid")){
            return "Something went wrong!";
        }else{
            return "New album successfully added!";
        }
    }

    public String showRequests(String username) throws java.rmi.RemoteException{
        String request = "type | get_requests ; username | "+username;
        String answer = dealWithRequest(request);
        String[] splitted = answer.split(" ; ");
        if(splitted[2].split(" \\| ")[1].equals("empty"))
            return null;
        return splitted[2].split(" \\| ")[1].replaceAll("^[,\\s]+", "");
    }

    public boolean manageRequests(String username, String newUser, String groupID, String toDo)throws java.rmi.RemoteException{
        String request = "type | manage_request ; username | "+username+" ; new_user | "+newUser+" ; groupID | "+groupID+" ; request | "+toDo;

        String answer = dealWithRequest(request);

        if(answer.equals("type | manage_request ; operation | succeeded ; operation | accept")) {
            sendNotification("Your request to join group "+groupID+" has been accepted. Welcome!", newUser);
            return true;
        }
        else if (answer.equals("type | manage_request ; operation | succeeded ; operation | decline")) {
            sendNotification("Your request to join group "+groupID+" has been rejected", newUser);
            return true;
        }
        else
            return false;

    }

}


class MulticastChecker extends Thread{
    private int PORT = 4360;
    RMIServer rmiServer;
    int[] servers = {0,0,0};
    int serverCounter = 0;

    public MulticastChecker(RMIServer rmiserver){
        super("RMIChecker");
        this.rmiServer = rmiserver;
    }

    public void run(){
        System.out.println("Heartbeat receiver is up!");
        DatagramSocket socket = null;
        try{
            int currentRequest, serverNumber;
            socket = new DatagramSocket(PORT);
            byte[] buffer = new byte[32];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while(true){
                try {
                    socket.setSoTimeout(1000);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Received heartbeat from server " + message);
                    String aux[] = message.split(" ; ");
                    String info[][] = new String[2][];
                    info[0] = aux[0].split(" \\| ");
                    info[1] = aux[1].split( " \\| ");
                    serverNumber = Integer.parseInt(info[0][1].trim());
                    currentRequest = Integer.parseInt(info[1][1].trim());
                    if((rmiServer.getServers()[serverNumber-1]) && this.servers[serverNumber-1] < 28){
                        rmiServer.getServers()[serverNumber-1] = false;
                    }
                    if (!rmiServer.getServers()[serverNumber - 1]) {
                        if(serverCounter++ > 0){
                            String request = "type | config ; currentRequest | " + currentRequest;
                            request = rmiServer.setReplyServer(request, "config");
                            MulticastSocket multicastSocket = null;
                            try{
                                multicastSocket = new MulticastSocket();
                                InetAddress address = InetAddress.getByName("224.0.224.0");
                                String length = request.length() + "";
                                buffer = length.getBytes();
                                packet = new DatagramPacket(buffer, buffer.length, address, 4323);
                                multicastSocket.send(packet);
                                try{
                                    Thread.sleep(100);
                                }catch (InterruptedException e){

                                }
                                System.out.println("Sent to multicast: " + length);
                                buffer = request.getBytes();
                                packet = new DatagramPacket(buffer, buffer.length, address, 4323);
                                multicastSocket.send(packet);
                                System.out.println("Sent to multicast: " + request);
                            }catch(IOException e){
                                e.printStackTrace();
                            }finally {
                                multicastSocket.close();
                            }
                        }
                        rmiServer.getServers()[serverNumber-1] = true;
                        System.out.println("Server " + serverNumber + " is up!");

                    }
                    this.servers[serverNumber -1] = 0;
                }catch(SocketTimeoutException e){
                    for(int i = 0; i < rmiServer.getServers().length; i++){
                        if(rmiServer.getServers()[i]){
                            if(++this.servers[i] > 35){
                                System.out.println("Server " + (i+1) + " is dead!");
                                rmiServer.getServers()[i] = false;
                                this.serverCounter--;
                            }
                        }
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            socket.close();
        }
    }
}