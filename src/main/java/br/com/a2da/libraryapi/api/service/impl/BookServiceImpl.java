package br.com.a2da.libraryapi.api.service.impl;

import br.com.a2da.libraryapi.api.exception.BusinessException;
import br.com.a2da.libraryapi.api.model.Book;
import br.com.a2da.libraryapi.api.repository.BookRepository;
import br.com.a2da.libraryapi.api.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book bookInstance) {

        if (bookRepository.existsByIsbn(bookInstance.getIsbn())) {
            throw new BusinessException("Isbn ja cadastrado");
        }

        return bookRepository.save(bookInstance);
    }
}
