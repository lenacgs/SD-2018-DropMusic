package Interface;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class Group implements Serializable {
    private static final long serialVersionUID = 4L;
    private CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<User> editors = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<User> owners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<User> requests = new CopyOnWriteArrayList<>();

    private int groupID;

    public Group(User creator, int groupID) {
        this.addUser(creator);
        this.addEditor(creator);
        this.addOwner(creator);
        this.groupID = groupID;
    }

    public void addRequest(User user) { this.requests.add(user); }

    public void removeRequest(String username) {
        for(User u : this.requests){
            if(u.getUsername().equals(username)){
                this.requests.remove(u);
                return;
            }
        }
    }

    public CopyOnWriteArrayList<User> getRequests(){
        return this.requests;
    }

    public String getGroupRequests(){
        String reply = "<";
        int counter = 0;

        for(User u : this.requests){
            if(counter > 0){
                reply += ",";

            }
            reply += u.getUsername();
            counter++;
        }
        reply += ">";
        return reply;
    }

    public void removeUser(String username, CopyOnWriteArrayList<User> users) {
        for(User u : users){
            if(u.getUsername().equals(username)){
                users.remove(u);
                return;
            }
        }
    }

    public int userPerks(String username){
        for(User u : this.owners) {
            if (u.getUsername().equals(username)) return 1;
        }
        for(User u : this.editors) {
            if (u.getUsername().equals(username)) return 2;
        }
        for(User u : this.users) {
            if (u.getUsername().equals(username)) return 3;
        }
        return 0;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void addEditor(User user) {
        this.editors.add(user);
    }

    public void addOwner(User user) {
        this.owners.add(user);
    }

    public int getGroupID(){ return this.groupID;}

    public CopyOnWriteArrayList<User> getUsers(){ return this.users;}

    public CopyOnWriteArrayList<User> getEditors() { return this.editors;}

    public CopyOnWriteArrayList<User> getOwners() { return this.editors;}

    public boolean isUser(User user) {
        return this.users.contains(user);
    }

    public boolean isEditor(User user) {
        return this.editors.contains(user);
    }

    public boolean isOwner(User user) {
        return this.owners.contains(user);
    }

    public void printUsers(){
        System.out.print("(");
        int counter = 0;
        for(User u : this.users){
            System.out.print(u.getUsername());
            if(++counter<this.users.size()){
                System.out.print(",");
            }
        }
        System.out.println(")");
    }

    private User getUserByName(String name) {
        Iterator it = users.iterator();

        while (it.hasNext()) {
            User aux = (User)it.next();
            if (aux.getUsername().equals(name))
                return aux;
        }
        return null;
    }

    private User getEditorByName(String name) {
        Iterator it = editors.iterator();

        while (it.hasNext()) {
            User aux = (User)it.next();
            if (aux.getUsername().equals(name))
                return aux;
        }
        return null;
    }

    private User getOwnerByName(String name) {
        Iterator it = owners.iterator();

        while (it.hasNext()) {
            User aux = (User)it.next();
            if (aux.getUsername().equals(name))
                return aux;
        }
        return null;
    }

    public boolean isUser(String name) {
        User aux = getUserByName(name);

        return aux!= null;
    }

    public boolean isOwner(String name) {
        User aux = getOwnerByName(name);

        if (aux != null)
            return true;
        return false;
    }

    public boolean isEditor(String name) {
        User aux = getEditorByName(name);

        if (aux != null)
            return true;
        return false;
    }




}