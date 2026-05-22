package com.example.demo;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@GrpcService
public class RecommendationGrpcService
        extends RecommendationServiceGrpc.RecommendationServiceImplBase {
    @Override

    public void getRecommendations(
            RecommendationRequest request,
            StreamObserver<RecommendationResponse> responseObserver) {

        Long userId = request.getUserId();

        List<Loan> allLoans = request.getAllLoansList();
        List<Book> allBooks = request.getAllBooksList();

        List<Long> userBookIds = new ArrayList<>();
        for (Loan loan : allLoans) {
            if (loan.getUserId() == userId) {
                userBookIds.add(loan.getBookId());
            }
        }

        Set<String> userAuthors = new HashSet<>();
        for (Book book : allBooks) {
            if (userBookIds.contains(book.getId())) {
                userAuthors.add(book.getAuthor());
            }
        }

        List<String> recommendedTitles = new ArrayList<>();
        for (Book book : allBooks) {
            if (userAuthors.contains(book.getAuthor())) {
                recommendedTitles.add(book.getTitle());
            }
        }

        RecommendationResponse response = RecommendationResponse.newBuilder()
                .addAllTitles(recommendedTitles)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
