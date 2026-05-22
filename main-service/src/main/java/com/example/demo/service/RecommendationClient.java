package com.example.demo.service;
import com.example.demo.RecommendationRequest;
import com.example.demo.RecommendationResponse;
import com.example.demo.RecommendationServiceGrpc;
import com.example.demo.Book;
import com.example.demo.Loan;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanRepository;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationClient {

    @GrpcClient("recomendation-service")
    private RecommendationServiceGrpc.RecommendationServiceBlockingStub stub;
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    public RecommendationClient(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }
    public List<String> getRecommendations(Long userId) {
        List<com.example.demo.model.Loan> dbLoans = loanRepository.findAll();
        List<com.example.demo.model.Book> dbBooks = bookRepository.findAll();

        List<Loan> grpcLoans = dbLoans.stream().map(dbLoan -> Loan.newBuilder()
                .setId(dbLoan.getId())
                .setUserId(dbLoan.getUser().getId())
                .setBookId(dbLoan.getBook().getId())
                .build()).toList();


        List<Book> grpcBooks = dbBooks.stream().map(dbBook -> Book.newBuilder()
                .setId(dbBook.getId())
                .setTitle(dbBook.getTitle())
                .setAuthor(dbBook.getAuthor())
                .build()).toList();


        RecommendationRequest request = RecommendationRequest.newBuilder()
                .setUserId(userId)
                .addAllAllLoans(grpcLoans)
                .addAllAllBooks(grpcBooks)
                .build();


        RecommendationResponse response = stub.getRecommendations(request);

        return response.getTitlesList();
    }
    }
