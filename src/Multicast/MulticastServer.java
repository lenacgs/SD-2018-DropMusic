package Multicast;

import Interface.*;
import FileHandling.*;
//import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;
import java.io.IOException;
import java.nio.Buffer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import static java.lang.Math.toIntExact;

public class MulticastServer extends Thread {
    private ObjectFile usersObjectFile; //file for registered users
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4322;
    private CopyOnWriteArrayList<User> registeredUsers;
    private CopyOnWriteArrayList<User> loggedOn;
    private CopyOnWriteArrayList<Group> groups;
    private String pathToObjectFiles;
    private String name;
    private ObjectFile groupObjectFile;
    private ObjectFile songsObjectFile;
    private ObjectFile artistsObjectFile;
    private ObjectFile albumsObjectFile;
    private CopyOnWriteArrayList<Music> songs;
    private CopyOnWriteArrayList<Artist> artists;
    private CopyOnWriteArrayList<Album> albums;

    public void setGroups(CopyOnWriteArrayList<Group> groups) {
        this.groups = groups;
    }

    public ObjectFile getGroupObjectFile() {
        return groupObjectFile;
    }

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
        server.start();
    }

    public MulticastServer(String name, String pathToObjectFiles){
        super(name);
        usersObjectFile = new ObjectFile();
        groupObjectFile = new ObjectFile();
        artistsObjectFile = new ObjectFile();
        songsObjectFile = new ObjectFile();
        albumsObjectFile = new ObjectFile();
        registeredUsers = new CopyOnWriteArrayList<User>();
        groups = new CopyOnWriteArrayList<Group>();
        this.artists = new CopyOnWriteArrayList<>();
        this.songs = new CopyOnWriteArrayList<>();
        loggedOn = new CopyOnWriteArrayList<User>();
        this.pathToObjectFiles = pathToObjectFiles;
        this.name = name;
        this.albums = new CopyOnWriteArrayList<>();
    }

    public ObjectFile getUsersObjectFile() {
        return usersObjectFile;
    }


    public void fileHandler() {
        try {
            this.getUsersObjectFile().openRead("src/Multicast/users.obj");
            this.setRegisteredUsers((CopyOnWriteArrayList)this.getUsersObjectFile().readsObject());
            this.getUsersObjectFile().closeRead();
        } catch (IOException e) {/*if there's an exception => empty files => do nothing*/}
        try {
            System.out.println("yo");
            this.getGroupObjectFile().openRead("src/Multicast/groups.obj");
            this.setGroups((CopyOnWriteArrayList)this.getGroupObjectFile().readsObject());
            this.getGroupObjectFile().closeRead();
        } catch (IOException e) {/*if there's an exception => empty files => do nothing*/}
        try {
            this.getSongsObjectFile().openRead("src/Multicast/musics.obj");
            this.setSongs((CopyOnWriteArrayList)this.getSongsObjectFile().readsObject());
            this.getSongsObjectFile().closeRead();
        } catch (IOException e) {/*if there's an exception => empty files => do nothing*/}
        try {
            this.getAlbumsObjectFile().openRead("src/Multicast/albums.obj");
            this.setAlbums((CopyOnWriteArrayList)this.getAlbumsObjectFile().readsObject());
            this.getAlbumsObjectFile().closeRead();
        } catch (IOException e) {/*if there's an exception => empty files => do nothing*/}
        try {
            this.getArtistsObjectFile().openRead("src/Multicast/artists.obj");
            this.setArtists((CopyOnWriteArrayList)this.getArtistsObjectFile().readsObject());
            this.getArtistsObjectFile().closeRead();
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
            socket.setLoopbackMode(false);

            System.out.println("This is my address: " + socket.getInterface().getHostAddress());

            while(true){ //receiving
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());

                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + "with message: " + message);

                    //creates new thread for handling the new request
                    RequestHandler newRequest = new RequestHandler(message, this);
                    newRequest.start();
                }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            socket.close();
        }


    }
}


class RequestHandler extends Thread{ //handles request and sends answer back to RMI
    private String request;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private MulticastServer mainThread;

