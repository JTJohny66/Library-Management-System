package edu.farmingdale.library;

public class Student {

    private static int nextID = 100000;
    private int ID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Book[] bookList;

    public Student( String password, String email, String lastName, String firstName) {
        this.ID = nextID++;
        this.password = password;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public int getID() {
        return ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Book[] getBookList() {
        return bookList;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addBook(){

    }


}
