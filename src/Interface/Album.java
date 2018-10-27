    package Interface;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

    public class Album implements Serializable{
    private static final long serialVersionUID = 4L;
    private Artist artist;
    private String title;
    private int yearOfPublication;
    private CopyOnWriteArrayList<Music> musics;
    private String publisher;
    private String genre;
    private Description description;
    private CopyOnWriteArrayList<Review> reviews;
    private CopyOnWriteArrayList<Integer> groups;

    public Album(Artist artist, String title, int yearOfPublication, CopyOnWriteArrayList<Music> musics, String publisher, String genre, Description description) {
        this.artist = artist;
        this.title = title;
        this.yearOfPublication = yearOfPublication;
        this.musics = musics;
        this.publisher = publisher;
        this.genre = genre;
        this.description = description;
        this.reviews = new CopyOnWriteArrayList<>();
        this.groups = new CopyOnWriteArrayList<>();
    }

    public void add_groups(int group) { this.groups.add(group);}

    public int getYearOfPublication() {
        return yearOfPublication;
    }

    public void setYearOfPublication(int yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CopyOnWriteArrayList<Integer> getGroups(){ return this.groups;}

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public CopyOnWriteArrayList<Music> getMusics() {
        return musics;
    }

    public void setMusics(CopyOnWriteArrayList<Music> musics) {
        this.musics = musics;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public CopyOnWriteArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(CopyOnWriteArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public float calcAverageRate() {
        Iterator it = reviews.iterator();

        float total = 0;

        while (it.hasNext()) {
            Review r = (Review)it.next();
            total += r.getRate();
        }

        float avRate = total / reviews.size();
        return avRate;
    }

    public float getDuration() {
        Iterator it = musics.iterator();

        float total = 0;

        while (it.hasNext()){
            Music m = (Music)it.next();
            total += m.getDuration();
        }

        return total;
    }

    public String reviewsToString() {
        Iterator it = reviews.iterator();

        String res = "";

        while (it.hasNext()) {
            Review add = (Review)it.next();
            res += add.toString();
            res += "\n";
        }
        return res;
    }


    public void addReview(Review r) {
        reviews.add(r);
    }

    public String displayInfo() {
        String info = "Album title: " + this.title + "\n" +
                "Artist: " + this.artist + "\n" +
                "Publisher: " + this.publisher + "\n" +
                "Year of publication: " + this.yearOfPublication + "\n" +
                "Duration: " + this.getDuration() + "\n\n" +
                this.description.getText() + "\n\n" +
                this.musics.toString() + "\n\n" +
                "Average rate: " + this.calcAverageRate() + "\n\n" +
                this.reviewsToString();

        return info;
    }
}
