package br.com.a2da.libraryapi.api.service;

import br.com.a2da.libraryapi.api.model.Book;

import java.util.Optional;

public interface BookService {

    Book save(Book any);

    Optional<Book> findById(Long id);

    void delete(Book book);

    Book update(Book book);
}
