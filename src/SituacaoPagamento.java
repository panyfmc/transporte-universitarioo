public enum SituacaoPagamento {
    PAGO("Pago"),
    PENDENTE("Pendente"),
    ATRASADO("Atrasado");

    private final String descricao;

    SituacaoPagamento(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
