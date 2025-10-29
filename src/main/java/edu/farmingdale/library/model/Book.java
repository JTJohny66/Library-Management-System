package edu.farmingdale.library.model;

import java.util.Objects;

public class Book
        implements Comparable<Book>{

    private static int nextID = 100000;
    private int ID;
    private String ISBN;
    private String bookTitle;
    private String author;
    private Boolean inLibrary;
    private Student possesion;

    public Book(String ISBN, String bookTitle, String author, Boolean inLibrary, Student possesion) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.inLibrary = inLibrary;
        this.possesion = possesion;
        this.ISBN = ISBN;
        this.ID = nextID++;
    }

    public int getID() {
        return ID;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getAuthor() {
        return author;
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
                '}';
    }
}
