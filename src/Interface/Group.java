package Interface;

import java.util.*;

public class Group {
    private ArrayList<User> users;
    private ArrayList<User> editors;
    private ArrayList<User> owners;
    private int groupID;

    public Group(User creator) {
        this.addUser(creator);
        this.addEditor(creator);
        this.addOwner(creator);
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

    public boolean isUser(User user) {
        if (this.users.contains(user)) {
            return true;
        }
        return false;
    }

    public boolean isEditor(User user) {
        if (this.editors.contains(user)) {
            return true;
        }
        return false;
    }

    public boolean isOwner(User user) {
        if (this.editors.contains(user)) {
            return true;
        }
        return false;
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

        if (aux != null)
            return true;
        return false;
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
