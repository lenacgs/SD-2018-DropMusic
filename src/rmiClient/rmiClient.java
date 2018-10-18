package rmiClient;

import rmi.Services;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class rmiClient {
    private static Scanner sc = new Scanner(System.in);
    private static Services rmi;
    private static ArrayList<String> groups = new ArrayList<>();
    // o que estou a pensar é, no ato do login e em cada alteração atualizar esta lista para ser mais simples enviar pedidos ao RMI
    // um exemplo da lista podia ser [(<grupo> <role>) (<grupo> <role>) (...)]
    // desta maneira quando formos fazer um pedido ao RMI para mexer em algum grupo, enviamos logo a informação do grupo que ele que alterar
    // e sabemos logo a partir do role se ele pode fazer essas alterações ou não
    private static String user=null;
    private static int perk=0;

    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
        establishRMIConnection();
        rmi.hello();
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

    private static void validationMenu(int modifier){
        String username, password;
        int verifier;
        boolean validation;
        System.out.println("(you can type '0' at any time to exit)");
        while(true) {
            System.out.print("\nUsername: ");
            username = sc.nextLine().replaceAll("^[,\\s]+", "");
            if (username.equals("0")) {
                break;
            }
            if(username.contains(" ")) {
                System.out.println("Username cannot contain spaces");
                continue;
            }
            validation=stringChecker(username);
            if(!validation)
                continue;
            System.out.print("\nPassword: ");
            password = sc.nextLine().replaceAll("^[,\\s]+", "");
            if (password.equals("0")) {
                break;
            }
            if(password.contains(" ")) {
                System.out.println("Password cannot contain spaces");
                continue;
            }
            validation=stringChecker(password);
            if(!validation)
                continue;
            try {
                //funcao de registar e login tem que devolver um boolean
                if (modifier == 1) //registar
                    verifier = rmi.register(username,password);
                else //login
                    verifier = rmi.login(username,password);
                if (verifier <= 4) { //1- owner de algum grupo, 2- editor de algum grupo, 3- normal, 4-nao existe/credencias mal;
                    if (modifier == 1)
                        System.out.println("User registed successfully!");
                    else
                        System.out.println("Logged in successfully!");
                    user=username;
                    perk=verifier;
                    mainMenu();
                    break;
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

    private static void mainMenu(){
        int option;
        boolean verifier=false;
        while(true) { //1- owner de algum grupo, 2- editor de algum grupo, 3- normal
            System.out.println("----------------| Main Menu |----------------"); //2, 6,
            System.out.println("| 1) Search                                 |");
            System.out.println("| 2) Album and Artist details               |");
            System.out.println("| 3) Album Review                           |");
            System.out.println("| 4) Upload Music                           |");
            System.out.println("| 5) Download Music                         |");
            System.out.println("| 6) Share Music                            |");
            System.out.println("| 7) Create Group                           |");
            System.out.println("| 8) Join Group                             |");
            if (perk<3){ // se for editou ou owner
                System.out.println("| 9) Manage Groups                          |");
                System.out.println("| 10) Give 'Editor' privileges              |");

            }
            if(perk<2){ // se for owner
                System.out.println("| 11) Manage group requests                 |");
                System.out.println("| 12) Give 'Owner' privileges               |");
            }
            System.out.println("| 0) Logout                                 |");
            System.out.println("---------------------------------------------");
            //to be continued
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
                System.out.println();
                // continue
            else if(option == 5)
                System.out.println();
                // continue
            else if(option == 6)
                System.out.println();
                // continue
            else if(option == 7)
                System.out.println();
                // continue
            else if(option == 8)
                joinGroupMenu();
                // continue
            else if(option == 9 || option == 10){
                if(perk==3){
                    System.out.println("Please select one of the given options");
                    continue;
                }
                //if(option == 9)
                    // continue
                //if(option == 10)
                    // continue
            }
            else if(option == 11 || option == 12){
                if (perk>1){
                    System.out.println("Please select one of the given options");
                    continue;
                }
                //if(option == 11)
                // continue
                //if(option == 12)
                // continue
            }
             else
                System.out.println("Please select one of the given options");
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
            System.out.println("| 3) Genre                               |");
            System.out.println("| 4) Artist                              |");
            System.out.println("| 0) Back                                |");
            System.out.println("------------------------------------------");
            ob = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
            if (ob == 0) {
                break;
            }
            else if (ob>4){
                System.out.println("Please select a valid option");
                continue;
            }
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
            else if (ob>2){
                System.out.println("Please select a valid option");
                continue;
            }
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
            verifier = false;
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

    private static boolean stringChecker (String toCheck){
        if (toCheck.contains("|") || toCheck.contains(";")) {
            System.out.println("String contains forbidden characters ('|' , ';')\n");
            return false;
        }
        return true;
    }

}