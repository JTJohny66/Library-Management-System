package edu.farmingdale.library;

public class Book {

    private static int nextID = 100000;
    private int ID;
    private String bookTitle;
    private String author;
    private Boolean inLibrary;
    private Student possesion;

    public Book(int ID, String bookTitle, String author, Boolean inLibrary, Student possesion) {
        this.ID = ID;
        this.bookTitle = bookTitle;
        this.author = author;
        this.inLibrary = inLibrary;
        this.possesion = possesion;
    }
}
