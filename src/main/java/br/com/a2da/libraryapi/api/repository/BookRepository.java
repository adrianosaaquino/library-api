package br.com.a2da.libraryapi.api.repository;

import br.com.a2da.libraryapi.api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);
}
