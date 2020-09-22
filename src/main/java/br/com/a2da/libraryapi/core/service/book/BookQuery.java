package br.com.a2da.libraryapi.core.service.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookQuery {

    // TODO - unit test
    private String title;

    private String author;

}
