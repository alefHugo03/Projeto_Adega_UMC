package api.servico.adega.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Classe padrão para retornar erros da API.
 *
 * Contém informações sobre o erro ocorrido para facilitar o diagnóstico
 * no lado do cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Código HTTP do erro (ex: 404, 500, 400).
     */
    private int status;

    /**
     * Mensagem descritiva do erro.
     */
    private String message;

    /**
     * Timestamp de quando o erro ocorreu.
     */
    private LocalDateTime timestamp;

    /**
     * Caminho da requisição que causou o erro.
     */
    private String path;
}
