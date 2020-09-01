package br.com.a2da.libraryapi.core.service.book;

import br.com.a2da.libraryapi.core.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    Book save(Book any);

    Optional<Book> findById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(BookQuery filter, Pageable pageRequest);
}
