package edu.farmingdale.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {

    private static int nextID = 100000;
    private int ID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Store ISBNs, not Book objects → Firebase-friendly
    private List<String> currentBooks;

    // Store due dates as Map<ISBN, LocalDate string> → Firebase-friendly
    private Map<String, String> dueDates;

    //Firebase constructor
    public Student() {
        this.currentBooks = new ArrayList<>();
        this.dueDates = new HashMap<>();
    }

    //Regular Constructor
    public Student(String password, String email, String lastName, String firstName) {
        this.ID = nextID++;
        this.password = password;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.currentBooks = new ArrayList<>();
        this.dueDates = new HashMap<>();
    }

    //Getters and Setters
    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<String> getCurrentBooks() { return currentBooks; }
    public void setCurrentBooks(List<String> currentBooks) { this.currentBooks = currentBooks; }

    public Map<String, String> getDueDates() { return dueDates; }
    public void setDueDates(Map<String, String> dueDates) { this.dueDates = dueDates; }

    // Add a book by ISBN with due date
    public void addBook(String isbn, LocalDate dueDate) {
        if (!currentBooks.contains(isbn)) {
            currentBooks.add(isbn);
            if (dueDate != null) {
                dueDates.put(isbn, dueDate.toString());
            }
        }
    }

    // Remove a book by ISBN
    public void removeBook(String isbn) {
        currentBooks.remove(isbn);
        dueDates.remove(isbn);
    }

    //Get due date for book
    public LocalDate getDueDateForBook(String isbn) {
        String dateStr = dueDates.get(isbn);
        if (dateStr != null) {
            try {
                return LocalDate.parse(dateStr);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    //Password Check
    public boolean isPassword(String str) {
        return str.equals(password);
    }
}