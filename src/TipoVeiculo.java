/**
 * Tipos de veículo da associação. Cada constante já carrega seus dados fixos
 * (capacidade e custo por km), então não existe número "solto" espalhado pelo código.
 */
public enum TipoVeiculo {
    ONIBUS("Ônibus", 40, 5.00),
    VAN("Van", 20, 2.00);

    private final String descricao;
    private final int capacidade;
    private final double custoPorKm;

    TipoVeiculo(String descricao, int capacidade, double custoPorKm) {
        this.descricao = descricao;
        this.capacidade = capacidade;
        this.custoPorKm = custoPorKm;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public double getCustoPorKm() {
        return custoPorKm;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
