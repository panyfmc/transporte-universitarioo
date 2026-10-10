public enum SituacaoVeiculo {
    DISPONIVEL("Disponível"),
    EM_VIAGEM("Em viagem");

    private final String descricao;

    SituacaoVeiculo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
