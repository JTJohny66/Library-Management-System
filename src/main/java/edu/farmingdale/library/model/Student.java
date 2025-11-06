package edu.farmingdale.library.model;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private static int nextID = 100000;
    private int ID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Store ISBNs, not Book objects → Firebase-friendly
    private List<String> currentBooks;

    // REQUIRED by Firebase: public no-arg constructor
    public Student() {
        this.currentBooks = new ArrayList<>();
    }

    public Student(String password, String email, String lastName, String firstName) {
        this.ID = nextID++;
        this.password = password;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.currentBooks = new ArrayList<>();
    }

    public int getID() { return ID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<String> getCurrentBooks() { return currentBooks; }
    public void setCurrentBooks(List<String> currentBooks) { this.currentBooks = currentBooks; }

    // Add a book by ISBN
    public void addBook(String isbn) {
        if (!currentBooks.contains(isbn)) {
            currentBooks.add(isbn);
        }
    }

    // Remove a book by ISBN
    public void removeBook(String isbn) {
        currentBooks.remove(isbn);
    }

    public boolean isPassword(String str) {
        return str.equals(password);
    }
}
