package rmiClient;

import rmi.Services;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class rmiClient {
    private Scanner sc = new Scanner(System.in);
    private Services rmi;
    // o que estou a pensar é, no ato do login e em cada alteração atualizar esta lista para ser mais simples enviar pedidos ao RMI
    // um exemplo da lista podia ser [(<grupo> <role>) (<grupo> <role>) (...)]
    // desta maneira quando formos fazer um pedido ao RMI para mexer em algum grupo, enviamos logo a informação do grupo que ele que alterar
    // e sabemos logo a partir do role se ele pode fazer essas alterações ou não
    private String user=null;
    private int perk=0;

    public rmiClient() {
    }

    public Services getRmi() {
        return rmi;
    }

    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
        rmiClient client = new rmiClient();
        client.establishRMIConnection();
        client.getRmi().hello();
        client.firstMenu();
    }

    private void establishRMIConnection(){
        try {
            rmi = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting");
        }catch (RemoteException | NotBoundException e) {
            retryRMIConnection();
        }
    }

    private void retryRMIConnection(){
        while(true){
            try {
                Thread.sleep(1000);
                rmi = (Services) LocateRegistry.getRegistry(7000).lookup("Sporting");
                break;
            }catch (RemoteException | NotBoundException e) {
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void firstMenu(){
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

    private void validationMenu(int modifier) {
        String username, password=null;
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
                }
            }
        }
    }

    private void mainMenu(){
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

            }
            if(perk<2){ // se for owner
                System.out.println("| 11) Give 'Owner' privileges               |");
                System.out.println("| 12) Manage group requests                 |");//falta
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
            else if(option == 9 || option == 10){
                if(perk==3){
                    System.out.println("Please select one of the given options");
                    continue;
                }
                if(option == 9)
                    manageGroup();
                if(option == 10)
                    givePermissionsMenu("editor");
            }
            else if(option == 11 || option == 12){
                if (perk>1){
                    System.out.println("Please select one of the given options");
                    continue;
                }
                if(option == 11)
                    givePermissionsMenu("owner");
                //if(option == 12)
                // continue
            }
             else
                System.out.println("Please select one of the given options");
        }
    }

    private void uploadMenu() {
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
    private void TCPServerConnection(String username, String music, String path) throws UnknownHostException, IOException {
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

    private void uploadFile(String username, String music) {
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

    private void searchMenu(){
        int ob;
        String keyword=null, object, answer=null;
        boolean validation=false;
        while(true) {
            System.out.println("----------------| Search |----------------");
            System.out.println("| What are you searching for:            |");
            System.out.println("| 1) Music                               |");
            System.out.println("| 2) Album                               |");
            System.out.println("| 3) Genre                               |");
            System.out.println("| 4) Artist                              |");
            System.out.println("| 0) Back                                |");
            System.out.println("------------------------------------------");
            ob = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
            if (ob == 0) {
                break;
            }
            else if (ob > 4 || ob < 0)
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
                else if(ob == 3)
                    object="genre";
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

    private void detailsMenu(){
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


    private void reviewMenu(){
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

    private void createGroupMenu(){
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

    private void joinGroupMenu(){
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

    private void manageGroup(){
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

    private void givePermissionsMenu(String perk) {
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

    private boolean stringChecker (String toCheck){
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

}