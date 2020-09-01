package br.com.a2da.libraryapi.api.controller.book;

import br.com.a2da.libraryapi.api.helpers.BookHelperTest;
import br.com.a2da.libraryapi.core.exception.BusinessException;
import br.com.a2da.libraryapi.core.model.Book;
import br.com.a2da.libraryapi.core.service.book.BookQuery;
import br.com.a2da.libraryapi.core.service.book.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    /*
     * Toda action em uma controller devera receber:
     * - PathVariable: atributo passado direto na assinatura da action
     * - EntityForm: Classe responsavel por receber os params da request e mapear para o metodo
     * - EntityQueryForm: Classe responsavel por receber os params da request e mapear para o metodo
     *
     * O EntityMarshallerService tera a responsabilidade de converter (entrada):
     * - EntityForm -> Entity que sera passado para o service (service.method(Entity))
     * - EntityQueryForm -> EntityQuery que sera passado para o service (service.method(EntityQuery))
     *
     * O EntityMarshallerService tera a responsabilidade de converter (saida):
     * - Entity -> EntityDTO -> que sera retorada pela action
     * - EntityQueryForm -> EntityQuery ?????
     * */

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookServiceMocked;

    @MockBean
    BookMarshallerService bookMarshallerServiceMocked;

    @Autowired
    ObjectMapper objectMapper;

    final Book bookFromBookFormMocked = mock(Book.class);
    final Book bookWithIdMocked = mock(Book.class);
    final BookQuery bookQueryMocked = mock(BookQuery.class);
    final Long ID = BookHelperTest.ID;
    final Long ID_NOT_FOUND = BookHelperTest.ID_NOT_FOUND;
    BookDTO bookDTOWithId;

    @BeforeEach
    public void beforeEachTest() {

        bookDTOWithId = BookDTO.builder().id(ID).build();
    }

    @AfterEach
    public void afterEachTest() {

        verifyNoMoreInteractions(bookFromBookFormMocked);
        verifyNoMoreInteractions(bookWithIdMocked);
        verifyNoMoreInteractions(bookQueryMocked);
        verifyNoMoreInteractions(bookServiceMocked);
        verifyNoMoreInteractions(bookMarshallerServiceMocked);
    }

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {

        // Given a valid JSON body
        String jsonRequest = objectMapper.writeValueAsString(
                new HashMap<String, Object>() {{
                    put("author", BookHelperTest.MACHADO_DE_ASSIS);
                    put("title", BookHelperTest.DOM_CASMURRO);
                    put("isbn", BookHelperTest.DOM_CASMURRO_ISBN);
                }}
        );

        // Expected that call
        given(bookMarshallerServiceMocked.bindToBookSave(Mockito.any(BookForm.class)))
                .willReturn(bookFromBookFormMocked);
        given(bookServiceMocked.save(bookFromBookFormMocked))
                .willReturn(bookWithIdMocked);
        given(bookMarshallerServiceMocked.bindToBookDTO(bookWithIdMocked))
                .willReturn(bookDTOWithId);

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
                .andExpect(jsonPath("id").value(ID))
        ;

        // And verify mocks interaction
        ArgumentCaptor<BookForm> bookForm = ArgumentCaptor.forClass(BookForm.class);

        verify(bookMarshallerServiceMocked, times(1))
                .bindToBookSave(bookForm.capture());

        BookForm bookFormParam = bookForm.getValue();
        assertThat(bookFormParam.getAuthor()).isEqualTo(BookHelperTest.MACHADO_DE_ASSIS);
        assertThat(bookFormParam.getTitle()).isEqualTo(BookHelperTest.DOM_CASMURRO);
        assertThat(bookFormParam.getIsbn()).isEqualTo(BookHelperTest.DOM_CASMURRO_ISBN);

        verify(bookServiceMocked, times(1)).save(bookFromBookFormMocked);
        verify(bookMarshallerServiceMocked, times(1)).bindToBookDTO(bookWithIdMocked);
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
    @DisplayName("Deve retornar um erro quando save lançar BusinessException")
    public void createBookWithDuplicatedIsbn() throws Exception {

        // Given JSON body with duplicated isbn
        String jsonRequest = objectMapper.writeValueAsString(
                new HashMap<Object, Object>() {{
                    put("author", BookHelperTest.MACHADO_DE_ASSIS);
                    put("title", BookHelperTest.DOM_CASMURRO);
                    put("isbn", BookHelperTest.DOM_CASMURRO_ISBN);
                }}
        );

        // Expected that call save
        given(bookMarshallerServiceMocked.bindToBookSave(Mockito.any(BookForm.class)))
                .willReturn(bookFromBookFormMocked);
        given(bookServiceMocked.save(bookFromBookFormMocked))
                .willThrow(new BusinessException("Error"));

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
                .andExpect(jsonPath("errors[0]").value("Error"));

        // And verify mocks interaction
        verify(bookMarshallerServiceMocked, times(1))
                .bindToBookSave(Mockito.any(BookForm.class));

        verify(bookServiceMocked, times(1))
                .save(Mockito.any(Book.class));
    }

    @Test
    @DisplayName("Deve obter informaçoes de um livro")
    public void getBookDetailsTest() throws Exception {

        // Given a Book ID that exists

        // Expected that call findById
        given(bookServiceMocked.findById(ID))
                .willReturn(Optional.of(bookWithIdMocked));
        given(bookMarshallerServiceMocked.bindToBookDTO(bookWithIdMocked))
                .willReturn(bookDTOWithId);

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + ID))
                .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(ID))
        ;

        // And verify mocks interaction
        verify(bookServiceMocked, times(1))
                .findById(ID);
        verify(bookMarshallerServiceMocked, times(1))
                .bindToBookDTO(bookWithIdMocked);
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado nao existir")
    public void bookNotFoundTest() throws Exception {

        // Given a Book ID that not exists

        // Expected that call findById
        given(bookServiceMocked.findById(ID_NOT_FOUND)).willReturn(Optional.empty());

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + ID_NOT_FOUND))
                .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isNotFound())
        ;

        // And verify mocks interaction
        verify(bookServiceMocked, times(1))
                .findById(ID_NOT_FOUND);
    }

    @Test
    @DisplayName("Deve deletar um livro com sucesso")
    public void deleteBookTest() throws Exception {

        // Given a Book ID that exists

        // Expected that call findById
        given(bookServiceMocked.findById(ID))
                .willReturn(Optional.of(bookWithIdMocked));

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + ID));

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isNoContent());

        // And verify mocks interaction
        verify(bookServiceMocked, times(1)).findById(ID);
        verify(bookServiceMocked, times(1)).delete(bookWithIdMocked);
    }

    @Test
    @DisplayName("Deve retornar resource not found quando nao encontrar o livro para deletar")
    public void deleteBookNotFoundTest() throws Exception {

        // Given a Book ID that exists

        // Expected that call findById
        given(bookServiceMocked.findById(ID_NOT_FOUND))
                .willReturn(Optional.empty());

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + ID_NOT_FOUND));

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isNotFound());

        // And verify mocks interaction
        verify(bookServiceMocked, times(1))
                .findById(ID_NOT_FOUND);
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    public void updateBookTest() throws Exception {

        // Given a valid JSON body
        String jsonRequest = objectMapper.writeValueAsString(
                new HashMap<Object, Object>() {{
                    put("author", BookHelperTest.JORGE_AMADO);
                    put("title", BookHelperTest.CAPITAES_DA_AREIA);
                    put("isbn", BookHelperTest.CAPITAES_DA_AREIA_ISBN);
                }}
        );

        // Expected that call
        given(bookServiceMocked.findById(ID))
                .willReturn(Optional.of(bookWithIdMocked));

        given(
                bookMarshallerServiceMocked.bindToBookUpdate(
                        Mockito.any(BookForm.class),
                        eq(bookWithIdMocked)
                )
        ).willReturn(bookWithIdMocked);

        given(bookServiceMocked.update(bookWithIdMocked))
                .willReturn(bookWithIdMocked);

        given(bookMarshallerServiceMocked.bindToBookDTO(bookWithIdMocked))
                .willReturn(bookDTOWithId);

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API + "/" + ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(ID))
        ;

        // And verify mocks interaction
        verify(bookServiceMocked, times(1))
                .findById(ID);

        ArgumentCaptor<BookForm> bookForm = ArgumentCaptor.forClass(BookForm.class);

        verify(bookMarshallerServiceMocked, times(1))
                .bindToBookUpdate(bookForm.capture(), eq(bookWithIdMocked));

        BookForm bookFormParam = bookForm.getValue();
        assertThat(bookFormParam.getAuthor()).isEqualTo(BookHelperTest.JORGE_AMADO);
        assertThat(bookFormParam.getTitle()).isEqualTo(BookHelperTest.CAPITAES_DA_AREIA);
        assertThat(bookFormParam.getIsbn()).isEqualTo(BookHelperTest.CAPITAES_DA_AREIA_ISBN);

        verify(bookServiceMocked, times(1))
                .update(bookWithIdMocked);

        verify(bookMarshallerServiceMocked, times(1))
                .bindToBookDTO(bookWithIdMocked);
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    public void updateNotFoundBookTest() throws Exception {

        // Given a valid JSON body
        String jsonRequest = objectMapper.writeValueAsString(
                new HashMap<Object, Object>() {{
                    put("author", BookHelperTest.JORGE_AMADO);
                    put("title", BookHelperTest.CAPITAES_DA_AREIA);
                    put("isbn", BookHelperTest.CAPITAES_DA_AREIA_ISBN);
                }}
        );

        // Expected that call findById
        given(bookServiceMocked.findById(ID_NOT_FOUND))
                .willReturn(Optional.empty());

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API + "/" + ID_NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isNotFound());

        // And verify mocks interaction
        verify(bookServiceMocked, times(1))
                .findById(ID_NOT_FOUND);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() throws Exception {

        // Given a query request
        String queryString = String.format(
                "?title=%s&author=%s&page=0&size=100",
                BookHelperTest.DOM_CASMURRO,
                BookHelperTest.MACHADO_DE_ASSIS
        );

        // Expected that call
        given(bookMarshallerServiceMocked.bindBookQueryFormToBookQuery(any(BookQueryForm.class)))
                .willReturn(bookQueryMocked);

        given(bookServiceMocked.find(eq(bookQueryMocked), any(Pageable.class)))
                .willReturn(
                        new PageImpl<Book>(
                                Arrays.asList(bookWithIdMocked, bookWithIdMocked),
                                PageRequest.of(0, 100),
                                1
                        )
                );

        given(bookMarshallerServiceMocked.bindToBookDTO(bookWithIdMocked))
                .willReturn(bookDTOWithId);

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(2)))
                .andExpect(jsonPath("totalElements").value(2))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0))
        ;

        // And verify mocks interaction

        ArgumentCaptor<BookQueryForm> bookQueryForm = ArgumentCaptor.forClass(BookQueryForm.class);

        verify(bookMarshallerServiceMocked, times(1))
                .bindBookQueryFormToBookQuery(bookQueryForm.capture());

        BookQueryForm bookQueryFormParam = bookQueryForm.getValue();
        assertThat(bookQueryFormParam.getAuthor()).isEqualTo(BookHelperTest.MACHADO_DE_ASSIS);
        assertThat(bookQueryFormParam.getTitle()).isEqualTo(BookHelperTest.DOM_CASMURRO);


        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);

        verify(bookServiceMocked, times(1))
                .find(eq(bookQueryMocked), pageable.capture());

        Pageable pageableParam = pageable.getValue();
        assertThat(pageableParam.getPageNumber()).isEqualTo(0);
        assertThat(pageableParam.getPageSize()).isEqualTo(100);


        verify(bookMarshallerServiceMocked, times(2))
                .bindToBookDTO(bookWithIdMocked);
    }
}

