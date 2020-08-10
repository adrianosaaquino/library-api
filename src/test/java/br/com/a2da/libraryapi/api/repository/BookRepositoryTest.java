package br.com.a2da.libraryapi.api.repository;

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

        // cenario
        entityManager.persist(BookRepositoryHelperTest.createNewBook());

        // execuçao
        boolean exists = repository.existsByIsbn(BookRepositoryHelperTest.ISBN);


        // verificacao
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando nao existir um livo na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExists() {

        // cenario
        entityManager.persist(BookRepositoryHelperTest.createNewBook());

        // execuçao
        boolean exists = repository.existsByIsbn("456");


        // verificacao
        assertThat(exists).isFalse();
    }
}

class BookRepositoryHelperTest {

    public static String TITLE = "Um titulo";
    public static String AUTHOR = "Um author";
    public static String ISBN = "133";

    public static Book createNewBook() {
        return Book.builder()
                .title(TITLE)
                .author(AUTHOR)
                .isbn(ISBN)
                .build();
    }

}
