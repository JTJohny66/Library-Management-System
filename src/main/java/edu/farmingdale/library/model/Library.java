package edu.farmingdale.library.model;

import com.google.cloud.firestore.Firestore;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import edu.farmingdale.library.FirebaseConfig;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;





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
            instance.loadBooksFromCSV();
            instance.loadStudentsFromFirebase();
        }
        return instance;
    }

    public void loadBooksFromCSV() {
        String path = "/edu/farmingdale/library/books.csv";

        try (Scanner scanner = new Scanner(Objects.requireNonNull(getClass().getResourceAsStream(path)))) {

            if (scanner.hasNextLine()) scanner.nextLine(); // Skip header

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue; // skip blank lines

                String[] parts = line.split(",", 3); // ensure we only split into 3 parts

                if (parts.length < 3) {
                    System.out.println("Skipping invalid row: " + line);
                    continue;
                }

                String isbn = parts[0].trim();
                String title = parts[1].trim();
                String author = parts[2].trim();

                Book book = new Book(isbn, title, author, true, null);
                addBookCopy(book);
            }

            System.out.println("✅ Books loaded successfully.");

        } catch (Exception e) {
            System.out.println("⚠️ Error loading books: " + e.getMessage());
        }
    }

    public void loadStudentsFromFirebase() {
        try {
            Firestore db = FirebaseConfig.getDB();
            ApiFuture<QuerySnapshot> future = db.collection("students").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                Student s = doc.toObject(Student.class);
                students.put(s.getEmail().toLowerCase(Locale.ROOT), s);
            }

            System.out.println("✅ Loaded " + students.size() + " students from Firebase.");
        } catch (Exception e) {
            System.out.println("❌ Failed to load students: " + e.getMessage());
        }
    }





    // ====== STUDENT MANAGEMENT ======

    public void addStudent(Student student) {
        // Add to in-memory map
        students.put(student.getEmail().toLowerCase(Locale.ROOT), student);

        try {
            Firestore db = FirebaseConfig.getDB();
            ApiFuture<WriteResult> future = db.collection("students")
                    .document(student.getEmail().toLowerCase(Locale.ROOT))
                    .set(student);

            // Wait for confirmation
            future.get();
            System.out.println("✅ Student saved to Firebase: " + student.getEmail());

        } catch (Exception e) {
            System.out.println("❌ Failed to save student to Firebase: " + e.getMessage());
        }
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

    public Book getBookByIsbn(String isbn) {
        for (Book b : copiesById.values()) {
            if (b.getISBN().equalsIgnoreCase(isbn)) {
                return b;
            }
        }
        return null;
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

    // ====== BOOK SEARCHING ======

    public List<Book> searchByTitle(String title) {
        return copiesById.values().stream()
                .filter(b -> b.getBookTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    public List<Book> searchByAuthor(String author) {
        return copiesById.values().stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }

    public Book searchById(int id) {
        return copiesById.get(id); // direct O(1) lookup
    }


    // ====== DUE DATE TRACKING ======

    public void setDueDate(Book book, LocalDate date) {
        dueDates.put(book, date);
    }

    public LocalDate getDueDate(Book book) {
        return dueDates.get(book);
    }
}
