package Multicast;

import Interface.*;

import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.*;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4322;
    private ArrayList<User> UserList = new ArrayList<User>();

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer(){
        super("Multicast Server 1");
    }


    public void run(){
        MulticastSocket socket = null;
        System.out.println(this.getName() + " running...");
        try{
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while(true){
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                requestHandler newRequest = new requestHandler(message);
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

    public requestHandler(String request){
        super("Request");
        this.request = request;
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
                    if(info[1][0].equals("username") && info[2][0].equals("password") && info.length == 3) {
                        if(true) { //Se o register funcionar
                            return "type | status ; register | succeeded";
                        }else{
                            return "type | status ; register | failed";
                        }
                    }else {
                        return "type | status ; command | invalid";
                    }
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