package br.com.a2da.libraryapi.core.sevice.book;

import br.com.a2da.libraryapi.core.exception.BusinessException;
import br.com.a2da.libraryapi.core.model.Book;
import br.com.a2da.libraryapi.core.repository.BookRepository;
import br.com.a2da.libraryapi.core.service.book.BookService;
import br.com.a2da.libraryapi.core.service.book.BookServiceImpl;
import br.com.a2da.libraryapi.helperTest.BookHelperTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepositoryMocked;

    final Book bookToSaveMocked = mock(Book.class);
    final Book bookSavedMocked = mock(Book.class);
    final Long ID = BookHelperTest.ID;
    final Long ID_NOT_FOUND = BookHelperTest.ID_NOT_FOUND;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(bookRepositoryMocked);
    }

    @AfterEach
    public void afterEachTest() {

        verifyNoMoreInteractions(bookRepositoryMocked);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {

        // Given a valid Book

        // Expected that call
        given(bookToSaveMocked.getIsbn()).willReturn(BookHelperTest.DOM_CASMURRO_ISBN);
        given(bookRepositoryMocked.existsByIsbn(BookHelperTest.DOM_CASMURRO_ISBN)).willReturn(false);
        given(bookRepositoryMocked.save(bookToSaveMocked)).willReturn(bookSavedMocked);

        // When execute save
        Book bookSaved = bookService.save(bookToSaveMocked);

        // Then validate save return
        assertThat(bookSaved).isNotNull();

        // And verify mocks interaction
        verify(bookToSaveMocked, times(1)).getIsbn();
        verify(bookRepositoryMocked, times(1)).existsByIsbn(BookHelperTest.DOM_CASMURRO_ISBN);
        verify(bookRepositoryMocked, times(1)).save(Mockito.any(Book.class));
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN() {

        // Given a valid Book

        // Expected that call existsByIsbn
        given(bookToSaveMocked.getIsbn()).willReturn(BookHelperTest.DOM_CASMURRO_ISBN);
        given(bookRepositoryMocked.existsByIsbn(BookHelperTest.DOM_CASMURRO_ISBN)).willReturn(true);

        // When execute save
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(bookToSaveMocked));

        // Then validate exception
        assertThat(exception)
                .isNotNull()
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn ja cadastrado");

        // And verify mocks interaction
        verify(bookToSaveMocked, times(1)).getIsbn();
        verify(bookRepositoryMocked, times(1)).existsByIsbn(BookHelperTest.DOM_CASMURRO_ISBN);
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest() {

        // Given a existing Book

        // Expected that call findById
        given(bookRepositoryMocked.findById(ID)).willReturn(Optional.of(bookSavedMocked));

        // When execute save
        Optional<Book> foundBook = bookService.findById(ID);

        // Then
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get()).isEqualTo(bookSavedMocked);

        // And verify mocks interaction
        verify(bookRepositoryMocked, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id que ele nao existe na base")
    public void bookNotFoundByIdTest() {

        // Given a existing Book

        // Expected that call findById
        given(bookRepositoryMocked.findById(ID_NOT_FOUND)).willReturn(Optional.empty());

        // When execute save
        Optional<Book> foundBook = bookService.findById(ID_NOT_FOUND);

        // Then
        assertThat(foundBook.isPresent()).isFalse();

        // And verify mocks interaction
        verify(bookRepositoryMocked, times(1)).findById(ID_NOT_FOUND);
    }

    @Test
    @DisplayName("Deve excluir um livro")
    public void deleteBookTest() {

        // Given
        given(bookSavedMocked.getId()).willReturn(ID);

        // When
        bookService.delete(bookSavedMocked);

        // Then
        verify(bookSavedMocked, times(1)).getId();
        verify(bookRepositoryMocked, times(1)).delete(bookSavedMocked);
    }

    @Test
    @DisplayName("Devera lancar exception quando nao se informar um Book para exclusao")
    public void deleteBookNullTest() {

        // When execute save
        Throwable exception = Assertions.catchThrowable(() -> bookService.delete(null));

        // Then validate exception
        assertThat(exception)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cant be null");
    }

    @Test
    @DisplayName("Devera lancar exception quando se tenta exluir um Book sem ID")
    public void deleteBookWithoutIdTest() {

        // Given
        given(bookSavedMocked.getId()).willReturn(null);

        // When execute save
        Throwable exception = Assertions.catchThrowable(() -> bookService.delete(bookSavedMocked));

        // Then validate exception
        assertThat(exception)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cant be null");

        // Then
        verify(bookSavedMocked, times(1)).getId();
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() {

        // Given
        given(bookSavedMocked.getId()).willReturn(ID);
        given(bookRepositoryMocked.save(bookSavedMocked)).willReturn(bookSavedMocked);


        // When
        Book update = bookService.update(bookSavedMocked);

        // Then
        assertThat(update).isNotNull();

        // And verify mocks interaction
        verify(bookSavedMocked, times(1)).getId();
        verify(bookRepositoryMocked, times(1)).save(bookSavedMocked);
    }

    @Test
    @DisplayName("Devera lancar exception quando nao se informar um Book para update")
    public void updateBookNullTest() {

        // When execute save
        Throwable exception = Assertions.catchThrowable(() -> bookService.update(null));

        // Then validate exception
        assertThat(exception)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cant be null");
    }

    @Test
    @DisplayName("Devera lancar exception quando se tenta atualizar um Book sem ID")
    public void updateBookWithoutIdTest() {

        // Given
        given(bookSavedMocked.getId()).willReturn(null);

        // When execute save
        Throwable exception = Assertions.catchThrowable(() -> bookService.update(bookSavedMocked));

        // Then validate exception
        assertThat(exception)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cant be null");

        // Then
        verify(bookSavedMocked, times(1)).getId();
    }
}
