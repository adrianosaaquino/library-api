package br.com.a2da.libraryapi.api.controller.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookQueryForm {

    // TODO - unit test
    private String title;

    private String author;
}
