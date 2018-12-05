package Interface;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 4L;
    private String request;
    private String reply;
    private int replyServer;


    public Request(String request, int replyServer){
        this.request = request;
        this.replyServer = replyServer;
    }


    public int getReplyServer(){
        return this.replyServer;
    }

    public void setReply(String reply){
        this.reply = reply;
    }

    public String getReply(){
        return this.reply;
    }

    public String getRequest(){
        return this.request;
    }

    public boolean compare(String request){
        return this.request.equals(request);
    }
}
