package br.com.a2da.libraryapi.api.controller.book;

import br.com.a2da.libraryapi.api.controller.util.MarshallerService;
import br.com.a2da.libraryapi.api.exception.ApiErrors;
import br.com.a2da.libraryapi.api.exception.BusinessException;
import br.com.a2da.libraryapi.api.model.Book;
import br.com.a2da.libraryapi.api.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private MarshallerService marshallerService;

    public BookController(BookService bookService, MarshallerService marshallerService) {
        this.bookService = bookService;
        this.marshallerService = marshallerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HashMap<String, Object> save(@RequestBody @Valid BookForm bookForm) {

        Book bookInstance = bookService.save(
                bookForm.bindToSaveModel()
        );

        return marshallerService.bindBook(bookInstance);
    }

    @GetMapping("{id}")
    public HashMap<String, Object> show(@PathVariable Long id) {

        return bookService.findById(id)
                .map(bookInstance -> marshallerService.bindBook(bookInstance))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                ;
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
    public HashMap<String, Object> update(@PathVariable Long id, @RequestBody @Valid BookForm bookForm) {

        return bookService.findById(id).map(bookInstance -> {

            bookInstance.setAuthor(bookForm.getAuthor());
            bookInstance.setTitle(bookForm.getTitle());
            bookInstance.setIsbn(bookForm.getIsbn());

            return marshallerService.bindBook(
                    bookService.update(bookInstance)
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
