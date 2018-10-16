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

    public String translation(String message){
        System.out.println("translating...");
        String tokens[] = message.split(" ; ");
        String info[][] = new String[tokens.length][];
        for(int i = 0; i < tokens.length; i++) info[i] = tokens[i].split(" \\| ");
        if(info[0][0].equals("type"){
            String command = info[0][1];
            switch(command){
                case "register":
                    if(info[1][0].equals("username") && info[2][0].equals("password") && info.length == 3) {
                        this.UserList.add(new User(info[1][1], info[2][1]));
                        return "Registration successful!";
                    }else {
                        return "Registration failed!";
                    }
                case "login":
                    break;
                case "status":
                    break;
                case "perks":
                    break;
                case "perks_group":
                    break;
                case "search":
                    break;
                case "artist_list":
                    break;
                case "album_list":
                    break;
                case "music_list":
                    break;
                case "genre_list":
                    break;
                case "add_info":
                    break;
                case "change_info":
                    break;
                case "get_info":
                    break;
                case "review":
                    break;
                case "grant_perks":
                    break;
                case "notify_user":
                    break;
                default:
                    return "type | status ; operation | failed";
            }
        }
        return "";
    }


    public void run(){
        MulticastSocket socket = null;
        System.out.println(this.getName() + " running...");
        try{
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while(true){
                byte[] buffer1 = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer1, buffer1.length);
                socket.receive(packet);
                System.out.println("Received packet!");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);

                String reply = translation(message);

                byte[] buffer2 = reply.getBytes();

                packet = new DatagramPacket(buffer2, buffer2.length, group, PORT-1);
                socket.send(packet);
                System.out. println("Sent packet!");


            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            socket.close();

        }
    }
}


