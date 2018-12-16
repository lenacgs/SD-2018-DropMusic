package Web.Beans;

import java.rmi.RemoteException;

public class NotificationBean extends RMIBean {

    public NotificationBean(){
        super();
    }

    public String getNotifications(String username){
        String reply;
        try{
            reply = server.get_notifications(username);
            return reply;
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return null;

    }

    public void saveNotification(String username, String notification){
        try{
            server.sendNotification(notification,username);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }


}
