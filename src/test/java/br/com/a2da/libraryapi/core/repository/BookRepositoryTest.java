package br.com.a2da.libraryapi.core.repository;

import br.com.a2da.libraryapi.core.model.Book;
import br.com.a2da.libraryapi.helperTest.BookHelperTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    public void setUp() {
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando o livro existir na base com o isbn informado")
    public void returnTrueWhenIsbnExists() {

        // Given a saved Book
        Book bookSavedInstance = BookHelperTest.createBookWithNullId();
        entityManager.persist(bookSavedInstance);

        // When
        boolean exists = bookRepository.existsByIsbn(bookSavedInstance.getIsbn());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando nao existir um livo na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExists() {

        // Given a saved Book
        Book bookSavedInstance = BookHelperTest.createBookWithNullId();
        entityManager.persist(bookSavedInstance);

        // When
        boolean exists = bookRepository.existsByIsbn(BookHelperTest.CAPITAES_DA_AREIA_ISBN);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por ID")
    public void findByIdTest() {

        // Given a saved Book
        Book bookSavedInstance = BookHelperTest.createBookWithNullId();
        entityManager.persist(bookSavedInstance);

        // When
        Optional<Book> bookInstance = bookRepository.findById(bookSavedInstance.getId());

        // Then
        assertThat(bookInstance.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {

        // Given a new book
        Book bookInstance = BookHelperTest.createBookWithNullId();

        // When
        Book bookSavedInstance = bookRepository.save(bookInstance);

        // Then
        assertThat(bookSavedInstance.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve exluir um livro")
    public void deleteBookTest() {

        // Given a saved Book
        Book bookSavedInstance = BookHelperTest.createBookWithNullId();
        entityManager.persist(bookSavedInstance);
        Long bookId = bookSavedInstance.getId();

        // When
        bookRepository.delete(bookSavedInstance);

        // Then
        assertThat(bookRepository.findById(bookId)).isNotNull();
    }
}
