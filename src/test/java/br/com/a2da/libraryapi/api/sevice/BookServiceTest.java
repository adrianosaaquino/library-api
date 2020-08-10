package br.com.a2da.libraryapi.api.sevice;

import br.com.a2da.libraryapi.api.exception.BusinessException;
import br.com.a2da.libraryapi.api.model.Book;
import br.com.a2da.libraryapi.api.repository.BookRepository;
import br.com.a2da.libraryapi.api.service.BookService;
import br.com.a2da.libraryapi.api.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repositoryMocked;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repositoryMocked);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {

        // cenario
        Book book = BookServiceHelperTest.createNewBook();

        // expected
        Mockito.when(repositoryMocked.existsByIsbn(book.getIsbn())).thenReturn(
                false
        );
        Mockito.when(repositoryMocked.save(book)).thenReturn(
                Book.builder()
                        .id(1L)
                        .title("Um titulo")
                        .author("Um author")
                        .isbn("133")
                        .build()
        );

        // execuçao
        Book savedBook = service.save(book);

        // verificaçao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("133");
        assertThat(savedBook.getTitle()).isEqualTo("Um titulo");
        assertThat(savedBook.getAuthor()).isEqualTo("Um author");
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN() {

        // cenario
        Book book = BookServiceHelperTest.createNewBook();

        // expected
        Mockito.when(repositoryMocked.existsByIsbn(book.getIsbn())).thenReturn(
                true
        );

        // execuçao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        assertThat(exception)
                .isNotNull()
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn ja cadastrado");

        Mockito.verify(repositoryMocked, Mockito.never()).save(book);
    }
}

class BookServiceHelperTest {

    static Book createNewBook() {
        return Book.builder()
                .title("Um titulo")
                .author("Um author")
                .isbn("133")
                .build();
    }

}
