package br.com.a2da.libraryapi.api.controller.util;

import br.com.a2da.libraryapi.api.controller.book.BookForm;
import br.com.a2da.libraryapi.api.model.Book;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class MarshallerService {

    private final ModelMapper modelMapper = new ModelMapper();

    public HashMap<String, Object> bindBook(Book bookInstance) {

        return new HashMap<String, Object>() {{
            put("id", bookInstance.getId());
            put("author", bookInstance.getAuthor());
            put("title", bookInstance.getTitle());
            put("isbn", bookInstance.getId());
        }};
    }

    public Book bindBookFormToBook(BookForm bookForm) {
        return modelMapper.map(bookForm, Book.class);
    }

}
