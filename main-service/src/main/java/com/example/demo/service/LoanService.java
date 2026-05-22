package com.example.demo.service;

import com.example.demo.NotificationProducer;
import com.example.demo.RabbitConfig;
import com.example.demo.dto.NotificationMessage;
import com.example.demo.model.*;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.NotificationLogRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {
    private final LoanRepository loanRepo;
    private final UserRepository userRepo;
    private final BookRepository bookRepo;
    private final NotificationProducer notificationProducer;
    private final NotificationLogRepository logRepository;

    public LoanService(BookRepository bookRepo, UserRepository userRepo,LoanRepository loanRepo, NotificationProducer notificationProducer, NotificationLogRepository logRepository ) {
        this.loanRepo = loanRepo;
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
        this.notificationProducer = notificationProducer;
        this.logRepository = logRepository;
    }
    public List<Loan> getAll(LoanRepository loanRepository){return loanRepository.findAll();}
    @Transactional
    public Loan borrow(Long bookId, Long userId) {

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepo.findById(bookId).orElseThrow();

        long activeLoans = loanRepo.countByUserAndStatus(user, Status.BORROWED);

        if (user.getBorrowLimit() != null && activeLoans >= user.getBorrowLimit()) {
            throw new IllegalStateException("Borrow limit exceeded");
        }

        int numberOfCopies = book.getNumberOfCopies()-1;

        if (numberOfCopies < 0) {
            throw new IllegalStateException("All books are already borrowed");
        }
        if (!user.getActive()) {
            throw new IllegalStateException("User is blocked");
        }
        book.setNumberOfCopies(numberOfCopies);

        if (numberOfCopies == 0) {
            book.setStatus(Status.BORROWED);
        }
        else {
            book.setStatus(Status.AVAILABLE);
        }
        bookRepo.save(book);
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setStatus(Status.BORROWED);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));

        String msg = "You borrowed: " + book.getTitle();
        NotificationLog log = new NotificationLog(
                user.getId(),
                msg,
                LocalDateTime.now()
        );

        logRepository.save(log);

        notificationProducer.sendBorrowNotification(
                new NotificationMessage(user.getEmail(), "Book borrowed", msg)
        );

        return loanRepo.save(loan);
    }
    public Loan returnBook(Long bookId, Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Loan loan = loanRepo
                .findByBookIdAndUserIdAndStatus(bookId, userId, Status.BORROWED)
                .orElseThrow();
        Book book = bookRepo.findById(bookId).orElseThrow();
        loan.setStatus(Status.RETURNED);
        loan.setReturnDate(LocalDate.now());
        book.setStatus(Status.AVAILABLE);
        book.setNumberOfCopies(
                book.getNumberOfCopies() + 1
        );
        bookRepo.save(book);
        String msg = "You returned: " + book.getTitle();
        NotificationLog log = new NotificationLog(
                user.getId(),
                msg,
                LocalDateTime.now()
        );

        logRepository.save(log);

        notificationProducer.sendReturnNotification(
                new NotificationMessage(user.getEmail(), "Book returned", msg)
        );
        return loanRepo.save(loan);
    }

}
