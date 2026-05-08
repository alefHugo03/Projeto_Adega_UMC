package api.servico.adega.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Trata a exceção ResourceNotFoundException e retorna erro 404.
     *
     * Quando um recurso (ex: usuário) não é encontrado no banco de dados,
     * a aplicação retorna um 404 com mensagem descritiva.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Trata a exceção BadRequestException e retorna erro 400.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Trata a exceção ConflictException e retorna erro 409.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(
            ConflictException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    /**
     * Trata a exceção InsufficientStockException e retorna erro 409.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleInsufficientStockException(
            InsufficientStockException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    /**
     * Trata a exceção InvalidOperationException e retorna erro 400.
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<?> handleInvalidOperationException(
            InvalidOperationException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Trata erros de validação de campos (@Valid).
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        String mensagem = "Erro de validação: " + ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (acc, curr) -> acc.isEmpty() ? curr : acc + ", " + curr);
        
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                mensagem,
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Trata violação de integridade (ex: e-mail já cadastrado).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflito de dados: O registro já existe ou viola uma regra de negócio.",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Trata ResponseStatusException (ex: o conflito de e-mail que lançamos no service).
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(
            ResponseStatusException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode().value(),
                ex.getReason(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    /**
     * Trata exceções genéricas e retorna erro 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(
            Exception ex,
            WebRequest request) {

        ex.printStackTrace(); // Adicione isso para ver o erro real nos logs do Docker

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro interno na aplicação.",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
