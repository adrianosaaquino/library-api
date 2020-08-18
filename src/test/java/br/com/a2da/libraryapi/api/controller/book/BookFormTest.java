package br.com.a2da.libraryapi.api.controller.book;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookFormTest {

    /*private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public void beforeAll() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public void close() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("Deve transferir os dados para Book")
    public void bindToSaveModelValidTest() {

        // Given a valid BookForm
        BookForm bookForm = BookForm.builder()
                .id(BookHelperTest.ID)
                .author(BookHelperTest.MACHADO_DE_ASSIS)
                .title(BookHelperTest.DOM_CASMURRO)
                .isbn(BookHelperTest.DOM_CASMURRO_ISBN)
                .build();

        // When
        Book bookInstance = bookForm.bindToSaveModel();

        // Then
        assertThat(bookInstance).isNotNull();
        assertThat(bookInstance.getId()).isEqualTo(bookForm.getId());
        assertThat(bookInstance.getAuthor()).isEqualTo(bookForm.getAuthor());
        assertThat(bookInstance.getTitle()).isEqualTo(bookForm.getTitle());
        assertThat(bookInstance.getIsbn()).isEqualTo(bookForm.getIsbn());
    }

    @Test
    @DisplayName("Deve dar erro quando BookForm for invalido")
    public void bindToSaveModelInvalidTest() {

        // Given a valid BookForm
        BookForm bookForm = BookForm.builder().build();

        // When
        Set<ConstraintViolation<BookForm>> violations = validator.validate(bookForm);

        // Then
        // assertThat(violations).isEmpty();
        assertThat(bookInstance).isNotNull();
        assertThat(bookInstance.getId()).isEqualTo(bookForm.getId());
        assertThat(bookInstance.getAuthor()).isEqualTo(bookForm.getAuthor());
        assertThat(bookInstance.getTitle()).isEqualTo(bookForm.getTitle());
        assertThat(bookInstance.getIsbn()).isEqualTo(bookForm.getIsbn());

        assertEquals(violations.size(), 1);

        ConstraintViolation<Player> violation
                = violations.iterator().next();
        assertEquals("size must be between 3 and 3",
                violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("a", violation.getInvalidValue());
    }*/
}
