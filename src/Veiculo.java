/** Veículo da frota (ônibus ou van, diferenciados pelo atributo tipo). */
public class Veiculo {
    private final String placa;
    private final int idInterno;
    private final TipoVeiculo tipo;
    private SituacaoVeiculo situacao;

    public Veiculo(String placa, int idInterno, TipoVeiculo tipo) {
        this.placa = Validador.exigirTexto(placa, "placa");
        Validador.exigirNaoNulo(tipo, "tipo");
        this.idInterno = idInterno;
        this.tipo = tipo;
        this.situacao = SituacaoVeiculo.DISPONIVEL; // todo veículo novo começa disponível
    }

    public String getPlaca() {
        return placa;
    }

    public int getIdInterno() {
        return idInterno;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public SituacaoVeiculo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoVeiculo situacao) {
        Validador.exigirNaoNulo(situacao, "situação");
        this.situacao = situacao;
    }

    // métodos de conveniência: o veículo "responde" sobre si mesmo (troca de mensagens)
    public int getCapacidade() {
        return tipo.getCapacidade();
    }

    public double getCustoPorKm() {
        return tipo.getCustoPorKm();
    }

    public boolean estaDisponivel() {
        return situacao == SituacaoVeiculo.DISPONIVEL;
    }

    @Override
    public String toString() {
        return tipo + " " + placa + " (nº " + idInterno + ")";
    }
}
