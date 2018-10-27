package Interface;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 4L;
    private String request;
    private boolean replied;

    public Request(String request){
        this.request = request;
        this.replied = false;
    }

    public String getRequest(){
        return this.request;
    }

    public boolean replied(){
        return this.replied;
    }

    public void confirmReply(){
        this.replied = true;
    }

    public boolean compare(String request){
        return this.request.equals(request) && !replied;
    }
}
