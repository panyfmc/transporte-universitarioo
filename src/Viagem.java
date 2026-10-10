import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Uma viagem: saída de um veículo em certa data, turno e rota, com um motorista
 * e uma lista de estudantes.
 *
 * A Viagem protege as regras que dependem só dela mesma (capacidade, duplicidade,
 * inadimplência). As regras que dependem de OUTRAS viagens (motorista/veículo
 * ocupados) ficam no ViagemService.
 */
public class Viagem {
    // AGREGAÇÃO: a viagem USA estes objetos, mas eles existem independentemente dela
    private LocalDate data;
    private Turno turno;
    private Rota rota;
    private Veiculo veiculo;
    private Motorista motorista;
    private final ArrayList<Estudante> estudantes = new ArrayList<>(); // agora é criada!
    private SituacaoViagem situacao = SituacaoViagem.PLANEJADA;

    public Viagem(LocalDate data, Turno turno, Rota rota, Veiculo veiculo, Motorista motorista) {
        setData(data);
        setTurno(turno);
        setRota(rota);
        setVeiculo(veiculo);
        setMotorista(motorista);
    }

    //sets da classe (só funcionam enquanto a viagem está PLANEJADA)
    public void setData(LocalDate data) {
        exigirPlanejada();
        Validador.exigirNaoNulo(data, "data");
        this.data = data;
    }

    public void setTurno(Turno turno) {
        exigirPlanejada();
        Validador.exigirNaoNulo(turno, "turno");
        this.turno = turno;
    }

    public void setRota(Rota rota) {
        exigirPlanejada();
        Validador.exigirNaoNulo(rota, "rota");
        if (!estudantes.isEmpty()) {
            throw new RegraNegocioException("Não é possível trocar a rota de uma viagem que já tem estudantes.");
        }
        this.rota = rota;
    }

    public void setVeiculo(Veiculo veiculo) {
        exigirPlanejada();
        Validador.exigirNaoNulo(veiculo, "veículo");
        if (estudantes.size() > veiculo.getCapacidade()) {
            throw new RegraNegocioException("O veículo " + veiculo + " não comporta os "
                    + estudantes.size() + " estudantes já incluídos.");
        }
        this.veiculo = veiculo;
    }

    public void setMotorista(Motorista motorista) {
        exigirPlanejada();
        Validador.exigirNaoNulo(motorista, "motorista");
        this.motorista = motorista;
    }

    //gets da classe
    public LocalDate getData() {
        return data;
    }

    public Turno getTurno() {
        return turno;
    }

    public Rota getRota() {
        return rota;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public SituacaoViagem getSituacao() {
        return situacao;
    }

    // ----- métodos do parâmetro estudantes (era o "falta criar" do colega) -----

    public void adicionarEstudante(Estudante estudante) {
        Validador.exigirNaoNulo(estudante, "estudante");
        if (situacao == SituacaoViagem.EM_ANDAMENTO || situacao == SituacaoViagem.CONCLUIDA) {
            throw new RegraNegocioException("Não é possível alterar os passageiros de uma viagem "
                    + situacao.toString().toLowerCase() + ".");
        }
        if (estudante.isInadimplente()) {
            throw new RegraNegocioException("O estudante " + estudante + " está inadimplente ("
                    + estudante.contarPagamentosAtrasados() + " pagamentos em atraso) e não pode viajar.");
        }
        if (!estudante.getRota().mesmaRota(rota)) {
            throw new RegraNegocioException("O estudante " + estudante + " pertence à rota "
                    + estudante.getRota() + ", não à rota " + rota + ".");
        }
        if (possuiEstudante(estudante.getMatricula())) {
            throw new RegraNegocioException("O estudante " + estudante + " já está nesta viagem.");
        }
        if (estudantes.size() >= veiculo.getCapacidade()) {
            throw new RegraNegocioException("Capacidade máxima do veículo (" + veiculo.getCapacidade()
                    + " passageiros) atingida. Não foi possível incluir " + estudante + ".");
        }
        estudantes.add(estudante);
    }

    // SOBRECARGA: mesmo nome de método, parâmetros diferentes (um estudante x uma lista)
    public void adicionarEstudante(ArrayList<Estudante> lista) {
        for (Estudante estudante : lista) {
            adicionarEstudante(estudante); // reaproveita todas as validações do método acima
        }
    }

    /** Remove pelo número de matrícula. Devolve true se encontrou e removeu. */
    public boolean removerEstudante(String matricula) {
        for (int i = 0; i < estudantes.size(); i++) {
            if (estudantes.get(i).getMatricula().equals(matricula)) {
                estudantes.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean possuiEstudante(String matricula) {
        for (Estudante estudante : estudantes) {
            if (estudante.getMatricula().equals(matricula)) {
                return true;
            }
        }
        return false;
    }

    /** Devolve uma cópia: a lista interna só é alterada pelos métodos acima (que validam). */
    public ArrayList<Estudante> getEstudantes() {
        return new ArrayList<>(estudantes);
    }

    public int getQuantidadeEstudantes() {
        return estudantes.size();
    }

    public int getVagasDisponiveis() {
        return veiculo.getCapacidade() - estudantes.size();
    }

    // ----- custo -----

    /** Custo = distância da rota x custo por km do veículo. */
    public double calcularCusto() {
        return rota.getDistanciaKm() * veiculo.getCustoPorKm();
    }

    // ----- ciclo de vida -----

    // sem "public": só o ViagemService (mesmo pacote) pode confirmar uma viagem,
    // porque é ele quem faz todas as verificações antes.
    void marcarComoConfirmada() {
        situacao = SituacaoViagem.CONFIRMADA;
    }

    public void iniciar() {
        if (situacao != SituacaoViagem.CONFIRMADA) {
            throw new RegraNegocioException("Só é possível iniciar uma viagem confirmada.");
        }
        veiculo.setSituacao(SituacaoVeiculo.EM_VIAGEM);
        situacao = SituacaoViagem.EM_ANDAMENTO;
    }

    public void concluir() {
        if (situacao != SituacaoViagem.EM_ANDAMENTO) {
            throw new RegraNegocioException("Só é possível concluir uma viagem em andamento.");
        }
        veiculo.setSituacao(SituacaoVeiculo.DISPONIVEL);
        situacao = SituacaoViagem.CONCLUIDA;
    }

    private void exigirPlanejada() {
        if (situacao != SituacaoViagem.PLANEJADA) {
            throw new RegraNegocioException("Uma viagem já confirmada não pode ser alterada.");
        }
    }

    @Override
    public String toString() {
        return Formatador.data(data) + " | " + turno + " | " + rota + " | " + veiculo
                + " | Motorista: " + motorista
                + " | " + estudantes.size() + "/" + veiculo.getCapacidade() + " passageiros"
                + " | custo " + Formatador.moeda(calcularCusto());
    }
}
