package Web.Beans;

import com.github.scribejava.core.model.Token;

import java.rmi.RemoteException;

public class LoginBean extends RMIBean {

    public LoginBean(){
        super();
        lookup();
    }

    public int loginUser(String username, String password){
        int perks,trys=0;
        while(true) {
            try {
                if(trys>=30)
                    return -1;
                perks = server.login(username, password);
                return perks;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }

    public String loginDropboxUser(String accessToken) {
        String res = null;
        try {
            res = server.loginDropbox(accessToken);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (res.equals("0")) return "FAIL";
        //res[0]=perks, res[1]=username, res[2]=accessToken
        return res;
    }



}
