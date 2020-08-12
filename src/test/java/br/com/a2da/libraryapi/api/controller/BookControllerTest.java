package br.com.a2da.libraryapi.api.controller;

import br.com.a2da.libraryapi.api.controller.util.ControllerHelperService;
import br.com.a2da.libraryapi.api.dto.BookDTO;
import br.com.a2da.libraryapi.api.exception.BusinessException;
import br.com.a2da.libraryapi.api.model.Book;
import br.com.a2da.libraryapi.api.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookServiceMocked;

    @MockBean
    ControllerHelperService controllerHelperService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {

        // Given a valid JSON body
        String jsonRequest = objectMapper.writeValueAsString(
                new HashMap<Object, Object>() {{
                    put("author", BookCHTest.MACHADO_DE_ASSIS);
                    put("title", BookCHTest.DOM_CASMURRO);
                    put("isbn", BookCHTest.DOM_CASMURRO_ISBN);
                }}
        );

        // Expected that call save
        BDDMockito
                .given(bookServiceMocked.save(Mockito.any(Book.class)))
                .willReturn(BookCHTest.createNewBook());

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(BookCHTest.ID))
                .andExpect(jsonPath("author").value(BookCHTest.MACHADO_DE_ASSIS))
                .andExpect(jsonPath("title").value(BookCHTest.DOM_CASMURRO))
                .andExpect(jsonPath("isbn").value(BookCHTest.DOM_CASMURRO_ISBN));

        // And check book bind to save
        ArgumentCaptor<Book> bookFromBookForm = ArgumentCaptor.forClass(Book.class);
        BDDMockito
                .verify(bookServiceMocked, Mockito.times(1))
                .save(bookFromBookForm.capture());

        Book bookSaveParam = bookFromBookForm.getValue();
        Assertions.assertThat(bookSaveParam.getAuthor()).isEqualTo(BookCHTest.MACHADO_DE_ASSIS);
        Assertions.assertThat(bookSaveParam.getTitle()).isEqualTo(BookCHTest.DOM_CASMURRO);
        Assertions.assertThat(bookSaveParam.getIsbn()).isEqualTo(BookCHTest.DOM_CASMURRO_ISBN);
    }

    @Test
    @DisplayName("Deve lançar erro de validaçao quando nao houver dados suficiente para validaçao do livro")
    public void createInvalidBookTest() throws Exception {

        // Given a invalid JSON body
        String jsonRequest = objectMapper.writeValueAsString(
                new HashMap<>()
        );

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(6)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn que ja existe")
    public void createBookWithDuplicatedIsbn() throws Exception {

        // Given JSON body with duplicated isbn
        String jsonRequest = objectMapper.writeValueAsString(
                new HashMap<Object, Object>() {{
                    put("author", BookCHTest.MACHADO_DE_ASSIS);
                    put("title", BookCHTest.DOM_CASMURRO);
                    put("isbn", BookCHTest.DOM_CASMURRO_ISBN);
                }}
        );

        // Expected that call save
        BDDMockito.given(bookServiceMocked.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("Isbn ja cadastrado"));

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Isbn ja cadastrado"));
    }

    @Test
    @DisplayName("Deve obter informaçoes de um livro")
    public void getBookDetailsTest() throws Exception {

        // Given a Book ID that exists

        // Expected that call findById
        BDDMockito
                .given(bookServiceMocked.findById(BookCHTest.ID))
                .willReturn(Optional.of(BookCHTest.createNewBook()));

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + BookCHTest.ID))
                .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(BookCHTest.ID))
                .andExpect(jsonPath("author").value(BookCHTest.MACHADO_DE_ASSIS))
                .andExpect(jsonPath("title").value(BookCHTest.DOM_CASMURRO))
                .andExpect(jsonPath("isbn").value(BookCHTest.DOM_CASMURRO_ISBN))
        ;
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado nao existir")
    public void bookNotFoundTest() throws Exception {

        // Given a Book ID that not exists

        // Expected that call findById
        BDDMockito
                .given(bookServiceMocked.findById(BookCHTest.ID_NOT_FOUND))
                .willReturn(Optional.empty());

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + BookCHTest.ID_NOT_FOUND))
                .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("Deve deletar um livro com sucesso")
    public void deleteBookTest() throws Exception {

        // Given a Book ID that exists

        // Expected that call findById
        BDDMockito
                .given(bookServiceMocked.findById(BookCHTest.ID))
                .willReturn(Optional.of(BookCHTest.createNewBook()));

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + BookCHTest.ID));

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isNoContent());

        // And check book to delete
        ArgumentCaptor<Book> bookFromFindById = ArgumentCaptor.forClass(Book.class);
        BDDMockito
                .verify(bookServiceMocked, Mockito.times(1))
                .delete(bookFromFindById.capture());

        Book bookDeleteParam = bookFromFindById.getValue();
        Assertions.assertThat(bookDeleteParam.getId()).isEqualTo(BookCHTest.ID);
    }

    @Test
    @DisplayName("Deve retornar resource not found quando nao encontrar o livro para deletar")
    public void deleteBookNotFoundTest() throws Exception {

        // Given a Book ID that exists

        // Expected that call findById
        BDDMockito
                .given(bookServiceMocked.findById(BookCHTest.ID_NOT_FOUND))
                .willReturn(Optional.empty());

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + BookCHTest.ID_NOT_FOUND));

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isNotFound());

        // And check book to delete
        BDDMockito
                .verify(bookServiceMocked, Mockito.never())
                .delete(Mockito.any(Book.class));
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    public void updateBookTest() throws Exception {

        // Given a valid JSON body
        String jsonRequest = objectMapper.writeValueAsString(
                new HashMap<Object, Object>() {{
                    put("author", BookCHTest.JORGE_AMADO);
                    put("title", BookCHTest.CAPITAES_DA_AREIA);
                    put("isbn", BookCHTest.CAPITAES_DA_AREIA_ISBN);
                }}
        );

        // Expected that call findById
        Book bookToBeUpdateMocked = BookCHTest.createNewBook();

        BDDMockito
                .given(bookServiceMocked.findById(BookCHTest.ID))
                .willReturn(Optional.of(bookToBeUpdateMocked));

        // And update
        BDDMockito
                .given(bookServiceMocked.update(bookToBeUpdateMocked))
                .willReturn(bookToBeUpdateMocked);

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API + "/" + BookCHTest.ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(BookCHTest.ID))
                .andExpect(jsonPath("author").value(BookCHTest.JORGE_AMADO))
                .andExpect(jsonPath("title").value(BookCHTest.CAPITAES_DA_AREIA))
                .andExpect(jsonPath("isbn").value(BookCHTest.CAPITAES_DA_AREIA_ISBN));

        // And check book bind to update
        ArgumentCaptor<Book> bookFromFindById = ArgumentCaptor.forClass(Book.class);
        BDDMockito
                .verify(bookServiceMocked, Mockito.times(1))
                .update(bookFromFindById.capture());

        Book bookUpdateParam = bookFromFindById.getValue();
        Assertions.assertThat(bookUpdateParam.getId()).isEqualTo(BookCHTest.ID);
        Assertions.assertThat(bookUpdateParam.getAuthor()).isEqualTo(BookCHTest.JORGE_AMADO);
        Assertions.assertThat(bookUpdateParam.getTitle()).isEqualTo(BookCHTest.CAPITAES_DA_AREIA);
        Assertions.assertThat(bookUpdateParam.getIsbn()).isEqualTo(BookCHTest.CAPITAES_DA_AREIA_ISBN);
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    public void updateNotFoundBookTest() throws Exception {

        // Given a valid JSON body
        String jsonRequest = objectMapper.writeValueAsString(
                new HashMap<Object, Object>() {{
                    put("author", BookCHTest.JORGE_AMADO);
                    put("title", BookCHTest.CAPITAES_DA_AREIA);
                    put("isbn", BookCHTest.CAPITAES_DA_AREIA_ISBN);
                }}
        );

        // Expected that call findById
        BDDMockito
                .given(bookServiceMocked.findById(BookCHTest.ID_NOT_FOUND))
                .willReturn(Optional.empty());

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API + "/" + BookCHTest.ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isNotFound());

        // And check book bind to update
        ArgumentCaptor<Book> bookFromFindById = ArgumentCaptor.forClass(Book.class);
        BDDMockito
                .verify(bookServiceMocked, Mockito.never())
                .update(Mockito.any(Book.class));
    }

}

