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
    private CopyOnWriteArrayList<Group> groups;
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

    public CopyOnWriteArrayList<Group> getGroups(){ return groups;   }

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

        registeredUsers = new CopyOnWriteArrayList<User>();
        groups = new CopyOnWriteArrayList<Group>();
        loggedOn = new CopyOnWriteArrayList<User>();
        this.pathToObjectFiles = pathToObjectFiles;
    }

    public ObjectFile getUsersObjectFile() {
        return usersObjectFile;
    }


    public void fileHandler() {
        try {
            this.getUsersObjectFile().openRead("users.obj");

            //carregar o que esta nos ficheiros de objetos para a registeredUsers

            this.setRegisteredUsers((CopyOnWriteArrayList)this.getUsersObjectFile().readsObject());
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

    private Group findGroup(int id) {
        Iterator it = mainThread.getGroups().iterator();

        while (it.hasNext()) {
            Group aux = (Group)it.next();

            if(aux.getGroupID() == id) return aux;
        }

        return null;
    }

    private User findUser (String username){
        Iterator it = mainThread.getRegisteredUsers().iterator();

        while (it.hasNext()) {
            User aux = (User)it.next();

            if (aux.getUsername().equals(username)) return aux;
        }
        return null;
    }

    private void test(){
        System.out.println("Registered:");
        Iterator it1 = mainThread.getRegisteredUsers().iterator();
        while(it1.hasNext()){
            User aux = (User)it1.next();
            System.out.println(aux.getUsername());
        }
        System.out.println("Logged on:");
        Iterator it2 = mainThread.getLoggedOn().iterator();
        while(it2.hasNext()){
            User aux = (User)it2.next();
            System.out.println(aux.getUsername());
        }
        System.out.println("Groups:");
        Iterator it3 = mainThread.getGroups().iterator();
        while(it3.hasNext()){
            User aux = (User)it3.next();
            System.out.println(aux.getUsername());
        }
    }

    private boolean isInGroup(User user, Group group){ return (group.isUser(user)); }

    private boolean verifyPassword(User current, String password){ return current.getPassword().equals(password);}

    private String getAvailableGroups(User user){
        int counter = 0;
        String reply = "<";
        Iterator it = mainThread.getGroups().iterator();

        while(it.hasNext()) {
            Group aux = (Group)it.next();
            if(!isInGroup(user, aux)){
                if(counter++ > 0){
                    reply += ",";
                }
                reply += String.format("%d",aux.getGroupID());
            }
        }
        reply += ">";
        return reply;
    }

    private void saveFile(String filename, Object o){
        try {
            mainThread.getUsersObjectFile().openWrite(filename);
            mainThread.getUsersObjectFile().writesObject(o);
            mainThread.getUsersObjectFile().closeWrite();
        } catch (IOException e) {
            System.out.println("Could not openWrite to file " + mainThread.getPathToObjectFiles() + "/users.obj");
        }
    }

    private String translation(String message){
        String tokens[] = message.split(" ; ");
        String info[][] = new String[tokens.length][];
        for(int i = 0; i < tokens.length; i++) info[i] = tokens[i].split(" \\| ");
        if(info[0][0].equals("type")){
            String command = info[0][1];
            switch(command) {
                case "register": {

                }case "login": {
                    User currentUser;
                    String username = info[1][1];
                    String password = info[2][1];
                    if ((currentUser = findUser(username)) == null) {
                        return "type | status ; operation | failed ; message | This username doesn't exist! \n";
                    }
                    if (!verifyPassword(currentUser, password)) {
                        return "type | status ; operation | failed ; message | Wrong password! \n";
                    }

                    mainThread.getLoggedOn().add(currentUser);

                    saveFile("logged.obj", mainThread.getLoggedOn());

                    return "type | status ; operation | succeeded ; message | Welcome " + username + "! \n";

                }case "logout":{
                    String username = info[1][1];
                    User current = findUser(username);
                    if(current != null) {
                        mainThread.getLoggedOn().remove(current);
                        saveFile("logged.obj", mainThread.getLoggedOn());
                    }
                    return "type | status ; operation | succeeded \n";

                }case "perks":{
                    String username = info[1][1];
                    User current = findUser(username);
                    if(current == null){
                        return "type | status ; operation | failed \n";
                    }
                    return "type | perks ; user | " + current.getPerks() + " \n";

                }case "perks_group": {
                    User current = findUser(info[1][1]);
                    int groupID = Integer.parseInt(info[2][1]);
                    Group g = findGroup(groupID);
                    if(g == null){
                        return "type | status ; operation | failed \n";
                    }
                    if(g.isOwner(current)){
                        return "type | perks_group ; user | owner \n";
                    } else if(g.isEditor(current)){
                        return "type | perks_group ; user | editor \n";
                    }else if(g.isUser(current)) {
                        return "type | perks_group ; user | normal \n";
                    }else{
                        return "type | status ; operation | failed \n";
                    }
                }case "groups": {
                    String username = info[1][1];
                    User current = findUser(username);
                    return "type | groups ; list ; " + getAvailableGroups(current) + " \n";
                }case "new_group": {
                    String username = info[1][1];
                    User current = findUser(username);
                    mainThread.getGroups().add(new Group(current));
                    saveFile("groups.obj", mainThread.getGroups());
                }case "search": {

                }case "add_info": {
                    if (info[1][0].equals("object") && info[2][0].equals("new_info") && info[3][0].equals("username") && info.length == 4) {
                        if (true) {
                            return "type | status ; add_info | successful";
                        } else {
                            return "type | status ; add_info | failed";
                        }
                    } else {
                        return "type | status ; command | invalid";
                    }
                }case "change_info": {
                    if (info[1][0].equals("object") && info[2][0].equals("new_info") && info[3][0].equals("username") && info.length == 4) {
                        if (true) {
                            return "type | status ; change_info | successful";
                        } else {
                            return "type | status ; change_info | failed";
                        }
                    } else {
                        return "type | status ; command | invalid";
                    }
                }case "get_info": {
                    if (info[1][0].equals("object") && info[2][0].equals("title") && info.length == 3) {
                        if (true) {
                            return "type | status ; get_info | successful";
                        } else {
                            return "type | status ; get_info | failed";
                        }
                    } else {
                        return "type | status ; command | invalid";
                    }
                }case "review": {
                    if (info[1][0].equals("album_title") && info[2][0].equals("username") && info[3][0].equals("text") && info[4][0].equals("rate") && info.length == 4) {
                        if (true) {
                            return "type | status ; review | successful";
                        } else {
                            return "type | status ; review | failed";
                        }
                    } else {
                        return "type | status ; command | invalid";
                    }
                }case "grant_perks": {
                    String username = info[1][1];
                    String new_editor_username = info[2][1];
                    User current = findUser(username);
                    if(current.getPerks()<3){
                        User new_editor = findUser(new_editor_username);
                        if(new_editor == null){
                            return "type | grant_perks ; status | failed \n";
                        }
                        new_editor.setPerks(2);
                        return "type | grant_perks ; status | succeeded \n";
                    }
                    return "type | grant_perks ; status | failed \n";
                }case "test": {
                    test();
                    return "type | status, command | tested";
                }default: {
                    return "type | status ; command | invalid";
                }
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