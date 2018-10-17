    package Interface;

import java.util.*;

public class Album {
    private Artist artist;
    private String title;
    private int yearOfPublication;
    private ArrayList<Music> musics;
    private String publisher;
    private String genre;
    private Description description;
    private ArrayList<Review> reviews;

    public Album(String title, Artist artist, int year, String publisher, String genre) {
        this.artist = artist;
        this.yearOfPublication = year;
        this.title = title;
        this.publisher = publisher;
        this.genre = genre;
        this.description = null;
        this.musics = null;
    }

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

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<Music> musics) {
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

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
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