// BookControllerHelperTest
class BookCHTest {

    public static Long ID_NOT_FOUND = 12L;
    public static Long ID = 11L;
    public static String MACHADO_DE_ASSIS = "Machado de Assis";
    public static String DOM_CASMURRO = "Dom Casmurro";
    public static String DOM_CASMURRO_ISBN = "1111111111111111111";
    public static String JORGE_AMADO = "Jorge Amado";
    public static String CAPITAES_DA_AREIA = "Capitães da Areia";
    public static String CAPITAES_DA_AREIA_ISBN = "222222222222222222";

    public static Book createNewBook() {
        return Book.builder()
                .id(ID)
                .author(MACHADO_DE_ASSIS)
                .title(DOM_CASMURRO)
                .isbn(DOM_CASMURRO_ISBN)
                .build();
    }

    public static Book createNewBook(Long id) {
        return Book.builder()
                .id(id)
                .author(MACHADO_DE_ASSIS)
                .title(DOM_CASMURRO)
                .isbn(DOM_CASMURRO_ISBN)
                .build();
    }

    static BookDTO createNewBookDTO(String author, String title, String isbn) {
        return BookDTO.builder()
                .author(author)
                .title(title)
                .isbn(isbn)
                .build();
    }

    static BookDTO createNewBookDTO() {
        return createNewBookDTO(MACHADO_DE_ASSIS, DOM_CASMURRO, DOM_CASMURRO_ISBN);
    }

}
