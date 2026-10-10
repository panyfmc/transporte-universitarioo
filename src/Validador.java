/**
 * Métodos utilitários para validar dados de entrada.
 * Centralizar aqui evita repetir os mesmos "if" em todas as classes (princípio DRY).
 */
public final class Validador {

    // construtor privado: a classe só tem métodos estáticos, ninguém precisa instanciá-la
    private Validador() {
    }

    /** Garante que o texto não é nulo nem vazio e devolve ele sem espaços nas pontas. */
    public static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O campo '" + campo + "' é obrigatório.");
        }
        return valor.trim();
    }

    public static void exigirNaoNulo(Object valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("O campo '" + campo + "' é obrigatório.");
        }
    }

    public static void exigirPositivo(double valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O campo '" + campo + "' deve ser maior que zero.");
        }
    }
}
