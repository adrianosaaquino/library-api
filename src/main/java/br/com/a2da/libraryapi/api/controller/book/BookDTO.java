package br.com.a2da.libraryapi.api.controller.book;

import br.com.a2da.libraryapi.core.model.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String author;

    @NotEmpty
    private String isbn;

    public static BookDTO bindToDTO(Book bookInstance) {

        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(bookInstance, BookDTO.class);
    }
}
