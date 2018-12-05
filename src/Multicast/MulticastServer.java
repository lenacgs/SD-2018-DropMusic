package Multicast;

import Interface.*;
import FileHandling.*;


import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;
import java.io.IOException;
import java.nio.Buffer;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import static java.lang.Math.toIntExact;

public class MulticastServer extends Thread {
    private ObjectFile usersObjectFile; //file for registered users
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int serverNumber;
    private int serverCounter;
    private int replyServer;
    private int PORT = 4323;
    private CopyOnWriteArrayList<Request> requests;
    private CopyOnWriteArrayList<String> writeRequests;
    private CopyOnWriteArrayList<User> loggedOn;
    private CopyOnWriteArrayList<Group> groups;
    private String pathToObjectFiles;
    public int[] replyOrder = new int[]{0,0,0};
    private String name;
    private ObjectFile groupObjectFile;
    private ObjectFile songsObjectFile;
    private ObjectFile artistsObjectFile;
    private ObjectFile albumsObjectFile;
    private ObjectFile writeRequestsObjectFile;
    private CopyOnWriteArrayList<Music> songs;
    private CopyOnWriteArrayList<Artist> artists;
    private CopyOnWriteArrayList<Album> albums;

    public void setGroups(CopyOnWriteArrayList<Group> groups) {
        this.groups = groups;
    }

    public int getCurrentRequest(){
        return this.writeRequests.size();
    }

    public void addRequest(Request request){
        this.requests.add(request);
    }

    public int getServerNumber(){
        return this.serverNumber;
    }

    public int getReplyServer(){
        return this.replyServer;
    }

    public CopyOnWriteArrayList<String> getwriteRequests(){
        return this.writeRequests;
    }

    public void setServerCounter(int serverCounter){
        this.serverCounter = serverCounter;
    }

    public int getServerCounter(){
        return this.serverCounter;
    }

    public void setReplyServer(int replyServer){
        this.replyServer = replyServer;
    }

    public ObjectFile getGroupObjectFile() {
        return groupObjectFile;
    }

    public ObjectFile getWriteRequestsObjectFile(){return  writeRequestsObjectFile;}

    public void setGroupObjectFile(ObjectFile groupObjectFile) {
        this.groupObjectFile = groupObjectFile;
    }

    public ObjectFile getSongsObjectFile() {
        return songsObjectFile;
    }

    public void setSongsObjectFile(ObjectFile songsObjectFile) {
        this.songsObjectFile = songsObjectFile;
    }

    public ObjectFile getArtistsObjectFile() {
        return artistsObjectFile;
    }

    public void setArtistsObjectFile(ObjectFile artistsObjectFile) {
        this.artistsObjectFile = artistsObjectFile;
    }

    public ObjectFile getAlbumsObjectFile() {
        return albumsObjectFile;
    }

    public void setAlbumsObjectFile(ObjectFile albumsObjectFile) {
        this.albumsObjectFile = albumsObjectFile;
    }

    public CopyOnWriteArrayList<Music> getSongs() {
        return songs;
    }

    public void setSongs(CopyOnWriteArrayList<Music> songs) {
        this.songs = songs;
    }

    public CopyOnWriteArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(CopyOnWriteArrayList<Artist> artists) {
        this.artists = artists;
    }

    public CopyOnWriteArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(CopyOnWriteArrayList<Album> albums) {
        this.albums = albums;
    }

    public void setWriteRequests(CopyOnWriteArrayList<String> writeRequests) { this.writeRequests = writeRequests;    }

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

    public CopyOnWriteArrayList<User> getLoggedOn() {
        return loggedOn;
    }

    public void setLoggedOn(CopyOnWriteArrayList<User> loggedOn) {
        this.loggedOn = loggedOn;
    }

    public void addwriteRequest(String request){
        this.writeRequests.add(request);
    }

    public String getPathToObjectFiles() {
        return pathToObjectFiles;
    }

    public void setPathToObjectFiles(String pathToObjectFiles) {
        this.pathToObjectFiles = pathToObjectFiles;
    }

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer(args[0], args[1]);
        server.start();
    }

    public MulticastServer(String name, String pathToObjectFiles){
        super(name);
        this.usersObjectFile = new ObjectFile();
        this.groupObjectFile = new ObjectFile();
        this.artistsObjectFile = new ObjectFile();
        this.songsObjectFile = new ObjectFile();
        this.albumsObjectFile = new ObjectFile();
        this.writeRequestsObjectFile = new ObjectFile();
        this.groups = new CopyOnWriteArrayList<Group>();
        this.artists = new CopyOnWriteArrayList<>();
        this.songs = new CopyOnWriteArrayList<>();
        this.loggedOn = new CopyOnWriteArrayList<User>();
        this.writeRequests = new CopyOnWriteArrayList<>();
        this.pathToObjectFiles = pathToObjectFiles;
        this.name = name;
        this.albums = new CopyOnWriteArrayList<>();
        String aux = name.replace("MulticastServer", "");
        this.serverNumber = Integer.parseInt(aux);
        this.requests = new CopyOnWriteArrayList<>();
        this.serverCounter = 1;
        this.replyServer = this.serverNumber%3;
        this.replyOrder[this.getReplyServer()] = 1;
    }

    public void config(){
        int CONFIG_PORT = 4330;
        MulticastSocket socket = null;
        try{
            socket = new MulticastSocket();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            String message = "type | config ; server_number | " + this.getServerNumber() + " ; requestNumber | " + this.getCurrentRequest();
            byte buffer[] = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
            buffer = new byte[256];
            socket = new MulticastSocket(CONFIG_PORT);
            socket.joinGroup(group);
            packet = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(1000);
            socket.receive(packet);
            String reply = new String(packet.getData(), 0, buffer.length);
            String aux[] = reply.split(" ; ");
            String info[][] = new String[aux.length][];
            for(int i = 0; i < aux.length; i++){
                info[i] = aux[i].split(" \\| ");
            }
            int serverCount = 0;
            aux = info[1][1].split(",");
            for(int  i = 0; i < 3; i++){
                this.replyOrder[i] = Integer.parseInt(aux[i]);
                if(Integer.parseInt(aux[i])==1){
                    serverCount++;
                }
            }
            this.setServerCounter(serverCount);
            int replyServer = Integer.parseInt(info[2][1]);
            this.setReplyServer(replyServer);
            int n = Integer.parseInt(info[3][1]);
            if(n > 0){
                aux = reply.split(" ; ");
                String replaced = "";
                for(int i = 0; i < 4; i++){
                    replaced += aux[i];
                    replaced += " ; ";
                }
                replaced += "requests | ";
                reply = reply.replace(replaced, "");
                String[] requests = reply.split(",");
                requestHandler newRequest = new requestHandler(new Request(requests[0], replyServer), this);
                for(int i = 0; i < n; i++){
                    newRequest.translation(requests[i]);
                }
            }
        }catch (SocketTimeoutException e1){
        } catch (IOException e) {

        } finally{
            socket.close();
        }
    }

    public ObjectFile getUsersObjectFile() {
        return usersObjectFile;
    }


    public void fileHandler() {
        try {
            this.getGroupObjectFile().openRead("src/Multicast/"+getName()+"/groups.obj");
            this.setGroups((CopyOnWriteArrayList)this.getGroupObjectFile().readsObject());
            this.getGroupObjectFile().closeRead();
        } catch (IOException e) {/*if there's an exception => empty files => do nothing*/}
        try {
            this.getSongsObjectFile().openRead("src/Multicast/"+getName()+"/musics.obj");
            this.setSongs((CopyOnWriteArrayList)this.getSongsObjectFile().readsObject());
            this.getSongsObjectFile().closeRead();
        } catch (IOException e) {/*if there's an exception => empty files => do nothing*/}
        try {
            this.getAlbumsObjectFile().openRead("src/Multicast/"+getName()+"/albums.obj");
            this.setAlbums((CopyOnWriteArrayList)this.getAlbumsObjectFile().readsObject());
            this.getAlbumsObjectFile().closeRead();
        } catch (IOException e) {/*if there's an exception => empty files => do nothing*/}
        try {
            this.getArtistsObjectFile().openRead("src/Multicast/"+getName()+"/artists.obj");
            this.setArtists((CopyOnWriteArrayList)this.getArtistsObjectFile().readsObject());
            this.getArtistsObjectFile().closeRead();
        } catch (IOException e) {/*if there's an exception => empty files => do nothing*/}
        try {
            this.getWriteRequestsObjectFile().openRead("src/Multicast/" + this.getName() + "/writeRequests.obj");
            this.setWriteRequests((CopyOnWriteArrayList)this.getWriteRequestsObjectFile().readsObject());
            this.getWriteRequestsObjectFile().closeRead();
        }catch (IOException e){/*if there's an exception => empty files => do nothing*/}
    }

    public Request findRequest(String message){
        for(int i = requests.size()-1, k = 0; i >= 0 && k < 10; i--,k++){
            if(requests.get(i).compare(message)){
                return requests.get(i);
            }
        }
        return null;
    }

    public void run(){
        fileHandler();
        int counter = 0;
        System.out.print("Waiting for configuration");
        config();
        MulticastSocket socket = null;
        System.out.println("\n" + this.getName() + " running...");

        try{
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            socket.setLoopbackMode(false);


            while(true){ //receiving
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + " : " + packet.getPort() + " with message: " + message);
                //creates new thread for handling the new request
                requestHandler newRequest = new requestHandler(new Request(message, replyServer), this);
                newRequest.start();
                }

        }catch(IOException e){
            e.printStackTrace();
        }finally{
            socket.close();
        }


    }
}



