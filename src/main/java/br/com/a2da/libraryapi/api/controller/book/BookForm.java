package br.com.a2da.libraryapi.api.controller.book;

import br.com.a2da.libraryapi.api.model.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookForm {

    // TODO - unit test

    private Long id;

    @NotNull
    @NotEmpty
    private String title;

    @NotNull
    @NotEmpty
    private String author;

    @NotNull
    @NotEmpty
    private String isbn;

    public Book bindToSaveModel() {

        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(this, Book.class);
    }
}
