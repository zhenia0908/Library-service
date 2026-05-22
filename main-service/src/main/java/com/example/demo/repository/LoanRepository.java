package com.example.demo.repository;

import com.example.demo.model.Book;
import com.example.demo.model.Loan;
import com.example.demo.model.Status;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    long countByUserAndStatus(User user, Status status);
    List<Loan> findByStatus(Status status);
    boolean existsByBook(Book book);

    boolean existsByBookAndStatus(Book book, Status status);

    Optional<Loan> findByBookIdAndUserIdAndStatus(Long bookId, Long userId, Status status);
}
