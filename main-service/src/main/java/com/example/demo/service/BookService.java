package com.example.demo.service;

import com.example.demo.NotificationProducer;
import com.example.demo.dto.NotificationMessage;
import com.example.demo.model.Book;
import com.example.demo.model.Status;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repo;
    private final LoanRepository loanRepository;
    private final NotificationProducer producer;
    public BookService(BookRepository repo, NotificationProducer producer, LoanRepository loanRepository) {
        this.repo = repo;
        this.loanRepository = loanRepository;
        this.producer = producer;
    }

    public List<Book> getAll() {
        return repo.findAll();
    }
    public void delete(Long id) {
        Book book = repo.findById(id).orElseThrow();
        boolean borrowed = loanRepository.existsByBookAndStatus(book, Status.BORROWED);

        if (borrowed) {
            throw new IllegalStateException("Book cannot be deleted");
        }

        repo.delete(book);
    }
    public Book findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public Book save(Book book) {
        if (book.getStatus() == null) {
            book.setStatus(Status.AVAILABLE);
        }

        return repo.save(book);
    }
    public Book getById(Long id) {
        return repo.findById(id)
                .orElseThrow();
    }

}