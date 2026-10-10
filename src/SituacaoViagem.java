/** Ciclo de vida de uma viagem: PLANEJADA -> CONFIRMADA -> EM_ANDAMENTO -> CONCLUIDA. */
public enum SituacaoViagem {
    PLANEJADA("Planejada"),
    CONFIRMADA("Confirmada"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída");

    private final String descricao;

    SituacaoViagem(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
