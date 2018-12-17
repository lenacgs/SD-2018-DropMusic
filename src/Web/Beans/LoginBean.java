package Web.Beans;

import com.github.scribejava.core.model.Token;

import java.rmi.RemoteException;

public class LoginBean extends RMIBean {

    public LoginBean(){
        super();
    }

    public int loginUser(String username, String password){
        int perks;
        try{
            perks = server.login(username, password);
            return perks;
        }catch (RemoteException e){
            e.printStackTrace();
            return -1;
        }
    }

    public String loginDropboxUser(String accountID) {
        String res = null;
        try {
            res = server.loginDropbox(accountID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (res.equals("0")) return "FAIL";
        //res[0]=perks, res[1]=username, res[2]=accessToken
        return res;
    }



}
