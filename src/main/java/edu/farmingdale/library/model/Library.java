package edu.farmingdale.library.model;

import com.google.cloud.firestore.Firestore;
import edu.farmingdale.library.FirebaseConfig;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Library {

    //instance for singleton class
    private static Library instance;

    private HashMap<Integer, Book> copiesById;
    private HashMap<String, Student> students;
    private HashMap<String, Admin> admins;

    //Library Constructor is private so it can only be called internally (Singleton)
    private Library() {
        copiesById = new HashMap<>();
        students = new HashMap<>();
        admins = new HashMap<>();
    }

    //This is the public initialization. If instance null call constructor, if instance != null, call that instance
    public static Library getInstance() {
        if (instance == null) {
            instance = new Library();
            instance.loadBooksFromFirebase();
            if (instance.copiesById.isEmpty()) {
                instance.loadBooksFromCSV();
            }
            instance.loadStudentsFromFirebase();
            instance.loadAdminsFromFirebase();
            instance.syncBookAvailability();
        }
        return instance;
    }

    //Load CSV file of Books
    public void loadBooksFromCSV() {
        String path = "/edu/farmingdale/library/books.csv";

        try (Scanner scanner = new Scanner(Objects.requireNonNull(getClass().getResourceAsStream(path)))) {

            if (scanner.hasNextLine()) scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 3);

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

            System.out.println("✅ Books loaded successfully from CSV.");

        } catch (Exception e) {
            System.out.println("⚠️ Error loading books from CSV: " + e.getMessage());
        }
    }

    //Load Books from firebase
    public void loadBooksFromFirebase() {
        try {
            Firestore db = FirebaseConfig.getDB();
            ApiFuture<QuerySnapshot> future = db.collection("books").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                int id = doc.getLong("id").intValue();
                String isbn = doc.getString("isbn");
                String title = doc.getString("title");
                String author = doc.getString("author");
                Boolean inLibraryObj = doc.getBoolean("inLibrary");
                boolean inLibrary = (inLibraryObj != null) ? inLibraryObj : true;

                Book book = new Book(isbn, title, author, inLibrary, null);

                // Load ratings if they exist
                List<Long> ratings = (List<Long>) doc.get("ratings");
                if (ratings != null) {
                    for (Long rating : ratings) {
                        book.addRating(rating.intValue());
                    }
                }

                copiesById.put(id, book);
            }

            System.out.println("✅ Loaded " + copiesById.size() + " books from Firebase.");
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load books from Firebase (will try CSV): " + e.getMessage());
        }
    }

    //Load Students from Firebase
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

    //Load Admins from Firebase
    public void loadAdminsFromFirebase() {
        try {
            Firestore db = FirebaseConfig.getDB();
            ApiFuture<QuerySnapshot> future = db.collection("admins").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                Admin a = doc.toObject(Admin.class);
                admins.put(a.getUsername().toLowerCase(Locale.ROOT), a);
            }

            System.out.println("✅ Loaded " + admins.size() + " admins from Firebase.");

            // Create default admin if none exists
            if (admins.isEmpty()) {
                Admin defaultAdmin = new Admin("admin", "Admin@123", "System Administrator");
                addAdmin(defaultAdmin);
                System.out.println("✅ Created default admin account (username: admin, password: Admin@123)");
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to load admins: " + e.getMessage());
        }
    }

    // -------------------------------Admin Section---------------------------------------

    public void addAdmin(Admin admin) {
        admins.put(admin.getUsername().toLowerCase(Locale.ROOT), admin);
        saveAdminToFirebase(admin);
    }

    private void saveAdminToFirebase(Admin admin) {
        try {
            Firestore db = FirebaseConfig.getDB();
            ApiFuture<WriteResult> future = db.collection("admins")
                    .document(admin.getUsername().toLowerCase(Locale.ROOT))
                    .set(admin);

            future.get();
            System.out.println("✅ Admin saved to Firebase: " + admin.getUsername());

        } catch (Exception e) {
            System.out.println("❌ Failed to save admin to Firebase: " + e.getMessage());
        }
    }

    public Admin getAdminByUsername(String username) {
        return admins.get(username.toLowerCase(Locale.ROOT));
    }

    public boolean adminExists(String username) {
        return admins.containsKey(username.toLowerCase(Locale.ROOT));
    }

    // -------------------------------Student Section---------------------------------------

    public void addStudent(Student student) {
        students.put(student.getEmail().toLowerCase(Locale.ROOT), student);
        saveStudentToFirebase(student);
    }

    private void saveStudentToFirebase(Student student) {
        try {
            Firestore db = FirebaseConfig.getDB();
            ApiFuture<WriteResult> future = db.collection("students")
                    .document(student.getEmail().toLowerCase(Locale.ROOT))
                    .set(student);

            future.get();
            System.out.println("✅ Student saved to Firebase: " + student.getEmail());

        } catch (Exception e) {
            System.out.println("❌ Failed to save student to Firebase: " + e.getMessage());
        }
    }

    public void updateStudentInFirebase(Student student) {
        saveStudentToFirebase(student);
    }

    public boolean emailExists(String email) {
        return students.containsKey(email.toLowerCase(Locale.ROOT));
    }

    public Student getStudentByEmail(String email) {
        return students.get(email.toLowerCase(Locale.ROOT));
    }

    // -------------------------------Student Sorting Section---------------------------------------

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

    // -------------------------------Book Section---------------------------------------

    public void addBookCopy(Book book) {
        copiesById.put(book.getID(), book);
    }

    // Admin adds a new book (saves to Firebase)
    public void addNewBook(Book book) {
        copiesById.put(book.getID(), book);
        saveBookToFirebase(book);
    }

    public void updateBook(Book book) {
        copiesById.put(book.getID(), book);
        saveBookToFirebase(book);
    }

    public void deleteBook(int bookId) {
        Book book = copiesById.remove(bookId);
        if (book != null) {
            deleteBookFromFirebase(bookId);
        }
    }

    private void saveBookToFirebase(Book book) {
        try {
            Firestore db = FirebaseConfig.getDB();
            Map<String, Object> bookData = new HashMap<>();
            bookData.put("id", book.getID());
            bookData.put("isbn", book.getISBN());
            bookData.put("title", book.getBookTitle());
            bookData.put("author", book.getAuthor());
            bookData.put("inLibrary", book.getInLibrary());
            bookData.put("ratings", book.getRatings());

            ApiFuture<WriteResult> future = db.collection("books")
                    .document(String.valueOf(book.getID()))
                    .set(bookData);

            future.get();
            System.out.println("✅ Book saved to Firebase: " + book.getBookTitle());

        } catch (Exception e) {
            System.out.println("❌ Failed to save book to Firebase: " + e.getMessage());
        }
    }

    private void deleteBookFromFirebase(int bookId) {
        try {
            Firestore db = FirebaseConfig.getDB();
            ApiFuture<WriteResult> future = db.collection("books")
                    .document(String.valueOf(bookId))
                    .delete();

            future.get();
            System.out.println("✅ Book deleted from Firebase: " + bookId);

        } catch (Exception e) {
            System.out.println("❌ Failed to delete book from Firebase: " + e.getMessage());
        }
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

    // -------------------------------Book Sorting Section---------------------------------------

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

    // -------------------------------Book Searching Section---------------------------------------

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
        return copiesById.get(id);
    }

    // -------------------------------Due Date Section---------------------------------------

    public LocalDate getDueDate(Book book) {
        // Get due date from the student who has the book
        if (book.getPossesion() != null) {
            return book.getPossesion().getDueDateForBook(book.getISBN());
        }
        return null;
    }

    public boolean isOverdue(Book book) {
        LocalDate dueDate = getDueDate(book);
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue(Book book) {
        LocalDate dueDate = getDueDate(book);
        if (dueDate == null || !LocalDate.now().isAfter(dueDate)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public List<Student> getStudentsWithOverdueBooks() {
        return students.values().stream()
                .filter(student -> student.getCurrentBooks().stream()
                        .anyMatch(isbn -> {
                            Book book = getBookByIsbn(isbn);
                            return book != null && isOverdue(book);
                        }))
                .collect(Collectors.toList());
    }

    private void syncBookAvailability() {
        for (Student student : students.values()) {
            for (String isbn : student.getCurrentBooks()) {
                Book book = getBookByIsbn(isbn);
                if (book != null) {
                    book.setInLibrary(false);
                    book.setPossesion(student);
                    System.out.println("📚 Synced: " + book.getBookTitle() + " → borrowed by " + student.getFirstName());
                }
            }
        }
        System.out.println("✅ Book availability synced with student records.");
    }
}