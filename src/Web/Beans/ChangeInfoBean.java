package Web.Beans;

import java.rmi.RemoteException;

public class ChangeInfoBean extends RMIBean {

    public ChangeInfoBean(){
        super();
        lookup();
    }

    public String changeMusic(String username, String group, String title, String artist, String genre, String duration) {
        int trys=0;
        String reply;
        while(true) {
            try {
                if(trys>=30)
                    return "-1";
                reply = server.changeInfo(username, group, "music", title, artist, genre, duration);
                return reply;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }

    public String changeArtist(String username, String group, String title, String description, String concerts, String genre) {
        int trys=0;
        String reply;
        while(true) {
            try {
                if(trys>=30)
                    return "-1";
                reply = server.changeInfo(username, group, "artist", title, description, concerts, genre);
                return reply;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }

    public String changeAlbum(String username, String group, String artist, String title, String musics, String year, String publisher, String genre, String description) {
        int trys=0;
        String groupID;
        while(true) {
            try {
                if(trys>=30)
                    return "-1";
                groupID = server.changeInfo(username, group, artist, title, musics, year, publisher, genre, description);
                return groupID;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
