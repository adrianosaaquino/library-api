package br.com.a2da.libraryapi.api.controller;

import br.com.a2da.libraryapi.api.controller.util.MarshallerService;
import br.com.a2da.libraryapi.api.exception.BusinessException;
import br.com.a2da.libraryapi.api.helpers.BookHelperTest;
import br.com.a2da.libraryapi.api.model.Book;
import br.com.a2da.libraryapi.api.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
    MarshallerService marshallerServiceMocked;

    @Autowired
    ObjectMapper objectMapper;

    final Book bookMocked = mock(Book.class);
    final Long ID = BookHelperTest.ID;
    final Long ID_NOT_FOUND = BookHelperTest.ID_NOT_FOUND;

    @AfterEach
    public void afterEachTest() {

        verifyNoMoreInteractions(bookMocked);
        verifyNoMoreInteractions(bookServiceMocked);
        verifyNoMoreInteractions(marshallerServiceMocked);
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
        given(bookServiceMocked.save(Mockito.any(Book.class))).willReturn(bookMocked);

        given(marshallerServiceMocked.bindBook(bookMocked)).willReturn(
                new HashMap<String, Object>() {{
                    put("id", ID);
                }}
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(ID))
        ;

        // And verify mocks interaction
        ArgumentCaptor<Book> bindToSaveModel = ArgumentCaptor.forClass(Book.class);
        verify(bookServiceMocked, times(1)).save(bindToSaveModel.capture());

        Book bookSaveParam = bindToSaveModel.getValue();
        assertThat(bookSaveParam.getAuthor()).isEqualTo(BookHelperTest.MACHADO_DE_ASSIS);
        assertThat(bookSaveParam.getTitle()).isEqualTo(BookHelperTest.DOM_CASMURRO);
        assertThat(bookSaveParam.getIsbn()).isEqualTo(BookHelperTest.DOM_CASMURRO_ISBN);

        verify(marshallerServiceMocked, times(1)).bindBook(bookMocked);
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
                    put("author", BookHelperTest.MACHADO_DE_ASSIS);
                    put("title", BookHelperTest.DOM_CASMURRO);
                    put("isbn", BookHelperTest.DOM_CASMURRO_ISBN);
                }}
        );

        // Expected that call save
        given(bookServiceMocked.save(Mockito.any(Book.class)))
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

        // And verify mocks interaction
        verify(bookServiceMocked, times(1))
                .save(Mockito.any(Book.class));
    }

    @Test
    @DisplayName("Deve obter informaçoes de um livro")
    public void getBookDetailsTest() throws Exception {

        // Given a Book ID that exists

        // Expected that call findById
        given(bookServiceMocked.findById(ID))
                .willReturn(Optional.of(bookMocked));

        given(marshallerServiceMocked.bindBook(bookMocked))
                .willReturn(new HashMap<String, Object>() {{
                    put("id", ID);
                }});

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
        verify(bookServiceMocked, times(1)).findById(ID);
        verify(marshallerServiceMocked, times(1)).bindBook(bookMocked);
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
        verify(bookServiceMocked, times(1)).findById(ID_NOT_FOUND);
    }

    @Test
    @DisplayName("Deve deletar um livro com sucesso")
    public void deleteBookTest() throws Exception {

        // Given a Book ID that exists

        // Expected that call findById
        given(bookServiceMocked.findById(ID)).willReturn(Optional.of(bookMocked));

        // When execute request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + ID));

        ResultActions resultActions = mockMvc.perform(request);

        // Then validate response
        resultActions
                .andExpect(status().isNoContent());

        // And verify mocks interaction
        verify(bookServiceMocked, times(1)).findById(ID);
        verify(bookServiceMocked, times(1)).delete(bookMocked);
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
        given(bookServiceMocked.findById(ID)).willReturn(Optional.of(bookMocked));

        given(bookServiceMocked.update(bookMocked)).willReturn(bookMocked);

        given(marshallerServiceMocked.bindBook(bookMocked)).willReturn(
                new HashMap<String, Object>() {{
                    put("id", ID);
                }}
        );

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
        verify(bookServiceMocked, times(1)).findById(ID);

        verify(bookMocked, times(1)).setAuthor(BookHelperTest.JORGE_AMADO);
        verify(bookMocked, times(1)).setTitle(BookHelperTest.CAPITAES_DA_AREIA);
        verify(bookMocked, times(1)).setIsbn(BookHelperTest.CAPITAES_DA_AREIA_ISBN);

        verify(bookServiceMocked, times(1)).update(bookMocked);

        verify(marshallerServiceMocked, times(1)).bindBook(bookMocked);
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
        given(bookServiceMocked.findById(ID_NOT_FOUND)).willReturn(Optional.empty());

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
        verify(bookServiceMocked, times(1)).findById(ID_NOT_FOUND);
    }

}

