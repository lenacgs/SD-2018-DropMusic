package Multicast;

import Interface.*;
import FileHandling.*;
import java.net.MulticastSocket;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread {
    private ObjectFile usersObjectFile; //file for registered users
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4322;
    private CopyOnWriteArrayList<User> registeredUsers;
    private CopyOnWriteArrayList<User> loggedOn;
    private String pathToObjectFiles;

    public void setUsersObjectFile(ObjectFile usersObjectFile) {
        this.usersObjectFile = usersObjectFile;
    }

    public String getMULTICAST_ADDRESS() {
        return MULTICAST_ADDRESS;
    }

    public void setMULTICAST_ADDRESS(String MULTICAST_ADDRESS) {
        this.MULTICAST_ADDRESS = MULTICAST_ADDRESS;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public CopyOnWriteArrayList<User> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(CopyOnWriteArrayList<User> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public CopyOnWriteArrayList<User> getLoggedOn() {
        return loggedOn;
    }

    public void setLoggedOn(CopyOnWriteArrayList<User> loggedOn) {
        this.loggedOn = loggedOn;
    }

    public String getPathToObjectFiles() {
        return pathToObjectFiles;
    }

    public void setPathToObjectFiles(String pathToObjectFiles) {
        this.pathToObjectFiles = pathToObjectFiles;
    }

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer(args[0], args[1]);
        System.setProperty("java.net.preferIPv4Stack", "true");
        server.start();
    }

    public MulticastServer(String name, String pathToObjectFiles){
        super(name);
        usersObjectFile = new ObjectFile();
        registeredUsers = new CopyOnWriteArrayList<>();
        loggedOn = new CopyOnWriteArrayList<>();
        this.pathToObjectFiles = pathToObjectFiles;
    }

    public ObjectFile getUsersObjectFile() {
        return usersObjectFile;
    }

    public void fileHandler() {
        try {
            this.getUsersObjectFile().openRead("users.obj");

            //carregar o que esta nos ficheiros de objetos para a registeredUsers

            User aux = (User)this.getUsersObjectFile().readsObject();
            while (aux != null) {
                this.registeredUsers.add(aux);
                aux = (User)this.getUsersObjectFile().readsObject();
            }
            this.getUsersObjectFile().closeRead();
        } catch (IOException e) {/*if there's an exception => empty files => do nothing*/}

        Iterator it = registeredUsers.iterator();

        System.out.println("File has been read... Registered users:");

        while (it.hasNext()){
            User aux = (User)it.next();
            System.out.println(aux.getUsername());
        }


    }


    public void run(){
        MulticastSocket socket = null;
        System.out.println(this.getName() + " running...");

        fileHandler();

        try{
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            while(true){
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());

                //creates new thread for handling the new request
                requestHandler newRequest = new requestHandler(message, this);
                newRequest.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            socket.close();
        }
    }
}


class requestHandler extends Thread{
    private String request;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private MulticastServer mainThread;

    public requestHandler(String request, MulticastServer mainThread){
        super("Request");
        this.request = request;
        this.mainThread = mainThread;
    }

    private User findUser (String username){
        Iterator it = mainThread.getRegisteredUsers().iterator();

        while (it.hasNext()) {
            User aux = (User)it.next();

            if (aux.getUsername().equals(username)) return aux;
        }
        return null;
    }

    private boolean register(){
        return false;
    }

    private boolean login(){
        return false;
    }

    private boolean logout(){
        return false;
    }

    private boolean perks(){
        return false;
    }

    private boolean perks_group(){
        return false;
    }

    private boolean search(){
        return false;
    }

    private boolean add_info(){
        return false;
    }

    private boolean change_info(){
        return false;
    }

    private boolean review(){
        return false;
    }

    private boolean grant_perks(){
        return false;
    }

    private String translation(String message){
        String tokens[] = message.split(" ; ");
        String info[][] = new String[tokens.length][];
        for(int i = 0; i < tokens.length; i++) info[i] = tokens[i].split(" \\| ");
        if(info[0][0].equals("type")){
            String command = info[0][1];
            switch(command){
                case "register":
                    String username = info[1][1];
                    String password = info[2][1];
                    if (findUser(username) != null) { //já existe este username
                        return "type | status ; operation | failed ; message | This username already exists... Try a different one! \n";
                    }

                    //else, register the new user

                    User newUser = new User(username, password);

                    mainThread.getRegisteredUsers().add(newUser);

                    try {
                        mainThread.getUsersObjectFile().openWrite("users.obj");
                    }catch (IOException e) {System.out.println("Could not openWrite to file " + mainThread.getPathToObjectFiles() + "/users.obj");}

                    mainThread.getUsersObjectFile().writesObject(newUser);

                    mainThread.getUsersObjectFile().closeWrite();

                    return "type | status ; operation | succeeded ; message | User registered! \n";
                case "login":
                    if(info[1][0].equals("username") && info[2][0].equals("password") && info.length == 3) {
                        if(true) { //se o login funcionar
                            return "type | status ; login | succeeded ; perks | ";
                        }else{
                            return "type | status ; login | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }
                case "logout":
                    if(info[1][0].equals("username") && info.length == 2){
                        if(true){ //se o logout funcionar
                            return "type | status ; logout | succeeded";
                        }else{
                            return "type | status ; logout | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }
                case "perks":
                    if(info[1][0].equals("username") && info.length == 2){
                        if(true){  //se o perks funcionar
                            return "type | perks ; user | ";
                        }else{
                            return "type | status ; perks | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }
                case "perks_group":
                    if(info[1][0].equals("username") && info[2][0].equals("groupID") && info.length == 3){
                        if(true){
                            return "type | perks_group ; user | ";
                        }else{
                            return "type | status ; perks_group | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }
                case "search":
                    if(info[1][0].equals("keyword") && info[2][0].equals("object") && info.length == 3){
                        if(true){
                            return "type | " + " ; item_count | " ;
                        }else{
                            return "type | status ; perks_group | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }

                case "add_info":
                    if(info[1][0].equals("object") && info[2][0].equals("new_info") && info[3][0].equals("username") && info.length == 4){
                        if(true){
                            return "type | status ; add_info | successful";
                        }else{
                            return "type | status ; add_info | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }
                case "change_info":
                    if(info[1][0].equals("object") && info[2][0].equals("new_info") && info[3][0].equals("username") && info.length == 4){
                        if(true){
                            return "type | status ; change_info | successful";
                        }else{
                            return "type | status ; change_info | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }
                case "get_info":
                    if(info[1][0].equals("object") && info[2][0].equals("title") && info.length == 3){
                        if(true){
                            return "type | status ; get_info | successful";
                        }else{
                            return "type | status ; get_info | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }
                case "review":
                    if(info[1][0].equals("album_title") && info[2][0].equals("username")  && info[3][0].equals("text") && info[4][0].equals("rate") && info.length == 4){
                        if(true){
                            return "type | status ; review | successful";
                        }else{
                            return "type | status ; review | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }
                case "grant_perks":
                    if(info[1][0].equals("username") && info[2][0].equals("groupID") && info.length == 3){
                        if(true){
                            return "type | status ; grant_perks | succeeded";
                        }else{
                            return "type | status ; perks_group | failed";
                        }
                    }else{
                        return "type | status ; command | invalid";
                    }
                default:
                    return "type | status ; command | invalid";
            }
        }
        return "type | status ; command | invalid";
    }



    public void run(){
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            byte buffer[] = translation(this.request).getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}