package Web.Beans;

import java.rmi.RemoteException;

public class ReviewBean extends RMIBean {

    public ReviewBean(){
        super();
        lookup();
    }

    public boolean albumReview(String title, String artist, String username, String review, String rating){
        boolean reply;
        int trys=0;
        int ratingInt=Integer.parseInt(rating);
        while(true) {
            try {
                if(trys>=30)
                    return false;
                reply = server.review(title, artist, username, review, ratingInt);
                return reply;
            } catch (RemoteException e) {
                trys=reestablishConnection(trys);
            }
        }
    }
}
