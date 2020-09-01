package br.com.a2da.libraryapi.core.repository;

import br.com.a2da.libraryapi.core.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);
}
