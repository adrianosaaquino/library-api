package br.com.a2da.libraryapi.api.controller.book;

import br.com.a2da.libraryapi.api.exception.ApiErrors;
import br.com.a2da.libraryapi.core.exception.BusinessException;
import br.com.a2da.libraryapi.core.model.Book;
import br.com.a2da.libraryapi.core.service.book.BookQuery;
import br.com.a2da.libraryapi.core.service.book.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private BookMarshallerService bookMarshallerService;

    public BookController(BookService bookService, BookMarshallerService bookMarshallerService) {
        this.bookService = bookService;
        this.bookMarshallerService = bookMarshallerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO save(@RequestBody @Valid BookForm bookForm) {

        return bookMarshallerService.bindToBookDTO(
                bookService.save(
                        bookMarshallerService.bindToBookSave(bookForm)
                )
        );
    }

    @GetMapping("{id}")
    public BookDTO show(@PathVariable Long id) {

        return bookService.findById(id)
                .map(bookInstance -> bookMarshallerService.bindToBookDTO(bookInstance))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                ;
    }

    @GetMapping
    public Page<BookDTO> find(BookQueryForm bookQueryForm, Pageable pageRequest) {

        BookQuery bookQuery = bookMarshallerService.bindBookQueryFormToBookQuery(bookQueryForm);

        Page<Book> result = bookService.find(
                bookQuery,
                pageRequest
        );

        List<BookDTO> list = result.getContent()
                .stream()
                .map(bookInstance -> bookMarshallerService.bindToBookDTO(bookInstance))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        Book book = bookService.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        bookService.delete(book);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookForm bookForm) {

        return bookService.findById(id).map(bookInstance -> {

            Book bookToUpdateInstance = bookMarshallerService.bindToBookUpdate(bookForm, bookInstance);

            return bookMarshallerService.bindToBookDTO(
                    bookService.update(bookToUpdateInstance)
            );

        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {

        BindingResult bindingResult = ex.getBindingResult();

        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex);
    }
}
