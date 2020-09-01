package br.com.a2da.libraryapi.core.service.book;

import br.com.a2da.libraryapi.core.exception.BusinessException;
import br.com.a2da.libraryapi.core.model.Book;
import br.com.a2da.libraryapi.core.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public void delete(Book book) {

        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cant be null");
        }

        bookRepository.delete(book);
    }

    @Override
    public Book update(Book book) {

        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cant be null");
        }

        return bookRepository.save(book);
    }

    @Override
    public Page<Book> find(BookQuery filter, Pageable pageRequest) {
        return null;
    }
}
