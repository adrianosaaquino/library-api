package br.com.a2da.libraryapi.api.controller.book;

import br.com.a2da.libraryapi.core.model.Book;
import br.com.a2da.libraryapi.core.service.book.BookQuery;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class BookMarshallerService {

    // TODO - tests

    private final ModelMapper modelMapper = new ModelMapper();

    public BookDTO bindToBookDTO(Book bookInstance) {
        return modelMapper.map(bookInstance, BookDTO.class);
    }

    public Book bindToBookSave(BookForm bookForm) {
        return modelMapper.map(bookForm, Book.class);
    }

    public Book bindToBookUpdate(BookForm bookForm, Book bookInstance) {

        bookInstance.setAuthor(bookForm.getAuthor());
        bookInstance.setTitle(bookForm.getTitle());
        bookInstance.setIsbn(bookForm.getIsbn());

        return bookInstance;
    }

    public BookQuery bindBookQueryFormToBookQuery(BookQueryForm bookQueryForm) {
        return modelMapper.map(bookQueryForm, BookQuery.class);
    }
}
