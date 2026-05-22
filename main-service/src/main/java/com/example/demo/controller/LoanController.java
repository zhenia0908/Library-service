package com.example.demo.controller;

import com.example.demo.model.Loan;
import com.example.demo.model.Status;
import com.example.demo.model.User;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BookService;
import com.example.demo.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
public class LoanController {

    private final LoanService loanService;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    public LoanController(LoanService loanService, UserRepository userRepository, LoanRepository loanRepository){
        this.loanService = loanService;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }
    @GetMapping("/loans/active")
    public List<Loan> getActiveLoans() {
        return loanRepository.findByStatus(Status.BORROWED);
    }
    @GetMapping("/loans/all")
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
    @PostMapping("/{bookId}/borrow")
    public Loan borrow(@PathVariable Long bookId, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        return loanService.borrow(bookId, user.getId());
    }

    @PostMapping("/{bookId}/return")
    public Loan returnBook(@PathVariable Long bookId, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        return loanService.returnBook(bookId, user.getId());
    }
}
