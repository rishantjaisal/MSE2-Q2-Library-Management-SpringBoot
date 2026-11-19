package com.kiet.library.controller;

import com.kiet.library.model.Book;
import com.kiet.library.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    // Q2(b): POST /api/books
    @PostMapping
    public ResponseEntity<?> addBook(@Valid @RequestBody Book book) {
        if (book.getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Book ID is required"));
        }
        try {
            repository.insert(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(book);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Book ID already exists"));
        }
    }

    // Q2(c): GET /api/books
    @GetMapping
    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    // Q2(c): GET /api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable int id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Book not found")));
    }

    // Q2(d): PUT /api/books/{id}; all four book attributes are updated.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable int id, @Valid @RequestBody Book book) {
        // The URL ID wins; a different ID in the JSON must not create another book.
        book.setId(id);
        int rows = repository.update(id, book);
        if (rows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Book not found"));
        }
        return ResponseEntity.ok(book);
    }

    // Q2(e): DELETE /api/books/{id}; appropriate HTTP responses.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable int id) {
        int rows = repository.deleteById(id);
        if (rows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Book not found"));
        }
        return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
    }
}
