package br.com.a2da.libraryapi.api.exception;

import br.com.a2da.libraryapi.core.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {

    private List<String> errors;

    public ApiErrors(BindingResult bindingResult) {

        this.errors = new ArrayList<String>();

        bindingResult.getAllErrors()
                .forEach(
                        error -> this.errors.add(
                                error.getDefaultMessage()
                        )
                );

    }

    public ApiErrors(BusinessException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
