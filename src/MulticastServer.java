import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Scanner;
import java.io.*;
import java.util.*;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
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
        if(info[0][0].equals("type")){
            String command = info[0][1];
            switch(command){
                case "register":
                    if(info[1][0].equals("username") && info[2][0].equals("password")) {
                        this.UserList.add(new User(info[1][1], info[2][1]));
                        return "Registration successful!";
                    }else{
                        return "Registration failed!";
                    }
            }
        }
        return "1";
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
                System.out.println("Received packet!");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);

                String reply = translation(message);
                buffer = reply.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                System.out.println("Sent packet!");
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            socket.close(); 
        }
    }
}

