/** Pagamento mensal de um estudante. */
public class Pagamento {
    private final String mesReferencia; // ex.: "09/2026"
    private final double valor;
    private SituacaoPagamento situacao;

    public Pagamento(String mesReferencia, double valor, SituacaoPagamento situacao) {
        this.mesReferencia = Validador.exigirTexto(mesReferencia, "mês de referência");
        Validador.exigirPositivo(valor, "valor");
        Validador.exigirNaoNulo(situacao, "situação");
        this.valor = valor;
        this.situacao = situacao;
    }

    // SOBRECARGA de construtor: sem informar a situação, o pagamento nasce PENDENTE
    public Pagamento(String mesReferencia, double valor) {
        this(mesReferencia, valor, SituacaoPagamento.PENDENTE);
    }

    public String getMesReferencia() {
        return mesReferencia;
    }

    public double getValor() {
        return valor;
    }

    public SituacaoPagamento getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoPagamento situacao) {
        Validador.exigirNaoNulo(situacao, "situação");
        this.situacao = situacao;
    }

    public boolean estaAtrasado() {
        return situacao == SituacaoPagamento.ATRASADO;
    }
}
