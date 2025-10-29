package edu.farmingdale.library;

import java.util.HashMap;
import java.time.LocalDate;

public class Library {

    private static Library instance;

    private Library() {
        copiesById = new HashMap<>();
        students = new HashMap<>();
        dueDates = new HashMap<>();
    }

    public static Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }


    private HashMap<Integer, Book> copiesById;
    private HashMap<Integer, Student> students;
    private HashMap<Book, LocalDate> dueDates;


    public void addBookCopy(Book book) {
        copiesById.put(book.getID(), book);
    }

    // ==== Add Student ====
    public void addStudent(Student student) {
        students.put(student.getID(), student);
    }

}
