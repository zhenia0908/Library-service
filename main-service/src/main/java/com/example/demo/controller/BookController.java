package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/books")
@CrossOrigin
public class BookController {

    private final BookService service;
    private final BookRepository bookRepository;

    public BookController(BookService service, BookRepository bookRepository) {

        this.service = service;
        this.bookRepository = bookRepository;
    }


    @GetMapping
    public CollectionModel<EntityModel<Book>> getAll() {

        List<EntityModel<Book>> books = service.getAll()
                .stream()
                .map(book -> EntityModel.of(book,

                        linkTo(methodOn(BookController.class)
                                .getById(book.getId()))
                                .withSelfRel()

                ))
                .toList();

        return CollectionModel.of(
                books,

                linkTo(methodOn(BookController.class)
                        .getAll())
                        .withSelfRel(),

                linkTo(methodOn(BookController.class)
                        .create(null))
                        .withRel("create")
        );
    }


    @GetMapping("/{id}")
    public EntityModel<Book> getById(@PathVariable Long id) {

        Book book = service.getById(id);

        return EntityModel.of(
                book,

                linkTo(methodOn(BookController.class)
                        .getById(id))
                        .withSelfRel(),

                linkTo(methodOn(BookController.class)
                        .getAll())
                        .withRel("all-books")
        );
    }


    @PostMapping
    public EntityModel<Book> create(@RequestBody Book book) {

        Book savedBook = service.save(book);

        return EntityModel.of(
                savedBook,

                linkTo(methodOn(BookController.class)
                        .getById(savedBook.getId()))
                        .withSelfRel(),

                linkTo(methodOn(BookController.class)
                        .getAll())
                        .withRel("all-books")
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {

        Book existingBook = service.findById(id);
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setNumberOfCopies(bookDetails.getNumberOfCopies());

        Book updatedBook = service.save(existingBook);

        return ResponseEntity.ok(updatedBook);
    }
}