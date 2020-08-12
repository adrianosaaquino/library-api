package br.com.a2da.libraryapi.api.helpers;

import br.com.a2da.libraryapi.api.dto.BookDTO;
import br.com.a2da.libraryapi.api.model.Book;

// BookControllerHelperTest
public class BookHelperTest {

    public static Long ID_NOT_FOUND = 12L;
    public static Long ID = 11L;
    public static String MACHADO_DE_ASSIS = "Machado de Assis";
    public static String DOM_CASMURRO = "Dom Casmurro";
    public static String DOM_CASMURRO_ISBN = "1111111111111111111";
    public static String JORGE_AMADO = "Jorge Amado";
    public static String CAPITAES_DA_AREIA = "Capitães da Areia";
    public static String CAPITAES_DA_AREIA_ISBN = "222222222222222222";

    public static Book createBook() {
        return Book.builder()
                .id(ID)
                .author(MACHADO_DE_ASSIS)
                .title(DOM_CASMURRO)
                .isbn(DOM_CASMURRO_ISBN)
                .build();
    }

    public static Book createBookWithNullId() {
        return Book.builder()
                .author(MACHADO_DE_ASSIS)
                .title(DOM_CASMURRO)
                .isbn(DOM_CASMURRO_ISBN)
                .build();
    }

    public static Book createBook(Long id) {
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
