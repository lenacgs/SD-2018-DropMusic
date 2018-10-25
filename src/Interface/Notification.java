package Interface;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification implements Serializable{
    private String message;
    private String timeStamp;

    public Notification(String message) {
        this.message = message;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss");
        timeStamp = sdf.format(date);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
