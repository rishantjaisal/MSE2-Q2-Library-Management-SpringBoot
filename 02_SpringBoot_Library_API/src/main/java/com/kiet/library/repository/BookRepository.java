package com.kiet.library.repository;

import com.kiet.library.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbc;

    private static final RowMapper<Book> BOOK_MAPPER = (result, rowNum) -> new Book(
            result.getInt("id"),
            result.getString("title"),
            result.getString("author"),
            result.getString("category"),
            result.getInt("available_copies")
    );

    public BookRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Book> findAll() {
        return jdbc.query("SELECT id, title, author, category, available_copies FROM books ORDER BY id", BOOK_MAPPER);
    }

    public Optional<Book> findById(int id) {
        List<Book> results = jdbc.query(
                "SELECT id, title, author, category, available_copies FROM books WHERE id = ?",
                BOOK_MAPPER, id);
        return results.stream().findFirst();
    }

    public int insert(Book book) {
        return jdbc.update(
                "INSERT INTO books (id, title, author, category, available_copies) VALUES (?, ?, ?, ?, ?)",
                book.getId(), book.getTitle(), book.getAuthor(), book.getCategory(), book.getAvailableCopies());
    }

    public int update(int id, Book book) {
        return jdbc.update(
                "UPDATE books SET title = ?, author = ?, category = ?, available_copies = ? WHERE id = ?",
                book.getTitle(), book.getAuthor(), book.getCategory(), book.getAvailableCopies(), id);
    }

    public int deleteById(int id) {
        return jdbc.update("DELETE FROM books WHERE id = ?", id);
    }
}
