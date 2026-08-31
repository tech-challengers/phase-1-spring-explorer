package br.com.foodapi.controller;

import br.com.foodapi.generated.model.FieldError;
import br.com.foodapi.generated.model.Problem;
import br.com.foodapi.infra.errors.InvalidPasswordException;
import br.com.foodapi.infra.errors.UserAlreadyExistsException;
import br.com.foodapi.infra.errors.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    FieldError fe = new FieldError();
                    fe.setField(error.getField());
                    fe.setMessage(error.getDefaultMessage());
                    return fe;
                })
                .distinct()
                .collect(Collectors.toList());

        Problem problem = new Problem();
        problem.setType("about:blank");
        problem.setTitle("Dados Inválidos");
        problem.setStatus(HttpStatus.BAD_REQUEST.value());
        problem.setDetail("Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.");
        problem.setInstance(((ServletWebRequest) request).getRequest().getRequestURI());

        problem.setObjects(fieldErrors);

        return handleExceptionInternal(ex, problem, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Problem> handleUserNotFound(UserNotFoundException ex) {
        Problem problem = new Problem();
        problem.setStatus(HttpStatus.NOT_FOUND.value());
        problem.setTitle("Not Found");
        problem.setDetail(ex.getMessage());
        problem.setType("User not found");
        problem.instance("/exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Problem> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        Problem problem = new Problem();
        problem.setStatus(HttpStatus.CONFLICT.value());
        problem.setTitle("Not Found");
        problem.setDetail(ex.getMessage());
        problem.setType("about:blank");
        problem.setInstance("/exception");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Problem> handleInvalidPassword(InvalidPasswordException ex) {
        Problem problem = new Problem();
        problem.setStatus(HttpStatus.BAD_REQUEST.value());
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        problem.setType("about:blank");
        problem.setInstance("/exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Problem> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String requiredType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "desconhecido";

        String detail = String.format(
                "O parâmetro '%s' recebeu o valor '%s', mas esperava o tipo '%s'.",
                ex.getName(),
                ex.getValue(),
                requiredType
        );

        Problem problem = new Problem();

        problem.setType("about:blank");
        problem.setTitle("Falha na Validação do Parâmetro");
        problem.setStatus(HttpStatus.BAD_REQUEST.value());
        problem.setDetail(detail);
        problem.setInstance("/exception");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problem);
    }
}
