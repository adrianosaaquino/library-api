package br.com.a2da.libraryapi.api.controller.book;

import br.com.a2da.libraryapi.api.controller.util.ControllerHelperService;
import br.com.a2da.libraryapi.api.dto.BookDTO;
import br.com.a2da.libraryapi.api.exception.ApiErrors;
import br.com.a2da.libraryapi.api.exception.BusinessException;
import br.com.a2da.libraryapi.api.model.Book;
import br.com.a2da.libraryapi.api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private ModelMapper modelMapper;
    private ControllerHelperService controllerHelperService;

    public BookController(BookService bookService, ModelMapper modelMapper, ControllerHelperService controllerHelperService) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
        this.controllerHelperService = controllerHelperService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO save(@RequestBody @Valid BookForm bookForm) {

        Book bookInstance = bookService.save(
                bookForm.bindToSaveModel()
        );

        return BookDTO.bindToDTO(bookInstance);
    }

    @GetMapping("{id}")
    public BookDTO show(@PathVariable Long id) {

        return bookService.findById(id)
                .map(BookDTO::bindToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                ;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        Book book = bookService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        bookService.delete(book);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookForm bookForm) {

        Book bookInstance = bookService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        bookInstance.setAuthor(bookForm.getAuthor());
        bookInstance.setTitle(bookForm.getTitle());
        bookInstance.setIsbn(bookForm.getIsbn());

        return BookDTO.bindToDTO(
                bookService.update(bookInstance)
        );
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