class requestHandler extends Thread{ //handles request and sends answer back to RMI
    private Request request;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4324;
    private MulticastServer mainThread;


    public requestHandler(Request request, MulticastServer mainThread){
        super("Request");
        this.request = request;
        this.mainThread = mainThread;
    }

    public Group findGroup(int id) {
        Iterator it = mainThread.getGroups().iterator();
        while (it.hasNext()) {
            Group aux = (Group)it.next();
            if(aux.getGroupID() == id) return aux;
        }

        return null;
    }


    private CopyOnWriteArrayList<Artist> searchForArtist(String keyword, User user){
        Iterator it = mainThread.getArtists().iterator();

        CopyOnWriteArrayList<Artist> toReturn = new CopyOnWriteArrayList<>();
        int i=0;

        while (it.hasNext()) {
            Artist aux = (Artist)it.next();
            if((aux.getName().contains(keyword) || aux.getGenre().contains(keyword) || aux.checkIfContains(keyword)) &&  verifyGroups(aux.getGroups(), user.getDefaultShareGroups())){
                toReturn.add(aux);
                i++;
            }
        }
        return toReturn;
    }

    private Artist findArtist (String artistName) {
        for(Artist a : this.mainThread.getArtists()){
             if(a.getName().equals(artistName)){
                 return a;
             }
        }
        return null;
    }

    private User findUser (String username){
        Group g = findGroup(1);
        if(g != null){
            Iterator it = g.getUsers().iterator();

            while (it.hasNext()) {
                User aux = (User)it.next();

                if (aux.getUsername().equals(username)) return aux;
            }
            return null;
        }else{
            return null;
        }

    }

