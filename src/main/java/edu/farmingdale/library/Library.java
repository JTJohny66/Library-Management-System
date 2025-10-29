package edu.farmingdale.library;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Library {

    private static Library instance;

    private HashMap<Integer, Book> copiesById;       // Key = Book ID
    private HashMap<String, Student> students;       // Key = student email
    private HashMap<Book, LocalDate> dueDates;       // Track borrowed book due dates

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

    // ====== STUDENT MANAGEMENT ======

    public void addStudent(Student student) {
        students.put(student.getEmail().toLowerCase(Locale.ROOT), student);
    }

    public boolean emailExists(String email) {
        return students.containsKey(email.toLowerCase(Locale.ROOT));
    }

    public Student getStudentByEmail(String email) {
        return students.get(email.toLowerCase(Locale.ROOT));
    }

    // ====== STUDENT SORTING ======

    public List<Student> getStudentsSortedByName() {
        return students.values().stream()
                .sorted(Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName))
                .collect(Collectors.toList());
    }


    public List<Student> getStudentsSortedByEmail() {
        return students.values().stream()
                .sorted(Comparator.comparing(Student::getEmail))
                .collect(Collectors.toList());
    }

    public List<Student> getStudentsSortedByID() {
        return students.values().stream()
                .sorted(Comparator.comparingInt(Student::getID))
                .collect(Collectors.toList());
    }

    public List<Student> getStudentsSortedByBooksBorrowed() {
        return students.values().stream()
                .sorted(Comparator.comparingInt(s -> s.getCurrentBooks().size()))
                .collect(Collectors.toList());
    }

    // ====== BOOK MANAGEMENT ======

    public void addBookCopy(Book book) {
        copiesById.put(book.getID(), book);
    }

    public Book getBookByID(int id) {
        return copiesById.get(id);
    }

    public Collection<Book> getAllBooks() {
        return copiesById.values();
    }

    // ====== BOOK SORTING ======

    public List<Book> getBooksSortedByTitle() {
        return copiesById.values().stream()
                .sorted(Comparator.comparing(Book::getBookTitle))
                .collect(Collectors.toList());
    }

    public List<Book> getBooksSortedByAuthor() {
        return copiesById.values().stream()
                .sorted(Comparator.comparing(Book::getAuthor))
                .collect(Collectors.toList());
    }

    public List<Book> getBooksSortedByID() {
        return copiesById.values().stream()
                .sorted(Comparator.comparingInt(Book::getID))
                .collect(Collectors.toList());
    }

    // ====== DUE DATE TRACKING ======

    public void setDueDate(Book book, LocalDate date) {
        dueDates.put(book, date);
    }

    public LocalDate getDueDate(Book book) {
        return dueDates.get(book);
    }
}
