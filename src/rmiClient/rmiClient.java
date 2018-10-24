package rmiClient;

import rmi.Services;

import javax.xml.bind.SchemaOutputResolver;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class rmiClient extends UnicastRemoteObject implements Clients  {

    private static Scanner sc = new Scanner(System.in);
    private static Clients c;
    private static Services rmi;
    // o que estou a pensar é, no ato do login e em cada alteração atualizar esta lista para ser mais simples enviar pedidos ao RMI
    // um exemplo da lista podia ser [(<grupo> <role>) (<grupo> <role>) (...)]
    // desta maneira quando formos fazer um pedido ao RMI para mexer em algum grupo, enviamos logo a informação do grupo que ele que alterar
    // e sabemos logo a partir do role se ele pode fazer essas alterações ou não
    private static String user=null;
    private static int perk=0;
    private static int port;

    private rmiClient() throws RemoteException {
    }


    private static Services getRmi() throws RemoteException {
        return rmi;
    }

    private static void setPort(int p){
        port=p;
    }

    private static void setC() throws RemoteException, InterruptedException{
        while(true) {
            try {
                Registry registry = LocateRegistry.createRegistry(port);
                System.out.println(c);
                registry.rebind("Benfica", c);
                rmi.newClient(port);
                break;
            } catch (ExportException e) {
                UnicastRemoteObject.unexportObject(c, true);

            }
        }
    }

    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException{
        c = new rmiClient();
        establishRMIConnection();
        setPort(getRmi().hello());
        firstMenu();
    }

    private static void establishRMIConnection(){
        try {
            rmi = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting");
        }catch (RemoteException | NotBoundException e) {
            retryRMIConnection();
        }
    }

    private static void retryRMIConnection(){
        while(true){
            try {
                Thread.sleep(1000);
                rmi = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting");
                port=rmi.hello();
                setC();
                break;
            }catch (RemoteException | NotBoundException e) {
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void firstMenu(){
        int option;
        boolean verifier=false;
        System.out.println("");
        System.out.println("  ------------");
        System.out.println("  | WELCOME! |");
        System.out.println("  ------------");

        while(true) {
            System.out.println("----------------");
            System.out.println("| 1) Register  |");
            System.out.println("| 2) Login     |");
            System.out.println("| 3) Exit      |");
            System.out.println("----------------");
            try {
                option = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
                if (option == 1 || option == 2) {
                    validationMenu(option);
                    break;
                }
                else if (option == 3){
                    if (user != null) {
                        while (!verifier) {
                            try {
                                verifier = rmi.logout(user);
                            } catch (RemoteException e) {
                                retryRMIConnection();
                            }
                        }
                    }
                    break;
                }
                System.out.println("Please select a valid option\n");
            }catch (NumberFormatException e) {
                System.out.println("I only work with numbers bro! Try again...\n");
            }
        }
    }

    private static void validationMenu(int modifier) {
        String username, password = null;
        int verifier;
        boolean validation;
        System.out.println("(you can type '0' at any time to exit)");
        while (true) {
            System.out.print("\nUsername: ");
            username = sc.nextLine().replaceAll("^[,\\s]+", "");
            if (username.equals("0")) {
                break;
            }
            if (username.contains(" ")) {
                System.out.println("Username cannot contain spaces");
                continue;
            }
            validation = stringChecker(username);
            if (!validation)
                continue;
            System.out.print("\nPassword: ");
            password = sc.nextLine().replaceAll("^[,\\s]+", "");
            if (password.equals("0")) {
                break;
            }
            if (password.contains(" ")) {
                System.out.println("Password cannot contain spaces");
                continue;
            }
            validation = stringChecker(password);
            if (!validation)
                continue;
            while (true) {
                try {
                    //funcao de registar e login tem que devolver um boolean
                    if (modifier == 1) //registar
                        verifier = rmi.register(username, password);
                    else //login
                        verifier = rmi.login(username, password);
                    if (verifier <= 4) { //1- owner de algum grupo, 2- editor de algum grupo, 3- normal, 4-nao existe/credencias mal;
                        if (modifier == 1)
                            System.out.println("User registed successfully!");
                        else
                            System.out.println("Logged in successfully!");
                        user = username;
                        perk = verifier;
                        setC();
                        mainMenu();
                        return;
                    } else {
                        if (modifier == 1)
                            System.out.println("Username already exists. Please chose another one\n");
                        else
                            System.out.println("Invalid Credentials!");
                    }
                } catch (RemoteException e) {
                    retryRMIConnection();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void addChangeInfoMenu() { //apenas os editors têm acesso a este menu

        int option;
        boolean validation;
        boolean res = false;

        while (true) {
            System.out.println("Choose one of the following options: ");
            System.out.println("1) Add new content (musics, albums or artists)");
            System.out.println("2) Change info of existing content");
            System.out.println("0) Go back");
            try {
                option = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
            } catch (NumberFormatException e) {
                System.out.println("I can only work with numbers bro!");
                continue;
            }
            if (option == 0) {
                return;
            }
            if (option == 1) { //adding new content
                while (true) {
                    System.out.println("What kind of content do you want to add?");
                    System.out.println("1) Music");
                    System.out.println("2) Artist");
                    System.out.println("3) Album");
                    System.out.println("0) Go back");

                    try {
                        option = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
                    } catch (NumberFormatException e) {
                        System.out.println("I can only work with numbers bro!");
                        continue;
                    }

                    if (option == 1) { //user wants to add a new music
                        String title=null, artist=null, genre=null, duration=null;
                        while (true) {
                            System.out.println("Music title: ");
                            title = sc.nextLine();
                            if (title.equals("0")) {
                                break;
                            }

                            validation=stringChecker(title);
                            if(!validation)
                                continue;

                            System.out.println("Artist: ");
                            artist = sc.nextLine();
                            if (artist.equals("0")) {
                                break;
                            }

                            validation=stringChecker(artist);
                            if(!validation)
                                continue;

                            System.out.println("Music Genre: ");
                            genre = sc.nextLine();
                            if (genre.equals("0")) {
                                break;
                            }

                            validation=stringChecker(genre);
                            if(!validation)
                                continue;

                            System.out.println("Duration: ");
                            duration = sc.nextLine();
                            if (duration.equals("0")) {
                                break;
                            }

                            validation=stringChecker(duration);
                            if(!validation)
                                continue;

                            break;
                        }
                        try{
                            res = rmi.addInfo(user, "music", title, artist, genre, duration);
                        } catch (RemoteException e){
                            retryRMIConnection();
                        }
                    }

                    if (option == 2) { //user wants to add a new artist
                        String name=null, description=null, concerts=null, genre=null;

                        while (true) {
                            System.out.println("Artist name: ");
                            name = sc.nextLine();
                            if (name.equals("0")) {
                                break;
                            }

                            validation=stringChecker(name);
                            if(!validation)
                                continue;

                            System.out.println("Artist description: ");
                            description = sc.nextLine();
                            if (description.equals("0")) {
                                break;
                            }

                            validation=stringChecker(description);
                            if(!validation)
                                continue;

                            System.out.println("Next concerts (separated by \",\", listed by month/day/year-concert_venue-city-country): ");
                            concerts = sc.nextLine();
                            if (concerts.equals("0")) {
                                break;
                            }

                            validation=stringChecker(concerts);
                            if(!validation)
                                continue;

                            System.out.println("Genre: ");
                            genre = sc.nextLine();
                            if (genre.equals("0")) {
                                break;
                            }

                            validation=stringChecker(genre);
                            if(!validation)
                                continue;
                            break;
                        }
                        try {
                            res = rmi.addInfo(user, "artist", name, description, concerts, genre);
                        }catch (RemoteException e){
                            retryRMIConnection();
                        }
                    }

                    if (option == 3) { //user wants to add a new album
                        String artist=null, title=null, musics=null, year=null, publisher=null, genre=null, description=null;

                        while (true) {
                            System.out.println("Album title: ");
                            title = sc.nextLine();
                            if (title.equals("0")) {
                                break;
                            }

                            validation=stringChecker(title);
                            if(!validation)
                                continue;

                            System.out.println("Album artist: ");
                            artist = sc.nextLine();
                            if (artist.equals("0")) {
                                break;
                            }

                            validation=stringChecker(artist);
                            if(!validation)
                                continue;

                            System.out.println("Music list separated by commas:");
                            musics = sc.nextLine();
                            if (musics.equals("0")) {
                                break;
                            }

                            validation=stringChecker(musics);
                            if(!validation)
                                continue;

                            System.out.println("Year of publication: ");
                            year = sc.nextLine();
                            if (year.equals("0")) {
                                break;
                            }

                            validation=stringChecker(year);
                            if(!validation)
                                continue;

                            System.out.println("Publisher: ");
                            publisher = sc.nextLine();
                            if (publisher.equals("0")) {
                                break;
                            }

                            validation=stringChecker(publisher);
                            if(!validation)
                                continue;

                            System.out.println("Genre: ");
                            genre = sc.nextLine();
                            if (genre.equals("0")) {
                                break;
                            }

                            validation=stringChecker(genre);
                            if(!validation)
                                continue;

                            System.out.println("Album description: ");
                            description = sc.nextLine();
                            if (description.equals("0")) {
                                break;
                            }

                            validation=stringChecker(description);
                            if(!validation)
                                continue;

                            break;
                        }
                        try {
                            res = rmi.addInfo(user, artist, title, musics, year, publisher, genre, description);
                        }catch(RemoteException e){
                            retryRMIConnection();
                        }
                    }

                    break;
                }
                if (res) {//success
                    System.out.println("New information successfully added!");
                    return;
                }
                else
                    System.out.println("Could not add new information :(");
            }
            //-----------------------NÃO ESTÁ FEITO PORQUE É PRECISO A FUNÇÃO DE SEARCH--------------------------------------------------------------------------------
            /*if (option == 2) { //changing existing content

                while (true) {
                    System.out.println("What kind of content do you want to change?");
                    System.out.println("1) Music");
                    System.out.println("2) Artist");
                    System.out.println("3) Album");
                    System.out.println("0) Go back");


                    //search for which object you want to change
                }

            }*/

            //QUESTÃO DA PARTILHA POR GRUPOS
        }
    }

    private static void mainMenu(){
        int option;
        boolean verifier=false;
        while(true) { //1- owner de algum grupo, 2- editor de algum grupo, 3- normal
            System.out.println("----------------| Main Menu |----------------");
            System.out.println("| 1) Search                                 |");
            System.out.println("| 2) Album and Artist details               |");
            System.out.println("| 3) Album Review                           |");
            System.out.println("| 4) Upload Music                           |");//falta
            System.out.println("| 5) Download Music                         |");//falta
            System.out.println("| 6) Share Music                            |");//falta
            System.out.println("| 7) Create Group                           |");
            System.out.println("| 8) Join Group                             |");
            if (perk<3){ // se for editor ou owner
                System.out.println("| 9) Manage Groups                          |");
                System.out.println("| 10) Give 'Editor' privileges              |");
                System.out.println("| 11) Add/change info                       |");

            }
            if(perk<2){ // se for owner
                System.out.println("| 12) Give 'Owner' privileges               |");
                System.out.println("| 13) Manage group requests                 |");//falta
            }
            System.out.println("| 0) Logout                                 |");
            System.out.println("---------------------------------------------");
            try {
                option = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
            } catch (NumberFormatException e){
                System.out.println("I can only work with numbers bro!");
                continue;
            }
            if (option == 0) {
                while (!verifier) {
                    try {
                        verifier = rmi.logout(user);
                    } catch (RemoteException e) {
                        retryRMIConnection();
                    }
                }
                break;
            }
            if (option == 1)
                searchMenu();
            else if(option == 2)
                detailsMenu();
            else if(option == 3)
                reviewMenu();
            else if(option == 4)
                uploadMenu();
            else if(option == 5)
                System.out.println();
                // continue
            else if(option == 6)
                System.out.println();
                // continue
            else if(option == 7)
                createGroupMenu();
            else if(option == 8)
                joinGroupMenu();
            else if(option == 9 || option == 10 || option == 11){
                if(perk==3){
                    System.out.println("Please select one of the given options");
                    continue;
                }
                if(option == 9)
                    manageGroup();
                if(option == 10)
                    givePermissionsMenu("editor");
                if (option == 11)
                    addChangeInfoMenu();
            }
            else if(option == 12 || option == 13){
                if (perk>1){
                    System.out.println("Please select one of the given options");
                    continue;
                }
                if(option == 12)
                    givePermissionsMenu("owner");
                //if(option == 12)
                // continue
            }
             else
                System.out.println("Please select one of the given options");
        }
    }

    private static void uploadMenu() {
        System.out.println("You have to associate your music file with one of the musics info in our DB\n");
        boolean validation = false;
        String keyword = "", answer = "";

        while (!validation){
            System.out.println("----------------| Search |----------------");
            System.out.println("| Insert your keyword(s):                |");
            System.out.println("------------------------------------------");
            keyword = sc.nextLine().replaceAll("^[,\\s]+", "");
            validation = stringChecker(keyword);
        }
        //search for the keyword
        try {
            answer = rmi.search(keyword, "music");
        } catch (RemoteException e) {
            retryRMIConnection();
        }

        //warn the server that you will be sending a music file to associate with a certain file
        try {
            rmi.uploadFile(user, answer);
        }catch (RemoteException e) {
            retryRMIConnection();
        }

        uploadFile(user, answer);

    }

    //function to communicate with multicast server
    private static void TCPServerConnection(String username, String music, String path) throws UnknownHostException, IOException {
        String serverIP = "127.0.0.1";
        int serverPort = 5000;

        //opens a new socket in any avaliable port
        Socket sock = new Socket(serverIP, serverPort);

        File file = new File(path);
        long length = file.length();
        byte[] bytes = new byte[16*1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = sock.getOutputStream();

        //write file content to output stream
        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        sock.close();
    }

    private static void uploadFile(String username, String music) {
        boolean validation = false;
        String path = null;

        while (!validation){
            System.out.println("-------------------| Search |-------------------");
            System.out.println("| Insert the path to the file you want to upload |");
            System.out.println("------------------------------------------------");
            path = sc.nextLine().replaceAll("^[,\\s]+", "");
            validation = stringChecker(path);

            try {
                TCPServerConnection(username, music, path);
            } catch (IOException e) {
                System.out.println("There was an exception: " + e);
            }
        }

    }

    private static void searchMenu(){
        int ob;
        String keyword=null, object, answer=null;
        boolean validation=false;
        while(true) {
            System.out.println("----------------| Search |----------------");
            System.out.println("| What are you searching for:            |");
            System.out.println("| 1) Music                               |");
            System.out.println("| 2) Album                               |");
            System.out.println("| 3) Artist                              |");
            System.out.println("| 0) Back                                |");
            System.out.println("------------------------------------------");
            ob = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
            if (ob == 0) {
                break;
            }
            else if (ob > 3 || ob < 0)
                System.out.println("Please select a valid option");
            else{
                while(!validation){
                    System.out.println("----------------| Search |----------------");
                    System.out.println("| Insert your keyword(s):                |");
                    System.out.println("------------------------------------------");
                    keyword = sc.nextLine().replaceAll("^[,\\s]+", "");
                    validation = stringChecker(keyword);
                }
                if(ob == 1)
                    object="music";
                else if(ob == 2)
                    object="album";
                else
                    object="artist";
                while(answer==null) {
                    try {
                        answer = rmi.search(keyword, object);
                    } catch (RemoteException e) {
                        retryRMIConnection();
                    }
                }
                System.out.println(answer);
                break;
            }
        }
    }

    private static void detailsMenu(){
        int ob;
        String object, title=null, answer=null;
        boolean validation=false;
        while(true) {
            System.out.println("----------------| Details |----------------");
            System.out.println("| Know more about:                        |");
            System.out.println("| 1) Album                                |");
            System.out.println("| 2) Artist                               |");
            System.out.println("| 0) Back                                 |");
            System.out.println("-------------------------------------------");
            ob = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
            if (ob == 0) {
                break;
            }
            else if (ob > 2 || ob < 0)
                System.out.println("Please select a valid option");
            else{
                if(ob==1)
                    object = "album";
                else
                    object = "artist";
                while(!validation){
                    System.out.println("----------------| Details |----------------");
                    System.out.println("| Insert "+object+" name:                     |");
                    System.out.println("-------------------------------------------");
                    title = sc.nextLine().replaceAll("^[,\\s]+", "");
                    validation = stringChecker(title);
                }
                while(answer==null) {
                    try {
                        answer = rmi.details(object, title);
                    } catch (RemoteException e) {
                        retryRMIConnection();
                    }
                }
                System.out.println(answer);
                break;
            }
        }
    }


    private static void reviewMenu(){
        int rating;
        String review=null, title=null;
        boolean validation=false, verifier=false;
        while(!validation) {
            System.out.println("----------------| Reviews |----------------");
            System.out.println("| Insert the album title:                 |");
            System.out.println("-------------------------------------------");
            title=sc.nextLine().replaceAll("^[,\\s]+", "");
            validation = stringChecker(title);
        }
        validation=false;
        while(!validation) {
            System.out.println("----------------| Reviews |----------------");
            System.out.println("| Write a review (maximum 300 words):     |");
            System.out.println("-------------------------------------------");
            review=sc.nextLine().replaceAll("^[,\\s]+", "");
            validation = stringChecker(review);
        }
        while(true) {
            System.out.println("----------------| Reviews |----------------");
            System.out.println("| Rate the album (1 to 5):                |");
            System.out.println("-------------------------------------------");
            try {
                rating = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
            } catch (NumberFormatException e) {
                System.out.println("Please rate the album from 1 to 5");
                continue;
            }
            if (rating < 1 || rating > 5) {
                System.out.println("Please rate the album from 1 to 5");
                continue;
            }
            break;
        }
        validation=false;
        while(!validation){
            try{
                verifier=rmi.review(title,user,review,rating);
                validation=true;
            } catch (RemoteException e) {
                retryRMIConnection();
            }
        }
        if(verifier)
            System.out.println("Reviewed "+title+" successfully!");
        else
            System.out.println("Something went wrong! Maybe that album does not exist...");
    }

    private static void createGroupMenu(){
        String groupID;
        while(true){
            try {
                groupID = rmi.newGroup(user);
                break;
            } catch (RemoteException e){
                retryRMIConnection();
            }
        }
        System.out.println("----------------| Create Group |----------------");
        if (groupID==null)
            System.out.println("| Something went wrong. Try again later.       |");
        else {
            System.out.println("| Group " + groupID + " created successfully!           |");
            if(perk!=1)
                perk=1;
        }
        System.out.println("------------------------------------------------");
    }

    private static void joinGroupMenu(){
        int option;
        String groups=null;
        boolean verifier=false, validation;
        while (!verifier) {
            try {
                groups = rmi.showGroups(user);
                verifier = true;
            } catch (RemoteException e) {
                retryRMIConnection();
            }
        }
        if(groups==null){
            System.out.println("----------------| Join Group |----------------");
            System.out.println("| There are no groups for you to join!       |");
            System.out.println("----------------------------------------------");
        }
        else {
            String[] splitted = groups.replaceAll("^[,\\s]+", "").split(",");
            while (true) {
                System.out.println("----------------| Join Group |----------------");
                for (int i = 0; i < splitted.length; i++) {
                    System.out.println(" " + (i + 1) + ") " + splitted[i]);
                }
                System.out.println(" 0) Back");
                System.out.println("----------------------------------------------");
                System.out.print("Choose a group: ");
                try {
                    option = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
                } catch (NumberFormatException e) {
                    System.out.println("I can only work with numbers bro!");
                    continue;
                }
                if (option == 0)
                    break;
                else if (option > 0 && option < splitted.length) {
                    while (true) {
                        try {
                            validation = rmi.joinGroup(user, splitted[option - 1]);
                            break;
                        } catch (RemoteException e) {
                            retryRMIConnection();
                        }
                    }
                    if (validation)
                        System.out.println("Request successfully sent to group owner");
                    else
                        System.out.println("Something went wrong, please try again later");
                    break;
                } else
                    System.out.println("Please select one of the given options");
            }
        }
    }

    private static void manageGroup(){
        String groupID=null, text=null, object, objectName=null;
        int ob;
        boolean validation=false;
        String verifier;
        while(true) {
            System.out.println("----------------| Manage Group |----------------");
            System.out.println("| Change information about:                    |");
            System.out.println("| 1) Album                                     |");
            System.out.println("| 2) Artist                                    |");
            System.out.println("| 3) Music                                     |");
            System.out.println("| 0) Back                                      |");
            System.out.println("------------------------------------------------");
            ob = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
            if (ob == 0) {
                break;
            } else if (ob > 3 || ob < 0)
                System.out.println("Please select a valid option");
            else {
                if(ob==1)
                    object = "album";
                else if(ob==2)
                    object = "artist";
                else
                    object = "music";
                while(!validation){
                    System.out.println("-----------------| Manage Group |-----------------");
                    System.out.println("| Insert the name of the "+object+"                |");
                    System.out.println("-------------------------------------------------");
                    objectName = sc.nextLine().replaceAll("^[,\\s]+", "");
                    validation = stringChecker(objectName);
                }
                validation=false;
                while(!validation){
                    System.out.println("-----------------| Manage Group |-----------------");
                    System.out.println("| Insert new info about "+objectName+"             |");
                    System.out.println("--------------------------------------------------");
                    text = sc.nextLine().replaceAll("^[,\\s]+", "");
                    validation = stringChecker(text);
                }
                validation=false;
                while(!validation){
                    System.out.println("-----------------| Manage Group |-----------------");
                    System.out.println("| Group in which you want to make these changes? |");
                    System.out.println("--------------------------------------------------");
                    groupID = sc.nextLine().replaceAll("^[,\\s]+", "");
                    validation = stringChecker(groupID);
                }
                while(true){
                    try{
                        verifier=rmi.changeInfo(object, objectName, text, user, groupID);
                        break;
                    }catch (RemoteException e){
                        retryRMIConnection();
                    }
                }
                System.out.println(verifier);
                break;
            }
        }
    }

    private static void givePermissionsMenu(String perk) {
        String username=null, groupID=null;
        boolean validation=false;
        String verifier;
        System.out.println("(you can type '0' at any time to exit)");
        while (!validation){
            System.out.println("----------------| Permissions Menu |----------------");
            System.out.println("| Name of the new "+perk+"?                          |");
            System.out.println("----------------------------------------------------");
            username = sc.nextLine().replaceAll("^[,\\s]+", "");
            if (username.equals("0")) {
                return;
            }
            if (username.contains(" ")) {
                System.out.println("Username cannot contain spaces");
                continue;
            }
            validation = stringChecker(username);
        }
        validation=false;
        while (!validation){
            System.out.println("----------------| Permissions Menu |----------------");
            System.out.println("| Group in which you want to make these changes?   |");
            System.out.println("----------------------------------------------------");
            groupID = sc.nextLine().replaceAll("^[,\\s]+", "");
            if (groupID.equals("0")) {
                return;
            }
            validation = stringChecker(groupID);
        }
        while(true){
            try{
                verifier=rmi.givePermissions(perk,user,username,groupID);
                break;
            }catch(RemoteException e){
                retryRMIConnection();
            }
        }
        System.out.println(verifier);
    }

    private static boolean stringChecker (String toCheck){
        if(toCheck==null) {
            System.out.println("String is NULL. Please type something");
            return false;
        }
        if (toCheck.contains("|") || toCheck.contains(";")) {
            System.out.println("String contains forbidden characters ('|' or ';')\n");
            return false;
        }
        return true;
    }

    public void notification (String message) throws RemoteException{
        System.out.println("----------------| New Notification |----------------");
        System.out.println(message);
    }

    public String getUsername() throws RemoteException {
        return user;
    }

}