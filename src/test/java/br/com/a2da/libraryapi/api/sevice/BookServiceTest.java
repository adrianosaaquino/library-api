package br.com.a2da.libraryapi.api.sevice;

import br.com.a2da.libraryapi.api.exception.BusinessException;
import br.com.a2da.libraryapi.api.helpers.BookHelperTest;
import br.com.a2da.libraryapi.api.model.Book;
import br.com.a2da.libraryapi.api.repository.BookRepository;
import br.com.a2da.libraryapi.api.service.BookService;
import br.com.a2da.libraryapi.api.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository repositoryMocked;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(repositoryMocked);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {

        // Given a valid Book
        Book bookToSave = BookHelperTest.createBookWithNullId();

        // Expected that call existsByIsbn
        BDDMockito
                .given(repositoryMocked.existsByIsbn(BookHelperTest.DOM_CASMURRO_ISBN))
                .willReturn(false);

        // and save
        BDDMockito
                .given(repositoryMocked.save(bookToSave))
                .willReturn(BookHelperTest.createBook());

        // When execute save
        Book bookSaved = bookService.save(bookToSave);

        // Then validate save return
        Assertions.assertThat(bookSaved.getId()).isEqualTo(BookHelperTest.ID);
        Assertions.assertThat(bookSaved.getAuthor()).isEqualTo(BookHelperTest.MACHADO_DE_ASSIS);
        Assertions.assertThat(bookSaved.getTitle()).isEqualTo(BookHelperTest.DOM_CASMURRO);
        Assertions.assertThat(bookSaved.getIsbn()).isEqualTo(BookHelperTest.DOM_CASMURRO_ISBN);
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN() {

        // Given a valid Book
        Book bookInstance = BookHelperTest.createBook();

        // Expected that call existsByIsbn
        BDDMockito
                .given(repositoryMocked.existsByIsbn(bookInstance.getIsbn()))
                .willReturn(true);

        // When execute save
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(bookInstance));

        // Then validate exception
        Assertions
                .assertThat(exception)
                .isNotNull()
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn ja cadastrado");

        // And
        BDDMockito
                .verify(repositoryMocked, Mockito.never())
                .save(bookInstance);
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest() {

        // Given a existing Book
        Book bookFoundInstance = BookHelperTest.createBook();

        // Expected that call findById
        BDDMockito
                .given(repositoryMocked.findById(BookHelperTest.ID))
                .willReturn(Optional.of(bookFoundInstance));

        // When execute save
        Optional<Book> foundBook = bookService.findById(BookHelperTest.ID);

        // Then
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(BookHelperTest.ID);
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(BookHelperTest.MACHADO_DE_ASSIS);
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(BookHelperTest.DOM_CASMURRO);
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(BookHelperTest.DOM_CASMURRO_ISBN);
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id que ele nao existe na base")
    public void bookNotFoundByIdTest() {

        // Given a existing Book

        // Expected that call findById
        BDDMockito
                .given(repositoryMocked.findById(BookHelperTest.ID))
                .willReturn(Optional.empty());

        // When execute save
        Optional<Book> foundBook = bookService.findById(BookHelperTest.ID);

        // Then
        Assertions.assertThat(foundBook.isPresent()).isFalse();
    }
}
