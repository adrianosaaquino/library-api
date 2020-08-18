package br.com.a2da.libraryapi.api.repository;

import br.com.a2da.libraryapi.api.helpers.BookHelperTest;
import br.com.a2da.libraryapi.api.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    public void setUp() {
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando o livro existir na base com o isbn informado")
    public void returnTrueWhenIsbnExists() {

        // Given a saved Book
        Book bookSavedInstance = BookHelperTest.createBookWithNullId();
        entityManager.persist(bookSavedInstance);

        // When
        boolean exists = repository.existsByIsbn(bookSavedInstance.getIsbn());

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
        boolean exists = repository.existsByIsbn(BookHelperTest.CAPITAES_DA_AREIA_ISBN);

        // Then
        assertThat(exists).isFalse();
    }
}