    public RequestHandler(String request, MulticastServer mainThread){
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


    private CopyOnWriteArrayList<Artist> searchForArtist(String keyword){
        Iterator it = mainThread.getSongs().iterator();

        CopyOnWriteArrayList<Artist> toReturn = new CopyOnWriteArrayList<>();
        int i=0;

        while (it.hasNext()) {
            Artist aux = (Artist)it.next();
            if(aux.getName().contains(keyword) || aux.getGenre().contains(keyword) || aux.checkIfContains(keyword)){
                toReturn.add(aux);
                i++;
            }
        }
        return toReturn;
    }

    private Artist findArtist (String artistName) {
        if(mainThread.getArtists().equals(null)) return null;
        for(Artist a : this.mainThread.getArtists()){
            if(a.getName().equals(artistName)){
                return a;
            }
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

    /*public String searchAlbums (String keyword) {

        CopyOnWriteArrayList<Album> albums = this.mainThread.getAlbums();
        String reply;
        for (Album a :albums) {}
    }*/

    private boolean isInGroup(User user, Group group){ return (group.isUser(user)); }

    private boolean verifyPassword(User current, String password){ return current.getPassword().equals(password);}

    private String getAvailableGroups(User user){
        int counter = 0;
        String reply = "<";
        Iterator it = mainThread.getGroups().iterator();

        while(it.hasNext()) {
            Group aux = (Group)it.next();
            aux.printUsers();
            if (!aux.isUser(user)) {
                if (counter++ > 0) {
                    reply += ",";
                }
                reply += aux.getGroupID();
            }
        }
        reply += ">";
        return reply;
    }

    public void saveFile(String filename, Object o){
        try {
            mainThread.getUsersObjectFile().openWrite(filename);
            mainThread.getUsersObjectFile().writesObject(o);
            mainThread.getUsersObjectFile().closeWrite();
        } catch (IOException e) {
            System.out.println("Could not openWrite to file " + mainThread.getPathToObjectFiles() + "src/Multicast/users.obj");
        }
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
            if((aux.getTitle().contains(keyword) || aux.getArtist().getName().contains(keyword) || aux.getGenre().contains(keyword)) && verifyGroups(aux.getGroups(), user.getDefaultShareGroups())) {
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
            if (aux.getTitle().equals(title) && aux.getArtist().getName().equals(artist)) {
                return aux;
            }
        }
        return null;
    }

    private CopyOnWriteArrayList<Album> findAlbum(String keyword) {

        Iterator it = mainThread.getAlbums().iterator();

        CopyOnWriteArrayList<Album> toReturn = new CopyOnWriteArrayList<>();
        int i=0;

        while (it.hasNext()) {
            Album aux = (Album) it.next();
            if(aux.getTitle().contains(keyword) || aux.getArtist().getName().contains(keyword) || aux.getGenre().contains(keyword)){
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
            if (aux.getTitle().equals(title) && aux.getArtist().getName() == artist) return aux;
        }

        return null;
    }

    private String translation(String message){
        String tokens[] = message.split(" ; ");
        String info[][] = new String[tokens.length][];
        for(int i = 0; i < tokens.length; i++) info[i] = tokens[i].split(" \\| ");
        if(info[0][0].equals("type")){
            String command = info[0][1];
            switch(command) {
                case "register": {
                    String username = info[1][1];
                    String password = info[2][1];
                    if (findUser(username) != null) { //já existe este username
                        return "type | status ; operation | failed ; message | This username already exists... Try a different one!";
                    }


                    //else, register the new user

                    int admin = 1;
                    if (mainThread.getRegisteredUsers().size() > 0) admin = 3;

                    User newUser = new User(username, password, admin);

                    mainThread.getRegisteredUsers().add(newUser);

                    saveFile("src/Multicast/users.obj", mainThread.getRegisteredUsers());
                    return "type | status ; operation | succeeded ; admin | " + admin + " ; message | User registered!";

                }case "login": {
                    User currentUser;
                    String username = info[1][1];
                    String password = info[2][1];

                    if ((currentUser = findUser(username)) == null) {
                        return "type | status ; operation | failed ; message | This username doesn't exist!";
                    }
                    if (!verifyPassword(currentUser, password)) {
                        return "type | status ; operation | failed ; message | Wrong password!";
                    }

                    mainThread.getLoggedOn().add(currentUser);

                    saveFile("src/Multicast/logged.obj", mainThread.getLoggedOn());

                    int perks = currentUser.getPerks();

                    return "type | status ; operation | succeeded ; perks | " + perks;


                }case "logout":{
                    String username = info[1][1];
                    User current = findUser(username);
                    if(current != null) {
                        mainThread.getLoggedOn().remove(current);
                        saveFile("src/Multicast/logged.obj", mainThread.getLoggedOn());
                    }
                    return "type | status ; operation | succeeded";

                }case "perks":{
                    String username = info[1][1];
                    User current = findUser(username);
                    if(current == null){
                        return "type | status ; operation | failed";
                    }
                    return "type | perks ; user | " + current.getPerks();

                }case "perks_group": {
                    User current = findUser(info[1][1]);
                    int groupID = Integer.parseInt(info[2][1]);
                    Group g = findGroup(groupID);
                    if(g == null){
                        return "type | status ; operation | failed";
                    }
                    if(g.isOwner(current)){
                        return "type | perks_group ; user | owner";
                    } else if(g.isEditor(current)){
                        return "type | perks_group ; user | editor";
                    }else if(g.isUser(current)) {
                        return "type | perks_group ; user | normal";
                    }else{
                        return "type | status ; operation | failed";
                    }
                }case "groups": {
                    String username = info[1][1];
                    User current = findUser(username);
                    return "type | groups ; list ; " + getAvailableGroups(current);
                }case "new_group": {
                    String username = info[1][1];
                    User current = findUser(username);
                    mainThread.getGroups().add(new Group(current, mainThread.getGroups().size() + 1));
                    saveFile("src/Multicast/groups.obj", mainThread.getGroups());
                    return "type | new_group ; operation | succeeded";
                }case "join_group": {
                    String username = info[1][1];
                    int groupID = Integer.parseInt(info[2][1]);
                    User current = findUser(username);
                    Group g = findGroup(groupID);
                    g.addRequest(current);
                    saveFile("src/Multicast/groups.obj", this.mainThread.getGroups());
                    return "type | join_group ; operation | succeeded";
                }case "manage_request":{
                    String username = info[1][1];
                    User new_user = findUser(info[2][1]);
                    Group g = findGroup(Integer.parseInt(info[3][1]));
                    String request = info[4][1];
                    if(g.isOwner(username)){
                        if(request.equals("accepted")){
                            g.addUser(new_user);
                            g.removeRequest(new_user.getUsername());
                        }else{
                            g.removeRequest(new_user.getUsername());
                        }
                        saveFile("src/Multicast/groups.obj", this.mainThread.getGroups());
                        return "type | manage_request ; operation | succeeded";
                    }else{
                        return "type | manage_request ; operation | failed";
                    }
                }case "expell_user":{
                    String username = info[1][1];
                    String expelled_user = info[2][1];
                    Group g = findGroup(Integer.parseInt(info[3][1]));
                    if(g.isOwner(username) && !g.isOwner(expelled_user)){
                        g.removeUser(expelled_user, g.getUsers());
                        if(g.isEditor(expelled_user)){
                            g.removeUser(expelled_user, g.getEditors());
                        }
                        saveFile("src/Multicast/groups.obj", this.mainThread.getGroups());
                        return "type | expell_user ; operation | succeeded";
                    }else{
                        return "type | expell_user ; operation | failed";
                    }
                }case "leave_group":{
                    String username = info[1][1];
                    Group g = findGroup(Integer.parseInt(info[2][1]));
                    if(g.isUser(username)){
                        g.removeUser(username, g.getUsers());
                        if(g.isEditor(username)){
                            g.removeUser(username, g.getEditors());
                            if(g.isOwner(username)){
                                g.removeUser(username, g.getOwners());
                            }
                        }
                        saveFile("src/Multicast/groups.obj", this.mainThread.getGroups());
                        return "type | leave_group ; operation | succeeded";
                    }else{
                        return "type | leave_group ; operation | failed";
                    }
                }case "grant_perks_group":{
                    String username = info[1][1];
                    String username2 = info[2][1];
                    int groupID = Integer.parseInt(info[3][1]);
                    int new_perks = Integer.parseInt(info[4][1]);
                    Group g = findGroup(groupID);
                    int old_perks = g.userPerks(username2);
                    int user_perks = g.userPerks(username);
                    if(new_perks >= user_perks && user_perks < 3 && new_perks < 3 && new_perks < old_perks){
                        User new_user = findUser(username2);
                        g.addEditor(new_user);
                        if(new_perks == 1){
                            g.addOwner(new_user);
                        }
                        saveFile("src/Multicast/groups.obj", this.mainThread.getGroups());
                        return "type | grant_perks_group ; operation | succeeded";
                    }else{
                        return "type | grant_perks_group ; operation | failed";
                    }
                }case "get_requests":{
                    String username = info[1][1];
                    Group g = findGroup(Integer.parseInt(info[2][1]));
                    if(g.isOwner(username)){
                        return "type | get_requests ; operation | succeeded ; list | " + g.getGroupRequests();
                    }else{
                        return "type | get_requests ; operation | failed";
                    }
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
                                toReturn += music.getTitle()+", "+music.getArtist().getName();
                            }
                        }
                    }else if(object.equals("album")){
                        toReturn += "album_list ; item_count | ";
                        CopyOnWriteArrayList<Album> a = findAlbum(keyword);
                        if(a==null)
                            toReturn += "0";
                        else {
                            toReturn += a.size() + " ; item_list | ";
                            for (Album album : a) {
                                toReturn += album.getTitle() + ", " + album.getArtist().getName();
                            }
                        }
                    }else if(object.equals("artist")){
                        toReturn += "artist_list ; item_count | ";
                        CopyOnWriteArrayList<Artist> ar = searchForArtist(keyword);
                        if(ar == null)
                            toReturn += "0";
                        else {
                            toReturn += ar.size() + " ; item_list | ";
                            for (Artist artist : ar) {
                                toReturn += artist.getName();
                            }
                        }
                    }
                    return toReturn;
                }case "add_music": {
                    String username = info[1][1];
                    User current = findUser(username);
                    if (current.getPerks() < 3) { //se é editor ou owner

                        //questão dos grupos ainda tem que ser tratada antes------------------
                        /*String groupIDs = info[2][1];
                        String aux[] = groupIDs.split(",");----------------------------------*/

                        String title = info[2][1];
                        String genre = info[4][1];
                        Artist artist = findArtist(info[3][1]);
                        if (artist == null) artist = new Artist(info[3][1], genre);
                        float duration = Float.parseFloat(info[5][1]);

                        //se já houver uma música com o mesmo title e artist, não se pode adicionar

                        if (findMusic(artist.getName(), title) == null) {
                            //caso este artista ainda não exista, cria-se um novo com a informação que já é dada

                            Music newMusic = new Music(title, artist, genre, duration);

                            newMusic.add_editor(username);

                            /*for(int i = 0; i < aux.length ; i++){
                                newMusic.add_groups(Integer.parseInt(aux[i]));
                            }*/
                            artist.addMusic(newMusic);
                            this.mainThread.getArtists().add(artist);
                            this.mainThread.getSongs().add(newMusic);
                            saveFile("src/Multicast/musics.obj", mainThread.getSongs());
                            saveFile("src/Multicast/artists.obj", mainThread.getArtists());
                            return "type | add_music ; operation | succeeded";
                        }
                    }
                    return "type | add_music ; operation | failed";

                }case "add_artist" : {
                    String username = info[1][1];
                    User current = findUser(username);
                    if (current.getPerks() < 3) {
                        String name = info[2][1];
                        String description = info[3][1];
                        String concerts = info[4][1];
                        Description desc = new Description(info[3][1], current);
                        String genre = info[5][1];

                        String conc[] = concerts.split(",");

                        if (findArtist(name) == null) {
                            Artist newArtist = new Artist(name, desc, new CopyOnWriteArrayList<>(Arrays.asList(conc)), genre);

                            this.mainThread.getArtists().add(newArtist);
                            saveFile("src/Multicast/artists.obj", mainThread.getArtists());
                            return "type | add_artist ; operation | succeeded";
                        }
                    }
                    return "type | add_artist ; operation | failed";
                }case "add_album": {
                    String username = info[1][1];
                    User current = findUser(username);
                    if (current.getPerks() < 3) {
                        String title = info[2][1];
                        String genre = info[7][1];
                        Artist artist = findArtist(info[3][1]);
                        if (artist == null) artist = new Artist(info[3][1], genre);
                        int year = Integer.parseInt(info[5][1]);
                        String musics = info[4][1];
                        musics = musics.replace("<", "");
                        musics = musics.replace(">", "");
                        String mus[] = musics.split(",");

                        String publisher = info[6][1];
                        String description = info[8][1];
                        Description desc = new Description(description, current);
                        CopyOnWriteArrayList<Music> musicList = new CopyOnWriteArrayList<>();

                        for (String m: mus) {
                            Music newMusic = new Music(title, artist, genre);
                            musicList.add(newMusic);
                            if (findMusic(artist.getName(), m) == null) mainThread.getSongs().add(newMusic);
                        }
                        if (findAlbum(title, info[3][1]) == null) {

                            Album newAlbum = new Album(artist, title, year, musicList, publisher, genre, desc);

                            this.mainThread.getAlbums().add(newAlbum);
                            saveFile("src/Multicast/albums.obj", this.mainThread.getAlbums());
                            saveFile("src/Multicast/musics.obj", this.mainThread.getSongs());
                            return "type | add_album ; operation | succeeded";
                        }
                    }
                    return "type | add_album ; operation | failed";

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
                    String username = info[2][1];
                    String new_editor_username = info[3][1];

                    User current = findUser(username);
                    if(current.getPerks()<3){
                        User new_editor = findUser(new_editor_username);
                        if(new_editor == null){
                            return "type | grant_perks ; status | failed";
                        }
                        new_editor.setPerks(2);
                        saveFile("src/Multicast/users.obj", this.mainThread.getRegisteredUsers());
                        return "type | grant_perks ; status | succeeded";
                    }
                    return "type | grant_perks ; status | failed";
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

                    if (!verifyGroups(found.getGroups(), user.getDefaultShareGroups())) return "type | upload ; operation | failed ; message | You don't have access to this music :(";

                    if (found != null) {
                        //siginifica que a música existe e que efetivamente conseguimos associá-la com o ficheiro que aí vem
                        ans = "type | upload ; port | 5500";
                    } else { //findMusic = null => significa que a música não existe, e portanto o request tem que ser recusado
                        return "type | upload ; operation | failed ; message | no such music";
                    }



                    //create new thread
                    TCPWorker newWorker = new TCPWorker(5500, user, found, this.mainThread, this);
                    newWorker.start();

                    return ans;
                }case "get_musics": {
                    String username = info[1][1];

                    User thisUser = findUser(username);

                    String list = "<";
                    int count = 0;

                    for (Music m : thisUser.getTransferredMusics()) {
                        list += m.getTitle() + ":" + m.getArtist().getName() + ",";
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

                    //percorrer todos os users de cada grupo, e adicionar a música às transferred musics
                    for (String ID : groupIDs) {
                        Group group = findGroup(Integer.parseInt(ID));
                        for (User user : group.getUsers()) {
                            user.getTransferredMusics().add(toBeShared);
                        }
                    }

                    return "type | share_music ; operation | succeeded";
                }case "notification": {
                    String username = info[1][1];
                    String notif = info[2][1];
                    User current = findUser(username);
                    Notification newNotif = new Notification(notif);
                    current.getNotifications().add(newNotif);
                    saveFile("src/Multicast/users.obj", mainThread.getRegisteredUsers());

                    return "type | status ; operation | succeeded";

                }case "get_notifications":{
                    String username = info[1][1];
                    User current = findUser(username);
                    System.out.println(current == null);
                    int counter;
                    if((counter = current.getNotifications().size())>0){
                        String reply = "type | get_notifications ; item_count | " + counter + " ; notifications | ";
                        for(Notification n : current.getNotifications()){
                            reply += n.getMessage() + "\n";
                            current.getNotifications().remove(n);
                        }
                        return reply;
                    }else{
                        return "type | get_notifications ; item_count | 0 ; notifications | ";
                    }
                } default:{
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
            String message = translation(this.request);
            System.out.println("Sent to multicast address: " + message);
            byte buffer[] = message.getBytes();
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

class TCPWorker extends Thread {
    int port;
    String serverAddress;
    User user; //user que fez o upload do ficheiro
    Music music;
    MulticastServer mainThread;
    String path;
    RequestHandler thread;

    public TCPWorker(int port, User user, Music music, MulticastServer mainThread, RequestHandler thread){
        super();
        this.port = port;
        this.user = user;
        this.music = music;
        this.mainThread = mainThread;
        this.thread = thread;
    }

    public void run() {
        int port = 5500;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(5500);
            System.out.println("ServerSocket is up");
            clientSocket = serverSocket.accept();

            //all the code for the file download
            saveFile(clientSocket);

            serverSocket.close();
            clientSocket.close();
            System.out.println("Sockets were closed");

            //associates this file with a certain music
            this.music.setPathToFile(this.path);
            this.user.getTransferredMusics().add(music);

            this.thread.saveFile("users.obj", mainThread.getRegisteredUsers());
            this.thread.saveFile("musics.obj", mainThread.getSongs());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile (Socket clientSocket) throws IOException {
        //first of all, "calculate" the path of the new file
        this.path = "src/Multicast/" + this.mainThread.getName() + "/TransferredFiles/" + this.music.getTitle().replaceAll(" ", "") + this.music.getArtist().getName().replaceAll(" ", "") + ".mp3";
        System.out.println("CALCULATED PATH = " + this.path);

        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        FileOutputStream fos = new FileOutputStream(this.path);

        //the first message the server receives is the file size
        int fileSize = dis.readInt();
        System.out.println("Received file size = " + fileSize);

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
    }
}