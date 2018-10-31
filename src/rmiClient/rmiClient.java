package rmiClient;

import Interface.Music;
import rmi.Services;
import static java.lang.Math.toIntExact;

import java.awt.font.NumericShaper;
import java.io.*;
import java.net.*;
import java.net.Socket;
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
    private static String user=null;
    private static int perk=0;
    private static int port;
    private static String host;

    private rmiClient() throws RemoteException {
    }


    private static Services getRmi() throws RemoteException {
        return rmi;
    }

    private static void setPort(int p){
        port=p;
    }

    private static void setC() throws RemoteException, InterruptedException{ //criar o registo da interface no client
        while(true) {
            try {
                Registry registry = LocateRegistry.createRegistry(port);
                registry.rebind("Benfica", c);
                rmi.newClient(port);
                break;
            } catch (ExportException e) {
                UnicastRemoteObject.unexportObject(c, true);

            }
        }
    }

    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException{
        host = args[0];
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                while(true) {
                    try {
                        if(user!=null)
                            rmi.logout(user);
                        break;
                    } catch (RemoteException e) {
                        retryRMIConnection();
                    }
                }
            }
        });
        c = new rmiClient();
        establishRMIConnection();
        firstMenu();
        System.exit(1);
    }

    private static void establishRMIConnection(){
        try {
            rmi = (Services) LocateRegistry.getRegistry(host, 7000).lookup("Sporting");
        }catch (RemoteException | NotBoundException e) {
            retryRMIConnection();
        }
    }

    private static void retryRMIConnection(){
        while(true){
            try {
                Thread.sleep(1000);
                rmi = (Services) LocateRegistry.getRegistry(host, 7000).lookup("Sporting");
                port=rmi.hello();
                if(user!=null)
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
                else
                System.out.println("Please select a valid option\n");
            }catch (NumberFormatException e) {
                System.out.println("I only work with numbers bro! Try again...\n" + e.getMessage());
            }
        }
    }

    private static void validationMenu(int modifier) {
        String username, password;
        int verifier=0;
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
            if (!validation) {
                continue;
            }
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
            while(true) {
                try {
                    //funcao de registar e login tem que devolver um boolean
                    if (modifier == 1) //registar
                        verifier = rmi.register(username, password);
                    else //login
                        verifier = rmi.login(username, password);
                    break;
                } catch (RemoteException e) {
                    retryRMIConnection();
                }
            }
            if (verifier < 4) { //1- owner de algum grupo, 2- editor de algum grupo, 3- normal, 4-ja existe/credencias mal, 5-user ja esta logado;
                if (modifier == 1)
                    System.out.println("User registered successfully!");
                else
                    System.out.println("Logged in successfully!");
                user = username;
                perk = verifier;
                while(true) {
                    try {
                        port = rmi.hello();
                        break;
                    } catch (RemoteException e) {
                        retryRMIConnection();
                    }
                }
                try {
                    setC();
                } catch (RemoteException | InterruptedException e) {
                    e.printStackTrace();
                }
                mainMenu();
                return;
            } else {
                if(verifier==4) {
                    if (modifier == 1)
                        System.out.println("Username already exists. Please choose another one!");
                    else
                        System.out.println("Username doesn't exist!");
                }
                else{
                    System.out.println("Wrong password!");
                }
            }
        }
    }

    private static void addChangeInfoMenu() { //apenas os editors têm acesso a este menu
        String object;
        int op=0,option;
        boolean validation=false;
        String res;

        while (true) {
            System.out.println("Choose one of the following options: ");
            System.out.println("1) Add new content (musics, albums or artists)");
            System.out.println("2) Change info of existing content");
            System.out.println("0) Go back");
            try {
                op = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
            } catch (NumberFormatException e) {
                System.out.println("I can only work with numbers bro!");
            }
            if(op!=0 && op!=1 && op!=2)
                System.out.println("Please chose one of the given options");
            else
                break;
        }
        if (op == 0) {
            return;
        }
        while (true) {
            if (op == 1)
                System.out.println("What kind of content do you want to add?");
            else
                System.out.println("What kind of content do you want to change?");
            System.out.println("1) Music");
            System.out.println("2) Artist");
            System.out.println("3) Album");
            System.out.println("0) Go back");

            try {
                option = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
                break;
            } catch (NumberFormatException e) {
                System.out.println("I can only work with numbers bro!");
            }
        }

        if (option == 1) { //user wants to add a new music
            object = "music";
            String groups=null, title = null, artist=null, genre=null, duration=null;
            while (!validation) {
                if (op == 1)
                    System.out.println("Where you want to add the " + object + " (group IDs): ");
                else
                    System.out.println("Where you want to change the " + object + " (group ID): ");
                groups = sc.nextLine().replaceAll("^[,\\s]+", "");
                if(op==2) {
                    try {
                        Integer.parseInt(groups);
                    } catch (NumberFormatException e) {
                        System.out.println("Please insert a number");
                        continue;
                    }
                }
                validation = stringChecker(groups);
            }
            if(groups.equals("0")){
                return;
            }
            validation = false;
            while(!validation) {
                System.out.println("Music title: ");
                title = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (title.equals("0")) {
                    return;
                }
                validation = stringChecker(title);
            }
            validation = false;
            while(!validation) {
                System.out.println("Artist: ");
                artist = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (artist.equals("0")) {
                    return;
                }

                validation = stringChecker(artist);
            }
            validation = false;
            while(!validation) {
                System.out.println("Music Genre: ");
                genre = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (genre.equals("0")) {
                    return;
                }

                validation = stringChecker(genre);
            }
            validation = false;
            while(!validation) {
                System.out.println("Duration: ");
                duration = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (duration.equals("0")) {
                    return;
                }

                validation = stringChecker(duration);
            }
            while(true) {
                try {
                    if(op==1)
                        res = rmi.addInfo(user, groups, object, title, artist, genre, duration);
                    else
                        res = rmi.changeInfo(user, groups, object, title, artist, genre, duration);
                    break;
                } catch (RemoteException e) {
                    retryRMIConnection();
                }
            }
            System.out.println(res);
        }
        if (option == 2) { //user wants to add a new artist
            String groups=null, name = null, description=null, concerts=null, genre=null;
            object="artist";
            while (!validation) {
                if (op == 1)
                    System.out.println("Where you want to add the " + object + " (group IDs): ");
                else
                    System.out.println("Where you want to change the " + object + " (group ID): ");
                groups = sc.nextLine().replaceAll("^[,\\s]+", "");
                if(op==2) {
                    try {
                        Integer.parseInt(groups);
                    } catch (NumberFormatException e) {
                        System.out.println("Please insert a number");
                        continue;
                    }
                }
                validation = stringChecker(groups);
            }
            if(groups.equals("0")){
                return;
            }
            validation = false;
            while(!validation) {
                System.out.println("Artist name: ");
                name = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (name.equals("0")) {
                    return;
                }

                validation = stringChecker(name);
            }
            validation = false;
            while(!validation) {
                System.out.println("Artist description: ");
                description = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (description.equals("0")) {
                    return;
                }

                validation = stringChecker(description);
            }
            validation = false;
            while(!validation) {
                System.out.println("Next concerts (separated by ','): ");
                concerts = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (concerts.equals("0")) {
                    return;
                }

                validation = stringChecker(concerts);
            }
            validation = false;
            while(!validation) {
                System.out.println("Genre: ");
                genre = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (genre.equals("0")) {
                    return;
                }

                validation = stringChecker(genre);
            }
            while(true) {
                try {
                    if(op==1)
                        res = rmi.addInfo(user, groups, object, name, description, concerts, genre);
                    else
                        res = rmi.changeInfo(user, groups, object, name, description, concerts, genre);
                    break;
                } catch (RemoteException e) {
                    retryRMIConnection();
                }
            }
            System.out.println(res);
        }
        if (option == 3) { //user wants to add a new album
            String groups=null, artist=null, title=null, musics=null, year=null, publisher=null, genre=null, description=null;
            object="album";
            while (!validation) {
                if (op == 1)
                    System.out.println("Where you want to add the " + object + " (group IDs): ");
                else
                    System.out.println("Where you want to change the " + object + " (group ID): ");
                groups = sc.nextLine().replaceAll("^[,\\s]+", "");
                if(op==2) {
                    try {
                        Integer.parseInt(groups);
                    } catch (NumberFormatException e) {
                        System.out.println("Please insert a number");
                        continue;
                    }
                }
                validation = stringChecker(groups);
            }
            if(groups.equals("0")){
                return;
            }
            validation = false;
            while(!validation) {
                System.out.println("Album title: ");
                title = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (title.equals("0")) {
                    return;
                }

                validation = stringChecker(title);
            }
            validation = false;
            while(!validation) {
                System.out.println("Album artist: ");
                artist = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (artist.equals("0")) {
                    return;
                }

                validation = stringChecker(artist);
            }
            validation = false;
            while(!validation) {
                System.out.println("Music list separated by commas:");
                musics = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (musics.equals("0")) {
                    return;
                }

                validation = stringChecker(musics);
            }
            validation = false;
            while(!validation) {
                System.out.println("Year of publication: ");
                year = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (year.equals("0")) {
                    return;
                }

                validation = stringChecker(year);
            }
            validation = false;
            while(!validation) {
                System.out.println("Publisher: ");
                publisher = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (publisher.equals("0")) {
                    return;
                }

                validation = stringChecker(publisher);
            }
            validation = false;
            while(!validation) {
                System.out.println("Genre: ");
                genre = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (genre.equals("0")) {
                    return;
                }

                validation = stringChecker(genre);
            }
            validation = false;
            while(!validation) {
                System.out.println("Album description: ");
                description = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (description.equals("0")) {
                    return;
                }

                validation = stringChecker(description);
            }
            while(true) {
                try {
                    if(op==1)
                        res = rmi.addInfo(user, groups, artist, title, musics, year, publisher, genre, description);
                    else
                        res = rmi.changeInfo(user, groups, artist, title, musics, year, publisher, genre, description);
                    break;
                } catch (RemoteException e) {
                    retryRMIConnection();
                }
            }
            System.out.println(res);
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
            System.out.println("| 4) Upload Music                           |");
            System.out.println("| 5) Download Music                         |");
            System.out.println("| 6) Share Music                            |");
            System.out.println("| 7) Create Group                           |");
            System.out.println("| 8) Join Group                             |");
            if (perk<3){ // se for editor ou owner
                System.out.println("| 9) Give 'Editor' privileges               |");
                System.out.println("| 10) Add/change info                       |");

            }
            if(perk<2){ // se for owner
                System.out.println("| 11) Give 'Owner' privileges               |");
                System.out.println("| 12) Manage group requests                 |");
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
                return;
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
                downloadMenu();
                // continue
            else if(option == 6)
                shareMusicMenu();
                // continue
            else if(option == 7)
                createGroupMenu();
            else if(option == 8)
                joinGroupMenu();
            else if(option == 9 || option == 10){
                if(perk==3){
                    System.out.println("Please select one of the given options");
                    continue;
                }
                if(option == 9)
                    givePermissionsMenu("editor");
                if (option == 10)
                    addChangeInfoMenu();
            }
            else if(option == 11 || option == 12){
                if (perk>1){
                    System.out.println("Please select one of the given options");
                    continue;
                }
                if(option == 11)
                    givePermissionsMenu("owner");
                if(option == 12)
                    getRequests();
            }
             else
                System.out.println("Please select one of the given options");
        }
    }

    private static void downloadMenu() {
        //get transferredMusics from user

        String ans = "";

        try {
            ans = rmi.getMusics(user);
        } catch (RemoteException e) {
            retryRMIConnection();
        }

        if (ans.equals("You don't have access to any music files!...")) {
            System.out.println(ans);
            return;
        }

        String ans1 = ans.replace("<", "");
        ans1 = ans1.replace(">", "");
        String ans2[] = ans1.split(","); //contains the titles of the musics

        String list = "";
        int i=1;

        for (String music : ans2) {
            list += i + ") " + music;
            i++;
        }

        int option;

        while (true) {
            System.out.println("Your files:");
            System.out.println(list);
            System.out.print("Insert the number of the file you want to download: ");

            try {
                option = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
            } catch (NumberFormatException e) {
                System.out.println("I can only work with numbers bro!");
                continue;
            }
            break;
        }

        String infos[] = ans2[option-1].split(":");
        String musicTitle = infos[0];
        String artistName = infos[1];

        int portTCP = 0;
        try {
            portTCP = rmi.downloadFile(user, musicTitle, artistName);
        } catch (RemoteException e) {
            retryRMIConnection();
        }


        boolean validation = false;
        String path;

        while (!validation){
            System.out.println("-------------------| Search |------------------------------------");
            System.out.println("| Insert the path to where you want the file to be downloaded to |");
            System.out.println("-----------------------------------------------------------------");
            path = sc.nextLine().replaceAll("^[,\\s]+", "");
            validation = stringChecker(path);

            try {
                TCPDownload(path, portTCP);
            } catch (IOException e) {
                System.out.println("There was an exception: " + e);
            }
        }
    }

    private static void TCPDownload(String path, int port) throws IOException{
        String serverAddress = "0.0.0.0";
        Socket socket = null;
        DataInputStream dis = null;
        FileOutputStream fos = null;

        try {
            socket = new Socket(serverAddress, 5500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            dis = new DataInputStream(socket.getInputStream());
            fos = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("--------------------------");

        //the first message the client receives is the file size
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

        //file has been read
        System.out.println("Your file has been downloaded!");


    }
    //function to communicate with multicast server

    private static void TCPUpload(String path, int port) throws IOException {
        String serverAddress = "0.0.0.0";
        Socket socket = null;

        try {
            socket = new Socket(serverAddress, port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //all the code for the file upload
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); //stream for writing to socket
        FileInputStream fis = new FileInputStream(path); //stream for reading from music file given by the user

        long len = fis.getChannel().size();
        byte[] buffer = new byte[toIntExact(len)];

        //sending the file size on a separate message
        dos.writeInt(toIntExact(len));

        fis.read(buffer); //reads bytes from file into buffer
        dos.write(buffer, 0, toIntExact(len)); //writes len bytes from buffer starting at position off to dataOutputStream (sends to socket)

        fis.close();
        dos.close();

        socket.close();

        System.out.println("Your file was uploaded with success!");
    }

    private static void shareMusicMenu() {
        //get list of music this user has access to from server

        String ans = "";
        try {
            ans = rmi.getMusics(user);
        } catch (RemoteException e) {
            retryRMIConnection();
        }

        if (ans.equals("You don't have access to any music files!...")) {
            System.out.println(ans);
            return;
        }

        String ans1 = ans.replace("<", "");
        ans1 = ans1.replace(">", "");
        String ans2[] = ans1.split(","); //contains the titles of the musics

        String list = "";
        int i=1;

        for (String music : ans2) {
            list += i + ") " + music;
            i++;
        }

        //chose one of the files to share
        int option;

        while (true) {
            System.out.println("Your files:");
            System.out.println(list);
            System.out.print("Insert the number of the file you want to share: ");

            try {
                option = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
            } catch (NumberFormatException e) {
                System.out.println("I can only work with numbers bro!");
                continue;
            }
            break;
        }

        //specify the group IDs the you want to share the file with
        System.out.println("Insert the IDs for the groups you want this file to be shared with:\nNote: format should be \"ID1,ID2,ID2,...\"");
        String groupIDs = sc.nextLine();

        //send this info to the server
        String aux[] = ans2[option-1].split(":");
        String music = aux[0];
        String artist = aux[1];

        boolean res;
        try {
            res = rmi.shareMusic(user, groupIDs, music, artist);
        } catch (RemoteException e) {
            retryRMIConnection();
        }
    }

    private static void uploadMenu() {
        System.out.println("You have to associate your music file with one of the musics info in our DB\n");
        boolean validation = false;
        String keyword = "", answer = "";
        String artist = "", music = "";

        /*while (!validation){
            System.out.println("----------------| Search |----------------");
            System.out.println("| Insert your keyword(s):                |");
            System.out.println("------------------------------------------");
            keyword = sc.nextLine().replaceAll("^[,\\s]+", "");
            validation = stringChecker(keyword);

        }
        //search for the keyword
        try {
            answer = rmi.search(user, keyword, "music");
        } catch (RemoteException e) {
            retryRMIConnection();
        }
        validation = false;*/

        int port = -1;
        while (port == -1) {
            validation = false;
            //get the name for the music and artist for the music the user wants to upload
            while (!validation) {
                System.out.print("Type and enter the title of the music you want to upload: ");
                music = sc.nextLine();
                validation = stringChecker(music);
            }

            validation = false;

            while (!validation) {
                System.out.print("Type and enter the name of the artist to this music: ");
                artist = sc.nextLine();
                validation = stringChecker(artist);
            }

            //warn the server that you will be sending a music file to associate with a certain music in the DB
            try {
                port = rmi.uploadFile(user, music, artist); //se port = -1 significa que o user deu input de uma música que não existe
                if (port == -1) System.out.println("Music not found -- make sure you input the correct title and artist name :(");
            } catch (RemoteException e) {
                retryRMIConnection();
            }
        }
        uploadFile(port);

    }



    private static void uploadFile(int port) {
        boolean validation = false;
        String path;

        while (!validation){
            System.out.print("Insert the path to the file you want to upload: ");
            path = sc.nextLine().replaceAll("^[,\\s]+", "");
            validation = stringChecker(path);

            try {
                TCPUpload(path, port);
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
            try {
                ob = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
            }catch(NumberFormatException e) {
                System.out.println("I only work with numbers bro! Try again...");
                continue;
            }
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
                        answer = rmi.search(user, keyword, object);
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
        String object, artist=null, title=null, answer=null;
        boolean validation=false;
        while(true) {
            System.out.println("----------------| Details |----------------");
            System.out.println("| Know more about:                        |");
            System.out.println("| 1) Album                                |");
            System.out.println("| 2) Artist                               |");
            System.out.println("| 0) Back                                 |");
            System.out.println("-------------------------------------------");
            while(true) {
                try {
                    ob = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("I can only work with numbers bro!");
                }
            }
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
                if(object.equals("album")){
                    validation=false;
                    while(!validation){
                        System.out.println("----------------| Details |----------------");
                        System.out.println("| Insert album artist:                    |");
                        System.out.println("-------------------------------------------");
                        artist = sc.nextLine().replaceAll("^[,\\s]+", "");
                        validation = stringChecker(artist);
                    }
                }
                while(answer==null) {
                    try {
                        if(object.equals("artist"))
                            answer = rmi.details(user, object, title);
                        else
                            answer = rmi.details(user, object, title, artist);
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
        String review=null, title=null, artist=null;
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
            System.out.println("| Insert album artist:                    |");
            System.out.println("-------------------------------------------");
            artist=sc.nextLine().replaceAll("^[,\\s]+", "");
            validation = stringChecker(artist);
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
                verifier=rmi.review(title,artist,user,review,rating);
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
            System.out.println("| Group " + groupID + " created successfully!                |");
            if(perk!=1)
                perk=1;
        }
        System.out.println("------------------------------------------------");
    }

    private static void joinGroupMenu(){
        int option;
        String groups=null, answer;
        boolean verifier=false;
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
                else if (option > 0 && option <= splitted.length) {
                    while (true) {
                        try {
                            answer = rmi.joinGroup(user, splitted[option - 1]);
                            break;
                        } catch (RemoteException e) {
                            retryRMIConnection();
                        }
                    }
                    if (answer.equals("success"))
                        System.out.println("Request successfully sent to group owner(s)");
                    else
                        System.out.println("Something went wrong, please try again");
                    break;
                } else
                    System.out.println("Please select one of the given options");
            }
        }
    }

    private static void givePermissionsMenu(String perk) {
        String username=null, groupID=null;
        boolean validation=false;
        boolean verifier;
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
        try{
            verifier=rmi.givePermissions(perk,user,username,groupID);
            if (verifier) System.out.println(perk + " permissions given to " + username + " on group " + groupID);
            else System.out.println("Could not give " + perk + " permissions to " + username + " :(");
        }catch(RemoteException e) {
            retryRMIConnection();
        }
    }

    private static void getRequests(){
        String toPrint;
        System.out.println("----------------| Manage Requests |----------------");
        while(true) {
            try {
                toPrint = rmi.showRequests(user);
                break;
            } catch (RemoteException e) {
                retryRMIConnection();
            }
        }
        if(toPrint==null) {
            System.out.println("| There are no requests for you                   |");
            System.out.println("---------------------------------------------------");
        }
        else {
            String requests[] = toPrint.split(",");
            for (String s : requests) {
                System.out.println(s);
            }
            System.out.println("\n1) Accept / delete requests");
            System.out.println("0) Back");
            int ob;
            while(true) {
                try {
                    ob = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("I can only work with numbers bro!");
                }
            }
            if (ob == 0) {
                return;
            }
            else if (ob != 1)
                System.out.println("Please select a valid option");
            else{
                manageRequests();
            }
        }
    }

    private static void manageRequests(){
        String groupID=null, username=null, toDo=null;
        boolean validation=false, verifier;
        int op;
        while(true) {
            while (!validation) {
                System.out.println("----------------| Accept Requests |----------------");
                System.out.println("| Insert group ID:                                |");
                System.out.println("---------------------------------------------------");
                groupID = sc.nextLine().replaceAll("^[,\\s]+", "");
                validation = stringChecker(groupID);
            }
            validation = false;
            while (!validation) {
                System.out.println("----------------| Accept Requests |----------------");
                System.out.println("| Insert user:                                    |");
                System.out.println("---------------------------------------------------");
                username = sc.nextLine().replaceAll("^[,\\s]+", "");
                validation = stringChecker(username);
            }
            validation = false;
            while (!validation) {
                System.out.println("----------------| Accept Requests |----------------");
                System.out.println("| What do you want to do: (accept/decline)         |");
                System.out.println("---------------------------------------------------");
                toDo = sc.nextLine().replaceAll("^[,\\s]+", "");
                if (toDo == null) {
                    System.out.println("Please type 'accept' or 'decline'");
                    continue;
                }
                if (!toDo.equals("accept") && !toDo.equals("decline")) {
                    System.out.println("Please type 'accept' or 'decline'");
                    continue;
                }
                validation = true;
            }

            while (true) {
                try {
                    verifier = rmi.manageRequests(user, username, groupID, toDo);
                    break;
                } catch (RemoteException e) {
                    retryRMIConnection();
                }
            }
            if (verifier) {
                if (toDo.equals("accept"))
                    System.out.println("Request successfully accepted");
                else
                    System.out.println("Request successfully declined");
            } else
                System.out.println("You don't have enough permissions to accept or decline requests in group " + groupID + "!");
            while(true) {
                System.out.println("----------------| Accept Requests |----------------");
                System.out.println("| Do you want to manage other requests?           |");
                System.out.println("| 1) Yes                                          |");
                System.out.println("| 2) No                                           |");
                System.out.println("---------------------------------------------------");
                try {
                    op = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", ""));
                } catch (NumberFormatException e) {
                    System.out.println("I can only work with numbers bro!");
                    continue;
                }
                if(op==1)
                    break;
                else if (op==2)
                    return;
                else
                    System.out.println("Please chose one of the given options");
            }
        }
    }

    private static boolean stringChecker (String toCheck){
        if(toCheck==null) {
            System.out.println("String is NULL. Please type something");
            return false;
        }
        if (toCheck.contains("|") || toCheck.contains(";") || toCheck.equals("")) {
            System.out.println("String contains forbidden characters ('|' or ';' or '\\n')\n");
            return false;
        }
        return true;
    }

    public void notification (String message) throws RemoteException{
        System.out.println("----------------| New Notification |----------------");
        if(message.contains("editor")){
            perk = 2;
        }else if(message.contains("owner")){
            perk = 1;
        }
        System.out.println(message);
    }

    public String getUsername() throws RemoteException {
        return user;
    }

}