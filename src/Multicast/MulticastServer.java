package Multicast;

import Interface.*;
import FileHandling.*;
//import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.net.*;
import java.io.*;
import java.io.IOException;
import java.nio.Buffer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread {
    private ObjectFile usersObjectFile; //file for registered users
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4323;
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

        System.out.println("File has been read... Registered users:");

        if(this.getGroups().size()>0){
            Group g = this.groups.get(0);
            Iterator it = g.getUsers().iterator();


            while (it.hasNext()){
                User aux = (User)it.next();
                System.out.println(aux.getUsername());
            }

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


class requestHandler extends Thread{ //handles request and sends answer back to RMI
    private String request;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4324;
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

    /*public String searchAlbums (String keyword) {

        CopyOnWriteArrayList<Album> albums = this.mainThread.getAlbums();
        String reply;
        for (Album a :albums) {}
    }*/

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

    private void saveFile(String filename, Object o){
        try {
            mainThread.getUsersObjectFile().openWrite(filename);
            mainThread.getUsersObjectFile().writesObject(o);
            mainThread.getUsersObjectFile().closeWrite();
        } catch (IOException e) {
            System.out.println("Could not openWrite to file " + mainThread.getPathToObjectFiles() + "src/Multicast/users.obj");
        }
    }

    private boolean verifyGroups(CopyOnWriteArrayList<Integer> musicGroups, CopyOnWriteArrayList<Group> userGroups){
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
                System.out.println("What!");
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
                    saveFile("src/Multicast/groups.obj", mainThread.getGroups());
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

                    return "type | status ; operation | succeeded ; perks | " +perks;


                }case "logout":{
                    String username = info[1][1];
                    for(User u : this.mainThread.getLoggedOn()){
                        if(u.getUsername().equals(username)){
                            this.mainThread.getLoggedOn().remove(u);
                            break;
                        }
                    }
                    return "type | status ; operation | succeeded";

                }case "groups": {
                    String username = info[1][1];
                    User current = findUser(username);
                    return "type | groups ; item_count | "+ getAvailableGroups(current);
                }case "new_group": {
                    String username = info[1][1];
                    User current = findUser(username);
                    int groupID = mainThread.getGroups().size() + 1;
                    Group g = new Group(current, groupID);
                    current.getDefaultShareGroups().add(g);
                    current.setPerks(1);
                    mainThread.getGroups().add(g);
                    saveFile("src/Multicast/groups.obj", mainThread.getGroups());
                    return "type | new_group ; groupID | " + groupID + " ; operation | succeeded";
                }case "join_group": {
                    String username = info[1][1];
                    int groupID = Integer.parseInt(info[2][1]);
                    User current = findUser(username);
                    Group g = findGroup(groupID);
                    if(!g.isUser(current.getUsername())) {
                        for(User request : g.getRequests()){
                            if(request.getUsername().equals(username)){
                                return "type | join_group ; status | failed ; error | You've already asked to join group " + groupID + "!";
                            }
                        }
                        g.addRequest(current);
                        saveFile("src/Multicast/groups.obj", this.mainThread.getGroups());
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
                        if(request.equals("accept")){
                            new_user.getDefaultShareGroups().add(g);
                            g.addUser(new_user);
                            g.removeRequest(new_user.getUsername());
                            saveFile("src/Multicast/groups.obj", this.mainThread.getGroups());
                            return "type | manage_request ; status | succeeded ; operation | accept";
                        }else{
                            g.removeRequest(new_user.getUsername());
                            saveFile("src/Multicast/groups.obj", this.mainThread.getGroups());
                            return "type | manage_request ; status | succeeded ; operation | decline";
                        }
                    }else{
                        return "type | manage_request ; operation | failed";
                    }
                }case "expell_user":{
                    String username = info[1][1];
                    String expelled_user = info[2][1];
                    Group g = findGroup(Integer.parseInt(info[3][1]));
                    if(g.isOwner(username) && !g.isOwner(expelled_user) && g.getGroupID()!=1){
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
                        User new_user = findUser(username2);
                        g.addEditor(new_user);
                        new_user.setPerks(2);
                        if(new_perks == 1){
                            g.addOwner(new_user);
                            new_user.setPerks(1);
                        }
                        saveFile("src/Multicast/groups.obj", this.mainThread.getGroups());
                        return "type | grant_perks ; status | succeeded";
                    }else{
                        return "type | grant_perks ; status | failed ; error | You don't have permission to give " + perks + "perks in group " + groupID + "!";
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
                                toReturn += artist.getName();
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
                        for(Group g : current.getDefaultShareGroups()){
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
                                mainThread.getArtists().add(artist);
                            }
                        //se já houver uma música com o mesmo title e artist, não se pode adicionar

                            //caso este artista ainda não exista, cria-se um novo com a informação que já é dada

                            Music newMusic = new Music(title, info[4][1], genre, duration);

                            newMusic.add_editor(username);

                            for(int i = 0; i < aux.length ; i++){
                                newMusic.add_groups(Integer.parseInt(aux[i]));
                            }
                            artist.addMusic(newMusic);
                            this.mainThread.getArtists().add(artist);
                            this.mainThread.getSongs().add(newMusic);
                            saveFile("src/Multicast/artists.obj", mainThread.getArtists());
                            saveFile("src/Multicast/musics.obj", mainThread.getSongs());

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
                            saveFile("src/Multicast/artists.obj", mainThread.getArtists());
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
                            if(g.getGroupID() == Integer.parseInt(aux[i])){
                                System.out.println(aux[i]);
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
                            int year = Integer.parseInt(info[6][1]);
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
                                saveFile("src/Multicast/musics.obj", mainThread.getSongs());
                            }
                            Album newAlbum = new Album(artist, title, year, musicList, publisher, genre, desc);
                            for (Integer i : groups) {
                                newAlbum.add_groups(i);
                            }
                            artist.getAlbums().add(newAlbum);
                            this.mainThread.getAlbums().add(newAlbum);
                            saveFile("src/Multicast/artists.obj", this.mainThread.getArtists());
                            saveFile("src/Multicast/albums.obj", this.mainThread.getAlbums());
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
                    String answer = "type | get_info ;  info | ";
                    System.out.println("Length: " + info.length);
                    for(int i = 0; i < info.length; i++){
                        System.out.println(info[i][0]);
                    }
                    if (info[1][0].equals("username") && info[2][0].equals("object") && info[3][0].equals("title") && info.length == 4) {
                        if (info[2][1].equals("artist")) {
                            User u = findUser(info[1][1]);
                            Artist a = findArtist(info[3][1]);
                            if (a == null) {
                                System.out.println("Hello2");
                                return "type | get_info ; status | failed ; error | That artist doesn't exist!\n";
                            }
                            for (int i = 0; i < mainThread.getGroups().size(); i++) {
                                if (verifyGroups(a.getGroups(), u.getDefaultShareGroups())) {
                                    answer += "----------| Artist |----------\n" + a.getName();
                                    System.out.println(a.getAlbums().size());
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
                            return "type | get_info ; status | failed ; error | That artist isn't shared with a group that you belong to!";
                        }
                    }else if(info[1][0].equals("username") && info[2][0].equals("object") && info[3][0].equals("title") && info.length == 5){
                        if (info[2][1].equals("album")) {
                            User u = findUser(info[1][1]);
                            Album a = findAlbum(info[3][1], info[4][1]);
                            if(a==null) {
                                return "type | get_info ; status | failed ; error | That album doesn't exist!";
                            }
                            for(int i=0 ; i<mainThread.getGroups().size();i++) {
                                if (verifyGroups(a.getGroups(), u.getDefaultShareGroups())) {
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
                                    return answer;
                                }
                            }
                            return "type | get_info ; status | failed ; error | That artist isn't shared with a group that you belong to!";
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

                            saveFile("src/Multicast/albums.obj", this.mainThread.getAlbums());
                            return "type | status ; review | successful";
                        }
                        else
                            return "type | status ; review | failed ; error | That album doesn't exist!";

                    } else {
                        return "type | status ; command | invalid";
                    }
                }case "test": {
                    test();
                    return "type | status ; command | tested";
                }case "upload": {
                    //há um user a querer fazer upload de um ficheiro
                    String username = info[1][1];
                    String musicTitle = info[2][1];
                    ServerSocket welcomeSocket = null;
                    Socket connectionSocket = null;
                    InputStream is = null;
                    FileOutputStream fos = null;
                    BufferedOutputStream bos = null;
                    int bytesRead;
                    int current = 0;

                    //ligação TCP
                    try {
                        welcomeSocket = new ServerSocket(5000);

                        connectionSocket = welcomeSocket.accept();
                        System.out.println("Accepted connection " + connectionSocket);

                        is = connectionSocket.getInputStream();

                        //fos = new FileOutputStream(path_to_file) - não sei como isto vai funcionar...
                        //bos = new BufferedOutputStream(fos);
                        byte[] file = new byte[16 * 1024];
                        bytesRead = is.read(file, 0, file.length);
                        current = bytesRead;
                    } catch (IOException e) {
                        System.out.println("Exception: " + e.getStackTrace());
                    }
                }case "notification": {
                    String username = info[1][1];
                    String notif = info[2][1];
                    User current = findUser(username);
                    Notification newNotif = new Notification(notif);
                    current.getNotifications().add(newNotif);
                    saveFile("src/Multicast/groups.obj", findGroup(1).getUsers());

                    return "type | status ; operation | succeeded";

                }case "get_notifications":{
                    String username = info[1][1];
                    User current = findUser(username);
                    System.out.println(current == null);
                    int counter;
                    if(current.getNotifications()!=null) {
                        if ((counter = current.getNotifications().size()) > 0) {
                            String reply = "type | get_notifications ; item_count | " + counter + " ; notifications | ";
                            for (Notification n : current.getNotifications()) {
                                reply += n.getMessage() + "\n";
                                current.getNotifications().remove(n);
                                saveFile("src/Multicast/groups.obj", findGroup(1).getUsers());
                            }
                            return reply;
                        } else {
                            return "type | get_notifications ; item_count | 0 ; notifications | ";
                        }
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