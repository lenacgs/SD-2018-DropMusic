package Interface;

public class Review {
    private int rate;
    private String text;
    private User user;

    public Review(int rate, String text, User user) {
        this.rate = rate;
        this.text = text;
        this.user = user;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toString() {
        return "@" + user + "\n" + "Rate: " + rate + " " + text;
    }
}
