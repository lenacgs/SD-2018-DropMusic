package ws;

import Interface.Notification;
import Interface.User;
import Web.Beans.NotificationBean;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/ws")
public class WebSocketAnnotation {
    private static final Set<WebSocketAnnotation> users = new CopyOnWriteArraySet<>();
    private String username;
    private Session session;
    private NotificationBean notificationBean;

    public WebSocketAnnotation(){
        notificationBean = new NotificationBean();
    }

    @OnOpen
    public void start(Session session){
        this.session = session;
    }

    @OnClose
    public void end(){
        users.remove(this);
    }

    @OnMessage
    public void receiveMessage(String message){
        String[] aux = message.split(" \\| ");
        if(aux[0].equals("username")) {
            this.username = aux[1];
            users.add(this);
        }else if(aux[0].equals("get_notifications")){
            this.username = aux[1];
            users.add(this);
            String notification = notificationBean.getNotifications(this.username);
            if(!notification.equals("")){
                String temp[] = notification.split("\n");
                for(String s : temp){
                    sendNotification(s, this.username);
                }
            }
        }else{
            String user = aux[0];
            String notification = aux[1];
            sendNotification(notification, user);
        }
    }

    @OnError
    public void handleError(Throwable t){
        t.printStackTrace();
    }

    public void sendNotification(String text, String target){
        try{
            boolean sent = false;
            for(WebSocketAnnotation curr : users){
                if(curr.username.equals(target)){
                    curr.session.getBasicRemote().sendText(text);
                    sent = true;
                    break;
                }
            }
            if(!sent){
                notificationBean.saveNotification(target, text);
            }
        }catch (IOException e) {
            try{
                this.session.close();
            }catch (IOException e1){
                e1.printStackTrace();
            }
        }
    }
}