    private void test(){
        System.out.println("Registered:");
        Iterator it1 = findGroup(1).getUsers().iterator();
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
            Group aux = (Group)it3.next();
            System.out.println(aux.getGroupID());
            aux.printUsers();
        }
        System.out.println("Musics:");
        Iterator it4 = mainThread.getSongs().iterator();
        while(it4.hasNext()){
            Music aux = (Music)it4.next();
            System.out.println(aux.getTitle());
        }
        System.out.println("Artists:");
        it3 = mainThread.getArtists().iterator();
        while(it3.hasNext()){
            Artist aux = (Artist) it3.next();
            System.out.println(aux.getName());
        }
        System.out.println("Albums:");
        it3 = mainThread.getAlbums().iterator();
        while(it3.hasNext()){
            Album aux = (Album)it3.next();
            System.out.println(aux.getTitle());
        }
    }

    private boolean isInGroup(User user, Group group){ return (group.isUser(user)); }

    private boolean verifyPassword(User current, String password){ return current.getPassword().equals(password);}

    private String getAvailableGroups(User user){
        int counter = 0;
        String groups="";
        String reply;
        Iterator it = mainThread.getGroups().iterator();

        while(it.hasNext()) {
            Group aux = (Group)it.next();
            aux.printUsers();
            if (!aux.isUser(user.getUsername())) {
                if (counter++ > 0) {
                    groups += ",";
                }
                groups += aux.getGroupID();
            }
        }
        reply = counter+" ; list | "+groups;
        return reply;
    }

    public void saveFile(String filename, Object o){
        try {
            mainThread.getUsersObjectFile().openWrite(filename);
            mainThread.getUsersObjectFile().writesObject(o);
            mainThread.getUsersObjectFile().closeWrite();
        } catch (IOException e) { }
    }

    //verifica se a lista de grupos onde o user está coincide com a lista de grupos que têm acesso à música
    private boolean verifyGroups(CopyOnWriteArrayList<Integer> musicGroups, CopyOnWriteArrayList<Group> userGroups){
        //userGroups is the groups a certain user is in (user.getDefaultShareGroups)
        //musicGroups is the groups where a certain music was shared (musig.getGroups)
        for(Integer i : musicGroups){
            for(Group g : userGroups){
                if(g.getGroupID() == i){
                    return true;
                }
            }
        }
        return false;
    }


    private CopyOnWriteArrayList<Music> findMusic(String keyword, User user){
        Iterator it = mainThread.getSongs().iterator();

        CopyOnWriteArrayList<Music> toReturn = new CopyOnWriteArrayList<>();
        int i=0;

        while (it.hasNext()) {
            Music aux = (Music)it.next();
            if((aux.getTitle().contains(keyword) || aux.getArtist().contains(keyword) || aux.getGenre().contains(keyword)) && verifyGroups(aux.getGroups(), user.getDefaultShareGroups())) {
                toReturn.add(aux);
                i++;
            }
        }
        return toReturn;
    }


    private Music findMusic(String artist, String title) {
        Iterator it = mainThread.getSongs().iterator();

        while (it.hasNext()) {
            Music aux = (Music)it.next();
            if (aux.getTitle().equals(title) && aux.getArtist().equals(artist)) {
                return aux;
            }
        }
        return null;
    }

    private CopyOnWriteArrayList<Album> findAlbum(String keyword, User user) {

        Iterator it = mainThread.getAlbums().iterator();

        CopyOnWriteArrayList<Album> toReturn = new CopyOnWriteArrayList<>();
        int i=0;

        while (it.hasNext()) {
            Album aux = (Album) it.next();
            if((aux.getTitle().contains(keyword) || aux.getArtist().getName().contains(keyword) || aux.getGenre().contains(keyword)) && verifyGroups(aux.getGroups(), user.getDefaultShareGroups())){
                toReturn.add(aux);
                i++;
            }

        }
        return toReturn;
    }


    private Album findAlbum(String title, String artist) {

        Iterator it = mainThread.getAlbums().iterator();
        while (it.hasNext()) {
            Album aux = (Album)it.next();

            if (aux.getTitle().equals(title) && aux.getArtist().getName().equals(artist)){
                return aux;
            }
        }
        return null;
    }


    public String translation(String message){
        String tokens[] = message.split(" ; ");
        String info[][] = new String[tokens.length][];
        for(int i = 0; i < tokens.length; i++) info[i] = tokens[i].split(" \\| ");
        if(info[0][0].equals("type")){
            String command = info[0][1];
            switch(command) {
                case "config": {
                    int servercounter = this.mainThread.getServerCounter() + 1;
                    if(servercounter > 3){
                        servercounter = 3;
                    }
                    this.mainThread.setServerCounter(servercounter);
                    String reply = "type | config ; server_order | ";
                    int configServer = Integer.parseInt(info[1][1]);
                    int currentRequest = Integer.parseInt(info[2][1]);
                    int replyServer = this.mainThread.getReplyServer();
                    if(this.mainThread.replyOrder[configServer%3] == 1 && replyServer == configServer%3){
                        if(this.mainThread.replyOrder[(replyServer+1)%3] == 1){
                            this.mainThread.setReplyServer((replyServer+1)%3);
                            replyServer = (replyServer+2)%3;
                        }else{
                            this.mainThread.setReplyServer((replyServer+2)%3);
                        }
                    }
                    else{
                        this.mainThread.replyOrder[configServer%3] = 1;
                        replyServer = (replyServer+1)%3;
                    }
                    for(int i = 0; i < this.mainThread.replyOrder.length; i++){
                        reply += this.mainThread.replyOrder[i];
                        if(i != 3){
                            reply += ",";                        }
                    }
                    reply += " ; reply_server | " + replyServer + " ; item_count | ";

                    reply += (this.mainThread.getCurrentRequest() - currentRequest) + " ; requests | ";
                    if(currentRequest < this.mainThread.getCurrentRequest()){
                        CopyOnWriteArrayList<String> serverRequests = this.mainThread.getwriteRequests();
                        for(int i = currentRequest; i < this.mainThread.getCurrentRequest(); i++){
                            reply += serverRequests.get(i);
                            if(i != mainThread.getCurrentRequest()){
                                reply += ",";
                            }
                        }
                    }
                    return reply;
                }case "register": {
                    String username = info[1][1];
                    String password = info[2][1];
                    if (mainThread.getGroups().size() > 0 && findUser(username) != null) { //já existe este username
                        return "type | status ; operation | failed";
                    }

                    //else, register the new user

                    int admin;
                    User newUser;
                    Group g;
                    if (mainThread.getGroups().size() == 0){
                        admin = 1;
                        newUser = new User(username, password, admin);
                        g = new Group(newUser, 1);
                        mainThread.getGroups().add(g);
                    }else{
                        admin = 3;
                        newUser = new User(username, password, admin);
                        g = findGroup(1);
                        g.addUser(newUser);
                    }
                    newUser.addToDefaultShareGroups(g);
                    this.mainThread.addwriteRequest(message);
                    saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                    saveFile("src/Multicast/"+mainThread.getName()+"/groups.obj", mainThread.getGroups());
                    return "type | status ; operation | succeeded ; message | "+admin;

                }case "login": {
                    User currentUser;
                    String username = info[1][1];
                    String password = info[2][1];

                    if ((currentUser = findUser(username)) == null) {
                        return "type | status ; operation | failed ; error | 4";
                    }
                    if (!verifyPassword(currentUser, password)) {
                        return "type | status ; operation | failed ; error | 5";
                    }

                    boolean loggedForSomeReason = false;
                    for(User u : mainThread.getLoggedOn()){
                        if(u.getUsername().equals(username)){
                            loggedForSomeReason = true;
                            break;
                        }
                    }

                    if(!loggedForSomeReason)mainThread.getLoggedOn().add(currentUser);

                    int perks = currentUser.getPerks();
                    this.mainThread.addwriteRequest(message);
                    saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                    return "type | status ; operation | succeeded ; perks | " +perks;


                }case "logout":{
                    String username = info[1][1];
                    for(User u : this.mainThread.getLoggedOn()){
                        if(u.getUsername().equals(username)){
                            this.mainThread.getLoggedOn().remove(u);
                            break;
                        }
                    }
                    this.mainThread.addwriteRequest(message);
                    saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                    return "type | status ; operation | succeeded";

                }case "groups": {
                    String username = info[1][1];
                    User current = findUser(username);
                    return "type | groups ; item_count | "+ getAvailableGroups(current);
                }case "new_group": {
                    this.mainThread.addwriteRequest(message);
                    String username = info[1][1];
                    User current = findUser(username);
                    int groupID = mainThread.getGroups().size() + 1;
                    Group g = new Group(current, groupID);
                    current.getDefaultShareGroups().add(g);
                    current.setPerks(1);
                    mainThread.getGroups().add(g);
                    saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                    saveFile("src/Multicast/"+mainThread.getName()+"/groups.obj", mainThread.getGroups());
                    return "type | new_group ; groupID | " + groupID + " ; operation | succeeded";
                }case "join_group": {
                    String username = info[1][1];
                    int groupID = Integer.parseInt(info[2][1]);
                    User current = findUser(username);
                    Group g = findGroup(groupID);
                    if(!g.isUser(current.getUsername())) {
                        for(User request : g.getRequests()){
                            if(request.getUsername().equals(username)){
                                return "type | join_group ; status | failed ; error | You've already requested to join group " + groupID + "!";
                            }
                        }
                        this.mainThread.addwriteRequest(message);
                        g.addRequest(current);
                        saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                        saveFile("src/Multicast/"+mainThread.getName()+"/groups.obj", this.mainThread.getGroups());
                        CopyOnWriteArrayList <User> owners = g.getOwners();
                        String toReturn = "type | join_group ; status | succeeded ; owners | ";
                        for(User u : owners){
                            toReturn+=u.getUsername()+",";
                        }
                        return toReturn;
                    }
                    return "type | join_group ; status | failed ; error | You're already in group " + groupID +"!";
                }case "manage_request":{
                    String username = info[1][1];
                    User new_user = findUser(info[2][1]);
                    Group g = findGroup(Integer.parseInt(info[3][1]));
                    String request = info[4][1];
                    if(g.isOwner(username) && g.getGroupID()!=1){
                        this.mainThread.addwriteRequest(message);
                        saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                        if(request.equals("accept")){
                            new_user.getDefaultShareGroups().add(g);
                            g.addUser(new_user);
                            g.removeRequest(new_user.getUsername());
                            saveFile("src/Multicast/"+mainThread.getName()+"/groups.obj", this.mainThread.getGroups());
                            return "type | manage_request ; operation | succeeded ; operation | accept";
                        }else{
                            g.removeRequest(new_user.getUsername());
                            saveFile("src/Multicast/"+mainThread.getName()+"/groups.obj", this.mainThread.getGroups());
                            return "type | manage_request ; operation | succeeded ; operation | decline";
                        }
                    }else{
                        return "type | manage_request ; operation | failed";
                    }
                }case "expell_user":{
                    String username = info[1][1];
                    String expelled_user = info[2][1];
                    Group g = findGroup(Integer.parseInt(info[3][1]));
                    if(g.isOwner(username) && !g.isOwner(expelled_user) && g.getGroupID()!=1){
                        this.mainThread.addwriteRequest(message);
                        saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                        g.removeUser(expelled_user, g.getUsers());
                        if(g.isEditor(expelled_user)){
                            g.removeUser(expelled_user, g.getEditors());
                        }
                        saveFile("src/" + this.mainThread.getName()+ "/groups.obj", mainThread.getGroups());
                        return "type | expell_user ; operation | succeeded";
                    }else{
                        return "type | expell_user ; operation | failed";
                    }
                }case "leave_group":{
                    String username = info[1][1];
                    Group g = findGroup(Integer.parseInt(info[2][1]));
                    if(g.isUser(username)){
                        this.mainThread.addwriteRequest(message);
                        g.removeUser(username, g.getUsers());
                        if(g.isEditor(username)){
                            g.removeUser(username, g.getEditors());
                            if(g.isOwner(username)){
                                g.removeUser(username, g.getOwners());
                            }
                        }
                        saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                        saveFile("src/Multicast/" + this.mainThread.getName()+ "/groups.obj", mainThread.getGroups());
                        return "type | leave_group ; operation | succeeded";
                    }else{
                        return "type | leave_group ; operation | failed ; error | You don't belong to group " + g.getGroupID() + "!";
                    }
                }case "grant_perks":{
                    String perks = info[1][1];
                    String username = info[2][1];
                    String username2 = info[3][1];
                    int groupID = Integer.parseInt(info[4][1]);
                    int new_perks = 0;
                    if(perks.equals("editor"))new_perks = 2;
                    else if(perks.equals("owner"))new_perks = 1;
                    Group g = findGroup(groupID);
                    int old_perks = g.userPerks(username2);
                    int user_perks = g.userPerks(username);
                    if(new_perks >= user_perks && user_perks < 3 && new_perks < 3 && new_perks < old_perks){
                        this.mainThread.addwriteRequest(message);
                        User new_user = findUser(username2);
                        g.addEditor(new_user);
                        new_user.setPerks(2);
                        if(new_perks == 1){
                            g.addOwner(new_user);
                            new_user.setPerks(1);
                        }
                        saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                        saveFile("src/Multicast/"+mainThread.getName()+"/groups.obj", this.mainThread.getGroups());
                        return "type | grant_perks ; operation | succeeded";
                    }else{
                        return "type | grant_perks ; operation | failed ; error | You don't have permission to give " + perks + "perks in group " + groupID + "!";
                    }
                }case "get_requests":{
                    String username = info[1][1];
                    String list="";
                    for(Group g : mainThread.getGroups()) {
                        if(g.isOwner(username)) {
                            String users = g.getGroupRequests();
                            if(!users.equals("<>")){
                                list+=g.getGroupID()+" "+users+",";
                            }
                        }
                    }
                    if(!list.equals(""))
                        return "type | get_requests ; operation | succeeded ; list | "+list;
                    else
                        return "type | get_requests ; operation | succeeded ; list | empty";

                }case "search": {
                    String username = info[1][1];
                    User current = findUser(info[1][1]);
                    String keyword = info[2][1];
                    String object = info[3][1];
                    String toReturn = "type | ";
                    if(object.equals("music")){
                        toReturn += "music_list ; item_count | ";
                        CopyOnWriteArrayList<Music> m = findMusic(keyword, current);
                        if(m==null)
                            toReturn += "0";
                        else {
                            toReturn += m.size()+" ; item_list | ";
                            for (Music music : m) {
                                toReturn += music.getTitle()+", "+music.getArtist() +"\n";
                            }
                        }
                    }else if(object.equals("album")){
                        toReturn += "album_list ; item_count | ";
                        CopyOnWriteArrayList<Album> a = findAlbum(keyword, current);
                        if(a==null)
                            toReturn += "0";
                        else {
                            toReturn += a.size() + " ; item_list | ";
                            for (Album album : a) {
                                toReturn += album.getTitle() + ", " + album.getArtist().getName()+"\n";
                            }
                        }
                    }else if(object.equals("artist")){
                        toReturn += "artist_list ; item_count | ";
                        CopyOnWriteArrayList<Artist> ar = searchForArtist(keyword, current);
                        if(ar == null)
                            toReturn += "0";
                        else {
                            toReturn += ar.size() + " ; item_list | ";
                            for (Artist artist : ar) {
                                toReturn += artist.getName()+"\n";
                            }
                        }
                    }
                    return toReturn;
                }case "add_music": {
                    String username = info[1][1];
                    User current = findUser(username);
                    String groupIDs = info[2][1];
                    String aux[] = groupIDs.split(",");
                    CopyOnWriteArrayList<Integer> groups = new CopyOnWriteArrayList<>();

                    for(int i = 0; i < aux.length; i++){
                        for(Group g : current.getDefaultShareGroups()){ //percorre os grupos em que o current está inserido
                            try {
                                Integer.parseInt(aux[i]);
                            }catch(NumberFormatException e){
                                return "type | add_music ; operation | failed ; error | Group doesn't exist!";
                            }
                            if(g.getGroupID() == Integer.parseInt(aux[i])){
                                if(g.isEditor(username))
                                    groups.add(g.getGroupID());
                            }
                        }
                    }
                    if (groups.size()>0) { //se é editor ou owner
                        String title = info[3][1];
                        String genre = info[5][1];
                        Artist artist = findArtist(info[4][1]);
                        float duration = Float.parseFloat(info[6][1]);
                        Music m = findMusic(info[4][1], title);
                        if (m == null) {
                            if (artist == null){
                                artist = new Artist(info[4][1], genre);
                                for(Integer i : groups){
                                    artist.add_groups(i);
                                }
                                this.mainThread.getArtists().add(artist);
                            }
                        //se já houver uma música com o mesmo title e artist, não se pode adicionar

                            //caso este artista ainda não exista, cria-se um novo com a informação que já é dada

                            Music newMusic = new Music(title, info[4][1], genre, duration);

                            newMusic.add_editor(username);

                            for(int i = 0; i < aux.length ; i++){
                                try {
                                    newMusic.add_groups(Integer.parseInt(aux[i]));
                                }catch(NumberFormatException e){
                                    return "type | add_music ; operation | failed ; message | Please insert valid groups";
                                }

                            }
                            artist.addMusic(newMusic);
                            this.mainThread.getSongs().add(newMusic);
                            saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                            saveFile("src/Multicast/"+mainThread.getName()+"/artists.obj", mainThread.getArtists());
                            saveFile("src/Multicast/"+mainThread.getName()+"/musics.obj", mainThread.getSongs());
                            return "type | add_music ; operation | succeeded";
                        }else{
                            for(Integer i : m.getGroups()){
                                if(i == 1){
                                    return "type | add_music ; operation | failed ; error | This song already exists in the public group!";
                                }
                            }
                            return "type | add_music ; operation | failed ; error | This song already exists in a private group!";
                        }
                    }
                    if(aux.length > 1)return "type | add_album ; operation | failed ; error | You don't have permission to add a song to these groups!";
                    else{
                        return "type | add_album ; operation | failed ; error | You don't have permission to add a song to this group!";
                    }

                }case "add_artist" : {
                    String username = info[1][1];
                    User current = findUser(username);
                    String groupIDs = info[2][1];
                    String aux[] = groupIDs.split(",");
                    CopyOnWriteArrayList<Integer> groups = new CopyOnWriteArrayList<>();
                    for(int i = 0; i < aux.length; i++){
                        for(Group g : current.getDefaultShareGroups()){
                            try{
                                Integer.parseInt(aux[i]);
                            }catch (NumberFormatException e){
                                return "type | add_artist ; operation | failed ; message | Invalid group!";
                            }
                            if(g.getGroupID() == Integer.parseInt(aux[i])){
                                if(g.isEditor(username))
                                    groups.add(g.getGroupID());
                            }
                        }
                    }
                    if (groups.size() > 0) {
                        String name = info[3][1];
                        String description = info[4][1];
                        String concerts = info[5][1];
                        Description desc = new Description(description, current);
                        String genre = info[6][1];
                        String conc[] = concerts.split(",");
                        Artist a;
                        if ((a = findArtist(name)) == null) {
                            Artist newArtist = new Artist(name, desc, new CopyOnWriteArrayList<>(Arrays.asList(conc)), genre);
                            for(Integer i : groups){
                                newArtist.add_groups(i);
                            }
                            this.mainThread.getArtists().add(newArtist);
                            this.mainThread.addwriteRequest(message);
                            saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                            saveFile("src/Multicast/"+mainThread.getName()+"/artists.obj", mainThread.getArtists());
                            return "type | add_artist ; operation | succeeded";
                        }else{
                            for(Integer i : a.getGroups()){
                                if(i == 1){
                                    return "type | add_artist ; operation | failed ; error | This artist already exists in the public group!";
                                }
                            }
                            return "type | add_artist ; operation | failed ; error | That artist already exists in a private group!";
                        }
                    }
                    if(aux.length > 1)return "type | add_album ; operation | failed ; error | You don't have permission to add an artist to these groups!";
                    else{
                        return "type | add_album ; operation | failed ; error | You don't have permission to add an artist to this group!";
                    }
                }case "add_album": {
                    String username = info[1][1];
                    User current = findUser(username);
                    String groupIDs = info[2][1];
                    String aux[] = groupIDs.split(",");
                    CopyOnWriteArrayList<Integer> groups = new CopyOnWriteArrayList<>();
                    for(int i = 0; i < aux.length; i++){
                        for(Group g : current.getDefaultShareGroups()){
                            try{
                                Integer.parseInt(aux[i]);
                            }catch (NumberFormatException e){
                                return "type | add_album ; operation | failed ; message | Invalid group!";
                            }
                            if(g.getGroupID() == Integer.parseInt(aux[i])){
                                if(g.isEditor(username))
                                    groups.add(g.getGroupID());
                            }
                        }
                    }
                    if(groups.size() > 0) {
                        String title = info[4][1];
                        String genre = info[8][1];
                        Artist artist = findArtist(info[3][1]);
                        Album a;
                        if ((a = findAlbum(title, info[3][1])) == null) {
                            if (artist == null) {
                                artist = new Artist(info[3][1], genre);
                                for (Integer i : groups) {
                                    artist.add_groups(i);
                                }
                                mainThread.getArtists().add(artist);
                            }
                            int year;
                            try{
                                year = Integer.parseInt(info[6][1]);
                            }catch (NumberFormatException e){
                                return "type | add_artist ; operation | failed ; message | Invalid year!";
                            }

                            String musics = info[5][1];
                            String mus[] = musics.split(",");
                            String publisher = info[7][1];
                            String description = info[9][1];
                            Description desc = new Description(description, current);
                            CopyOnWriteArrayList<Music> musicList = new CopyOnWriteArrayList<>();
                            for (String m : mus) {
                                Music music;
                                if ((music = findMusic(artist.getName(), m)) == null) {
                                    Music newMusic = new Music(m, artist.getName(), genre);
                                    for (Integer i : groups) {
                                        newMusic.add_groups(i);
                                    }
                                    musicList.add(newMusic);
                                    mainThread.getSongs().add(newMusic);
                                    artist.addMusic(newMusic);
                                } else {
                                    boolean change = true;
                                    for (Integer i : music.getGroups()) {
                                        if (i == 1) {
                                            change = false;
                                        }
                                    }
                                    if (change) {
                                        for (Integer i : groups) {
                                            boolean isShared = false;
                                            for (Integer i2 : music.getGroups()) {
                                                if (i == i2) {
                                                    isShared = true;
                                                    break;
                                                }
                                            }
                                            if (!isShared) {
                                                music.add_groups(i);
                                            }
                                        }
                                    }
                                }
                                saveFile("src/Multicast/"+mainThread.getName()+"/musics.obj", mainThread.getSongs());
                            }
                            Album newAlbum = new Album(artist, title, year, musicList, publisher, genre, desc);
                            for (Integer i : groups) {
                                newAlbum.add_groups(i);
                            }
                            artist.getAlbums().add(newAlbum);
                            this.mainThread.getAlbums().add(newAlbum);
                            this.mainThread.addwriteRequest(message);
                            saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                            saveFile("src/Multicast/"+mainThread.getName()+"/artists.obj", this.mainThread.getArtists());
                            saveFile("src/Multicast/"+mainThread.getName()+"/albums.obj", this.mainThread.getAlbums());
                            return "type | add_album ; operation | succeeded";
                        }else{
                            for(Integer i : a.getGroups()){
                                if(i == 1){
                                    return "type | add_album ; operation | failed ; error | This album already exists in the public group!";
                                }
                            }
                            return "type | add_album ; operation | failed ; error | This album already exists in a private group!";
                        }
                    }
                    if(aux.length > 1)return "type | add_album ; operation | failed ; error | You don't have permission to add an album to these groups!";
                    else{
                        return "type | add_album ; operation | failed ; error | You don't have permission to add an album to this group!";
                    }
                }case "change_info": {
                    if (info.length == 8) {
                        if (info[1][1].equals("music")) {
                            String username = info[2][1];
                            User current = findUser(username);
                            String groupID = info[3][1];
                            CopyOnWriteArrayList<Integer> groups = new CopyOnWriteArrayList<>();
                            for (Group g : current.getDefaultShareGroups()) {
                                try{
                                    Integer.parseInt(groupID);
                                }catch (NumberFormatException e){
                                    return "type | change_info ; operation | failed ; message | Invalid year!";
                                }
                                if (g.getGroupID() == Integer.parseInt(groupID)) {
                                    if (g.isEditor(username))
                                        groups.add(g.getGroupID());
                                }
                            }
                            if (groups.size() > 0) { //se é editor ou owner
                                String title = info[4][1];
                                String genre = info[6][1];
                                Artist artist = findArtist(info[5][1]);
                                float duration = Float.parseFloat(info[7][1]);
                                Music m;
                                if ((m = findMusic(info[5][1], title)) != null && m.getGroups().contains(Integer.parseInt(groupID))) {
                                    if (artist == null) {
                                        artist = new Artist(info[4][1], genre);
                                        for (Integer i : groups) {
                                            artist.add_groups(i);
                                        }
                                        mainThread.getArtists().add(artist);
                                    }
                                    m.setTitle(title);
                                    m.setArtist(info[5][1]);
                                    m.setGenre(genre);
                                    m.setDuration(duration);

                                    if (!m.getEditors().contains(username))
                                        m.add_editor(username);
                                    if (!artist.getMusics().contains(m))
                                        artist.addMusic(m);
                                    saveFile("src/Multicast/"+mainThread.getName()+"/musics.obj", mainThread.getSongs());
                                    saveFile("src/Multicast/"+mainThread.getName()+"/artists.obj", mainThread.getArtists());
                                    return "type | change_info ; operation | succeeded";
                                }
                                else
                                    return "type | change_info ; operation | failed ; error | Either the music doesnt exist or you dont have permitions to edit it";
                            }
                            return "type | change_info ; operation | failed ; error ! You are not Editor or Owner og group "+groupID;
                        } else if (info[1][1].equals("artist")) {
                            String username = info[2][1];
                            User current = findUser(username);
                            String groupID = info[3][1];
                            CopyOnWriteArrayList<Integer> groups = new CopyOnWriteArrayList<>();
                            for (Group g : current.getDefaultShareGroups()) {
                                if (g.getGroupID() == Integer.parseInt(groupID)) {
                                    if (g.isEditor(username))
                                        groups.add(g.getGroupID());
                                }
                            }
                            if (groups.size() > 0) {
                                String name = info[4][1];
                                String description = info[5][1];
                                String concerts = info[6][1];
                                Description desc = new Description(description, current);
                                String genre = info[7][1];
                                String conc[] = concerts.split(",");
                                Artist a;
                                if ((a = findArtist(name)) != null && a.getGroups().contains(Integer.parseInt(groupID))) {
                                    Artist newArtist = new Artist(name, desc, new CopyOnWriteArrayList<>(Arrays.asList(conc)), genre);
                                    a.setName(name);
                                    a.setDescription(desc);
                                    a.setConcerts(new CopyOnWriteArrayList<>(Arrays.asList(conc)));
                                    a.setGenre(genre);
                                    saveFile("src/Multicast/"+mainThread.getName()+"/artists.obj", mainThread.getArtists());
                                    return "type | change_info ; operation | succeeded";
                                }
                                else
                                    return "type | change_info ; operation | failed ; error | Either the artist doesnt exist or you dont have permitions to edit it";
                            }
                            return "type | change_info ; operation | failed ; error ! You are not Editor or Owner og group "+groupID;
                        }
                        return "type | status ; command | invalid";
                    }
                    else if (info.length == 10) {
                        String username = info[1][1];
                        User current = findUser(username);
                        String groupID = info[2][1];
                        CopyOnWriteArrayList<Integer> groups = new CopyOnWriteArrayList<>();
                        for (Group g : current.getDefaultShareGroups()) {
                            if (g.getGroupID() == Integer.parseInt(groupID)) {
                                if (g.isEditor(username))
                                    groups.add(g.getGroupID());
                            }
                        }
                        if(groups.size() > 0) {
                            String title = info[4][1];
                            String genre = info[8][1];
                            Artist artist = findArtist(info[3][1]);
                            Album a;
                            if ((a = findAlbum(title, info[3][1])) != null && a.getGroups().contains(Integer.parseInt(groupID))) {
                                if (artist == null) {
                                    artist = new Artist(info[4][1], genre);
                                    for (Integer i : groups) {
                                        artist.add_groups(i);
                                    }
                                    mainThread.getArtists().add(artist);
                                }
                                int year = Integer.parseInt(info[6][1]);
                                String musics = info[5][1];
                                String mus[] = musics.split(",");
                                String publisher = info[7][1];
                                String description = info[9][1];
                                CopyOnWriteArrayList<Music> musicList = new CopyOnWriteArrayList<>();
                                for (String m : mus) {
                                    Music music;
                                    if ((music = findMusic(artist.getName(), m)) == null){
                                        Music newMusic = new Music(m, info[3][1], genre);
                                        for(Integer i : groups){
                                            newMusic.add_groups(i);
                                        }
                                        musicList.add(newMusic);
                                        mainThread.getSongs().add(newMusic);
                                    }
                                    else
                                        musicList.add(music);
                                }
                                saveFile("src/Multicast/"+mainThread.getName()+"/musics.obj", mainThread.getSongs());
                                a.setArtist(artist);
                                a.setTitle(title);
                                a.setYearOfPublication(year);
                                a.setMusics(musicList);
                                a.setPublisher(publisher);
                                a.setGenre(genre);
                                Description desc = new Description(description,current);
                                a.setDescription(desc);
                                saveFile("src/Multicast/"+mainThread.getName()+"/albums.obj", this.mainThread.getAlbums());
                                return "type | change_info ; operation | succeeded";
                            }
                            else
                                return "type | change_info ; operation | failed ; error | Either the album doesnt exist or you dont have permitions to edit it";

                        }
                        return "type | change_info ; operation | failed ; error ! You are not Editor or Owner og group "+groupID;
                    }
                    return "type | status ; command | invalid";
                }case "get_info": {
                    String answer = "type | get_info ;  info | ";
                    for(int i = 0; i < info.length; i++){
                        System.out.println(info[i][0]);
                    }
                    if (info[1][0].equals("username") && info[2][0].equals("object") && info[3][0].equals("title") && info.length == 4) {
                        if (info[2][1].equals("artist")) {
                            User u = findUser(info[1][1]);
                            Artist a = findArtist(info[3][1]);
                            if (a == null) {
                                return "type | get_info ; operation | failed ; error | That artist doesn't exist!\n";
                            }
                            for (int i = 0; i < mainThread.getGroups().size(); i++) {
                                if (verifyGroups(a.getGroups(), u.getDefaultShareGroups())) {
                                    answer += "----------| Artist |----------\n" + a.getName()+"\n";
                                    if(a.getAlbums().size()>0){
                                        answer += "----------| Albums |----------\n";
                                        for (Album album : a.getAlbums()) {
                                            answer += album.getTitle() + "\n";
                                        }
                                    }
                                    if(a.getGenre()!=null){
                                        answer += "----------| Genre |----------\n" + a.getGenre();
                                    }
                                    if(a.getDescription()!=null){
                                        answer += "----------| Biografy |----------\n" + a.getDescription().getText();
                                    }
                                    return answer;
                                }
                            }
                            return "type | get_info ; operation | failed ; error | That artist isn't shared with a group that you belong to!";
                        }
                    }else if(info[1][0].equals("username") && info[2][0].equals("object") && info[3][0].equals("title") && info.length == 5){
                        if (info[2][1].equals("album")) {
                            User u = findUser(info[1][1]);
                            Album a = findAlbum(info[3][1], info[4][1]);
                            if(a==null) {
                                return "type | get_info ; operation | failed ; error | That album doesn't exist!";
                            }

                            for(int i=0 ; i<mainThread.getGroups().size();i++) {
                                if (a.getGroups().contains(mainThread.getGroups().get(i).getGroupID()) && mainThread.getGroups().get(i).getUsers().contains(u)) {
                                    answer+="----------| Album |----------\n"+a.getTitle()+"\n";
                                    answer+="----------| Artist |----------\n"+a.getArtist().getName()+"\n";
                                    answer+="----------| Music List |----------\n";
                                    for(Music m : a.getMusics()){
                                        try{
                                            answer+=m.getTitle()+"\n";
                                        }catch(NullPointerException e){};
                                    }
                                    answer+="----------| Genre |----------\n"+a.getGenre()+"\n";
                                    answer+="----------| Year |----------\n"+a.getYearOfPublication()+"\n";
                                    answer+="----------| Description |----------\n";
                                    answer+=a.getDescription().getText()+"\n";
                                    answer+="----------| Reviews |----------\n";
                                    if(a.reviewsToString()!=null)
                                        answer+=a.reviewsToString()+"\n";
                                    answer+="\nAverage Rating: "+a.calcAverageRate();
                                    return answer;
                                }
                            }
                            return "type | get_info ; operation | failed ; error | That artist isn't shared with a group that you belong to!";
                        }
                    }
                    else
                        return "type | status ; command | invalid";
                }case "review": {
                    if (info[1][0].equals("album_title") && info[2][0].equals("artist_name") && info[3][0].equals("username") && info[4][0].equals("text") && info[5][0].equals("rate") && info.length == 6) {
                        Album a = findAlbum(info[1][1], info[2][1]);
                        if(a != null){
                            Review r = new Review(Integer.parseInt(info[5][1]), info[4][1], findUser(info[3][1]));
                            a.addReview(r);
                            this.mainThread.addwriteRequest(message);
                            saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                            saveFile("src/Multicast/" + this.mainThread.getName()+ "/albums.obj", mainThread.getAlbums());
                            return "type | status ; review | succeeded";
                        }
                        else
                            return "type | status ; review | failed ; error | That album doesn't exist!";

                    } else {
                        return "type | status ; command | invalid";
                    }
                }case "test": {
                    test();
                    return "type | status ; command | tested";
                }case "upload": { //quando o Multicast recebe um pedido para um client dar upload de uma música

                    String username = info[1][1];
                    String musicTitle = info[2][1];
                    String musicTitle2 = musicTitle.replaceAll(" ", ""); //para fazer o path do ficheiro
                    String artistName = info[3][1];
                    String artistName2 = artistName.replaceAll(" ", ""); //para fazer o path do ficheiro
                    String ans = "";

                    Music found = findMusic(artistName, musicTitle);

                    //porém, este user pode não ter acesso à musica (não está num grupo onde esta tenha sido adicionada)
                    User user = findUser(username);


                    if (!verifyGroups(found.getGroups(), user.getDefaultShareGroups()))
                        return "type | upload ; operation | failed";

                    if (found != null) {
                        //siginifica que a música existe e que efetivamente conseguimos associá-la com o ficheiro que aí vem
                        ans = "type | upload ; port | 5500";
                    } else { //findMusic = null => significa que a música não existe, e portanto o request tem que ser recusado
                        return "type | upload ; operation | failed";
                    }


                    //create new thread
                    TCPWorker newWorker = new TCPWorker("upload",5500, user, found, this.mainThread, this);
                    newWorker.start();

                    return ans;
                }case "download": {
                    String username = info[1][1];
                    String musicTitle = info[2][1];
                    String musicTitle2 = musicTitle.replaceAll(" ", ""); //para fazer o path do ficheiro
                    String artistName = info[3][1];
                    String artistName2 = artistName.replaceAll(" ", ""); //para fazer o path do ficheiro
                    String ans = "";

                    Music found = findMusic(artistName, musicTitle);
                    User user = findUser(username);

                    //este user tem sempre acesso à música porque foi escolhida das suas transferredMusics

                    ans = "type | download ; port | 5500";

                    TCPWorker newWorker = new TCPWorker("download",5500, user, found, this.mainThread, this);
                    newWorker.start();

                    return ans;
                }case "get_musics": {
                    String username = info[1][1];

                    User thisUser = findUser(username);

                    String list = "<";
                    int count = 0;

                    for (Music m : thisUser.getTransferredMusics()) {
                        list += m.getTitle() + ":" + m.getArtist() + ",";
                        count++;
                    }

                    list = list.substring(0, list.length() - 1);
                    list += ">";

                    String ans = "type | get_musics ; item_count | " + count + " ; music_list | " + list;
                    return ans;
                }case "share_music": {
                    String username = info[1][1];
                    String musicTitle = info[2][1];
                    String artistName = info[3][1];
                    String groups = info[4][1];

                    groups = groups.replace("<", "");
                    groups = groups.replace (">", "");

                    String [] groupIDs = groups.split(",");


                    Music toBeShared = findMusic(artistName, musicTitle);

                    String list = "";
                    int count = 0;

                    //percorrer todos os users de cada grupo, e adicionar a música às transferred musics
                    for (String ID : groupIDs) {
                        Group group = findGroup(Integer.parseInt(ID));
                        for (User user : group.getUsers()) {
                            if (!user.getTransferredMusics().contains(toBeShared)) {
                                user.getTransferredMusics().add(toBeShared);
                                count++;
                                list += user.getUsername() + ",";
                            }
                        }
                    }

                    String ans = "type | share_music ; item_count | " + count + " ; user_list | " + list;
                    return ans;
                }case "notification": {
                    String username = info[1][1];
                    String notif = info[2][1];
                    User current = findUser(username);
                    Notification newNotif = new Notification(notif);
                    current.getNotifications().add(newNotif);
                    saveFile("src/Multicast/" + this.mainThread.getName()+ "/groups.obj", mainThread.getGroups());
                    this.mainThread.addwriteRequest(message);
                    saveFile("src/Multicast/" + this.mainThread.getName()+ "/writeRequests.obj", mainThread.getwriteRequests());
                    return "type | status ; operation | succeeded";

                }case "get_notifications":{
                    String username = info[1][1];
                    User current = findUser(username);
                    int counter;
                    if(current.getNotifications()!=null) {
                        if ((counter = current.getNotifications().size()) > 0) {
                            String reply = "type | get_notifications ; item_count | " + counter + " ; notifications | ";
                            for (Notification n : current.getNotifications()) {
                                reply += n.getMessage() + "\n";
                                current.getNotifications().remove(n);
                            }
                            saveFile("src/Multicast/" + this.mainThread.getName()+ "/groups.obj", mainThread.getGroups());
                            return reply;
                        } else {
                            return "type | get_notifications ; item_count | 0 ; notifications | ";
                        }
                    }
                }case "resend":{
                    message = message.replaceAll("type | resend ; ", "");
                    Request r = this.mainThread.findRequest(message);
                    this.mainThread.replyOrder[r.getReplyServer()] = 0;
                    this.mainThread.setServerCounter(this.mainThread.getServerCounter()-1);
                    return r.getReply();
                }default:{
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
            Request r = this.mainThread.findRequest(this.request.getRequest());
            String aux[] = this.request.getRequest().split(" ; ");
            String message;
            message = translation(this.request.getRequest());
            if(!aux[0].equals("type | resend")){
                 this.request.setReply(message);
                 this.mainThread.addRequest(this.request);
            }
            int replyServer = this.mainThread.getReplyServer();
            while(true){
                if(this.mainThread.replyOrder[replyServer] != 0){
                    if(this.mainThread.getServerNumber()%3 == this.mainThread.getReplyServer()){
                        byte[] buffer = message.getBytes();
                        DatagramPacket packet = null;
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        if(aux[0].equals("type | config")){
                            packet = new DatagramPacket(buffer, buffer.length, group, 4330);
                        }else {

                            packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        }
                        socket.send(packet);
                        System.out.println("Sent to multicast address: " + message);
                    }
                    break;
                }else{
                    replyServer++;
                    replyServer = replyServer%3;
                    this.mainThread.setReplyServer((replyServer));
                }
            }
            this.mainThread.setReplyServer((replyServer+1)%3);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}

class TCPWorker extends Thread {
    int port;
    String serverAddress;
    User user; //user que fez o upload do ficheiro
    Music music;
    MulticastServer mainThread;
    String path;
    requestHandler thread;
    String requestType; //can be "download" or "upload"

    public TCPWorker(String requestType, int port, User user, Music music, MulticastServer mainThread, requestHandler thread) {
        super();
        this.port = port;
        this.user = user;
        this.music = music;
        this.mainThread = mainThread;
        this.thread = thread;
        this.requestType = requestType;
    }

    public void run() {
        int port = 5500;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(5500);
            clientSocket = serverSocket.accept();

            //all the code for the file download
            if (requestType.equals("upload")) saveFile(clientSocket);
            else sendFile(clientSocket);

            serverSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(Socket clientSocket) throws IOException {
        //first of all, "calculate" the path of the new file
        this.path = "src/Multicast/" + this.mainThread.getName() + "/TransferredFiles/" + this.music.getTitle().replaceAll(" ", "") + this.music.getArtist().replaceAll(" ", "") + ".mp3";
        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        FileOutputStream fos = new FileOutputStream(this.path);
        //the first message the server receives is the file size
        int fileSize = dis.readInt();
        byte[] buffer = new byte[fileSize];
        int read = 0;
        int totalRead = 0;
        int remaining = fileSize;
        while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            fos.write(buffer, 0, read);
        }
        //file has been written
        this.music.setPathToFile(this.path);
        this.user.getTransferredMusics().add(this.music);
        this.thread.saveFile("src/Multicast/" + this.mainThread.getName() + "/users.obj", thread.findGroup(1).getUsers());
        this.thread.saveFile("src/Multicast/" + this.mainThread.getName() + "/musics.obj", mainThread.getSongs());
    }

    public void sendFile(Socket clientSocket) throws IOException {

        //the path of the file the user wants to download is in the Music object
        this.path = this.music.getPathToFile();
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream()); //stream for writing to client socket
        FileInputStream fis = new FileInputStream(this.path); //stream for reading from music file in the server
        long len = fis.getChannel().size();
        byte[] buffer = new byte[toIntExact(len)];

        //sending the file size on a separate message
        dos.writeInt(toIntExact(len));

        //writes the actual file to the clientSocket
        fis.read(buffer); //reads bytes from file into buffer
        dos.write(buffer, 0, toIntExact(len));

        fis.close();
        dos.close();
    }
}
