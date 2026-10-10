/**
 * Exceção lançada sempre que uma regra do negócio é violada
 * (ex.: ônibus lotado, motorista indisponível, estudante inadimplente).
 *
 * Usar uma exceção própria deixa claro, no catch, que o erro é uma regra
 * da associação e não um bug de programação.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
