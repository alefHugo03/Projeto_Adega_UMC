package api.servico.adega.exception;

/**
 * Exceção lançada quando uma operação de negócio inválida é tentada,
 * como editar um registro que já foi finalizado ou cancelado.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}