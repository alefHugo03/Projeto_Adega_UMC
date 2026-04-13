package api.servico.adega.exception;

/**
 * Exceção lançada quando um recurso não é encontrado no banco de dados.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor que recebe o tipo de recurso, o campo usado na busca e o valor procurado.
     */
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s não encontrado com %s : '%s'", resource, field, value));
    }
}
