package edu.farmingdale.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Book implements Comparable<Book>{

    private static int nextID = 100000;
    private int ID;
    private String ISBN;
    private String bookTitle;
    private String author;
    private Boolean inLibrary;
    private Student possesion;

    // Rating system will store each individual rating to to firebase
    private List<Integer> ratings;

    // Constructor that auto-generates ID
    public Book(String ISBN, String bookTitle, String author, Boolean inLibrary, Student possesion) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.inLibrary = inLibrary;
        this.possesion = possesion;
        this.ISBN = ISBN;
        this.ID = nextID++;
        this.ratings = new ArrayList<>();
    }

    // Constructor with specific ID for existing books
    public Book(int ID, String ISBN, String bookTitle, String author, Boolean inLibrary, Student possesion) {
        this.ID = ID;
        this.ISBN = ISBN;
        this.bookTitle = bookTitle;
        this.author = author;
        this.inLibrary = inLibrary;
        this.possesion = possesion;
        this.ratings = new ArrayList<>();
    }

    // Firebase Constructor
    public Book() {
        this.ratings = new ArrayList<>();
    }

    //Getters and Setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean getInLibrary() {
        return inLibrary;
    }

    public Student getPossesion() {
        return possesion;
    }

    public void setPossesion(Student possesion) {
        this.possesion = possesion;
    }

    public void setInLibrary(Boolean inLibrary) {
        this.inLibrary = inLibrary;
    }

    // Rating methods
    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            ratings.add(rating);
        }
    }

    public List<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = (ratings != null) ? ratings : new ArrayList<>();
    }

    public double getAverageRating() {
        if (ratings.isEmpty()) return 0.0;
        double sum = 0.0;
        for (int rating : ratings) {
            sum += rating;
        }
        return sum / ratings.size();
    }

    public int getRatingCount() {
        return ratings.size();
    }

    @Deprecated
    public double getTotalRating() {
        double sum = 0.0;
        for (int rating : ratings) {
            sum += rating;
        }
        return sum;
    }

    @Deprecated
    public void setTotalRating(double totalRating) {

    }

    @Deprecated
    public void setRatingCount(int ratingCount) {

    }


    public String getRatingDisplay() {
        if (ratings.isEmpty()) {
            return "No ratings yet";
        }
        return String.format("%.1f ⭐ (%d rating%s)",
                getAverageRating(),
                getRatingCount(),
                getRatingCount() == 1 ? "" : "s");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return ID == book.ID && ISBN == book.ISBN && Objects.equals(bookTitle, book.bookTitle) && Objects.equals(author, book.author) && Objects.equals(inLibrary, book.inLibrary) && Objects.equals(possesion, book.possesion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, ISBN, bookTitle, author, inLibrary, possesion);
    }

    @Override
    public int compareTo(Book other) {
        return this.bookTitle.compareToIgnoreCase(other.bookTitle);
    }

    @Override
    public String toString() {
        return "Book{" +
                "ID=" + ID +
                ", ISBN='" + ISBN + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", author='" + author + '\'' +
                ", inLibrary=" + inLibrary +
                ", possesion=" + possesion +
                ", avgRating=" + getAverageRating() +
                ", ratingCount=" + getRatingCount() +
                '}';
    }
}