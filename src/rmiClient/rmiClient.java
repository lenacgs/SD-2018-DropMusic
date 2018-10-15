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
        System.out.println(" ------------");
        System.out.println(" | WELCOME! |");
        System.out.println(" ------------");
        int option;
        while(true) {
            System.out.println("----------------");
            System.out.println("| 1) Register  |");
            System.out.println("| 2) Login     |");
            System.out.println("| 3) Exit      |");
            System.out.println("----------------");
            try {
                option = Integer.parseInt(sc.nextLine().replaceAll("^[,\\s]+", "")); // tem que ser assim senao da bode
                if(option == 1 || option == 2) {
                    validationMenu(option);
                }
                else if(option == 3)
                    break;
                System.out.println("Please select a valid option\n");
            }catch (NumberFormatException e) {
                System.out.println("I only work with numbers bro! Try again...\n");
            }
        }
    }

    private static void validationMenu(int modifier){
        String username, password, trash;
        int verifier;
        System.out.println("(you can type '0' at any time to go back)");
        while(true) {
            System.out.print("\nUsername: ");
            username = sc.nextLine().replaceAll("^[,\\s]+", "");
            if (username.equals("0")) {
                break;
            }
            System.out.print("\nPassword: ");
            password = sc.nextLine().replaceAll("^[,\\s]+", "");
            if (password.equals("0")) {
                break;
            }
            if (username.contains("|") || username.contains(";") || username.contains(" ") || password.contains("|") || password.contains(";") || password.contains(" "))
                System.out.println("Username or Password contains forbidden characters ('|' , ';' or ' ')\n");
            else {
                try {
                    //funcao de registar e login tem que devolver um boolean
                    if (modifier == 1) //registar
                        verifier = rmi.testerLogin();
                    else //login
                        verifier = rmi.testerLogin();
                    if (verifier <= 4) { //1- admin geral, 2- owner de algum grupo, 3- editor de algum grupo, 4- normal, 5-nao existe/credencias mal;
                        if (modifier == 1)
                            System.out.println("User registed successfully!");
                        else
                            System.out.println("Logged in successfully!");
                        mainMenu(verifier);
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
    }

    private static void mainMenu(int perk){
        System.out.println("-------------------------------------------------------"); //2, 6,
        System.out.println("| 1) Search");
        System.out.println("| 2) Album and Artist details");
        System.out.println("| 3) Album Review");
        System.out.println("| 4) Upload Music");
        System.out.println("| 5) Share Music");
        System.out.println("| 6) Download Music");
        System.out.println("| 7) Create Group");
        //to be continued
    }

}