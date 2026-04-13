package api.servico.adega.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Manipulador global de exceções da aplicação.
 *
 * Intercepta exceções lançadas pelos controllers e retorna respostas
 * HTTP padronizadas com status codes apropriados.
 */
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
     * Trata exceções genéricas e retorna erro 500.
     *
     * Qualquer exceção não tratada especificamente é capturada aqui
     * para evitar retornar responses não padronizadas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro interno na aplicação.",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
