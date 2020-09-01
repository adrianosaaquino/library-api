package br.com.a2da.libraryapi.api.controller.util;

import br.com.a2da.libraryapi.api.controller.book.BookForm;
import br.com.a2da.libraryapi.api.controller.book.BookDTO;
import br.com.a2da.libraryapi.core.model.Book;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

// TODO - delete-me?

@Service
public class ControllerHelperService {

    private final ModelMapper modelMapper = new ModelMapper();

    public Book bindBookFormToBook(BookForm bookForm) {
        return modelMapper.map(bookForm, Book.class);
    }

    public BookDTO bindBookToBookDTO(Book bookInstance) {
        return modelMapper.map(bookInstance, BookDTO.class);
    }
}
