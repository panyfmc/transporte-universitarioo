import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Monta as viagens automaticamente: escolhe veículo (capacidade x custo),
 * escolhe motorista disponível e agrupa estudantes da mesma instituição.
 */
public class AlocacaoService {
    private final ViagemService viagemService;

    public AlocacaoService(ViagemService viagemService) {
        Validador.exigirNaoNulo(viagemService, "viagemService");
        this.viagemService = viagemService;
    }

    /**
     * Escolhe o veículo mais barato (custo por km) que comporta todos os passageiros.
     * Se nenhum comportar todos, escolhe o maior disponível (o restante vai em outra viagem).
     * Devolve null se não houver nenhum veículo disponível.
     */
    public Veiculo escolherVeiculo(int passageiros, LocalDate data, Turno turno, ArrayList<Veiculo> frota) {
        Veiculo maisBarato = null;
        Veiculo maior = null;
        for (Veiculo veiculo : frota) {
            if (!viagemService.veiculoDisponivel(veiculo, data, turno)) {
                continue;
            }
            if (maior == null || veiculo.getCapacidade() > maior.getCapacidade()) {
                maior = veiculo;
            }
            if (veiculo.getCapacidade() >= passageiros
                    && (maisBarato == null || veiculo.getCustoPorKm() < maisBarato.getCustoPorKm())) {
                maisBarato = veiculo;
            }
        }
        return (maisBarato != null) ? maisBarato : maior;
    }

    /** Primeiro motorista que passa em todas as regras de disponibilidade e de turnos. */
    public Motorista escolherMotorista(LocalDate data, Turno turno, ArrayList<Motorista> motoristas) {
        for (Motorista motorista : motoristas) {
            if (viagemService.motoristaDisponivel(motorista, data, turno)) {
                return motorista;
            }
        }
        return null;
    }

    /**
     * Cria e confirma as viagens de uma rota em um turno.
     * Inadimplentes ficam de fora; estudantes da mesma instituição ficam juntos.
     */
    public ArrayList<Viagem> alocarViagens(LocalDate data, Turno turno, Rota rota,
                                           ArrayList<Estudante> estudantes,
                                           ArrayList<Veiculo> frota,
                                           ArrayList<Motorista> motoristas) {
        ArrayList<Estudante> candidatos = selecionarCandidatos(data, turno, rota, estudantes);
        ordenarPorInstituicao(candidatos);

        ArrayList<Viagem> criadas = new ArrayList<>();
        int proximo = 0; // posição do próximo estudante ainda sem transporte

        while (proximo < candidatos.size()) {
            int restantes = candidatos.size() - proximo;
            Veiculo veiculo = escolherVeiculo(restantes, data, turno, frota);
            Motorista motorista = escolherMotorista(data, turno, motoristas);
            if (veiculo == null || motorista == null) {
                throw new RegraNegocioException("Sem veículo ou motorista disponível para " + restantes
                        + " estudante(s) da rota " + rota + " no turno da " + turno.toString().toLowerCase() + ".");
            }

            Viagem viagem = new Viagem(data, turno, rota, veiculo, motorista);
            int quantidade = Math.min(restantes, veiculo.getCapacidade());
            for (int i = 0; i < quantidade; i++) {
                viagem.adicionarEstudante(candidatos.get(proximo + i));
            }
            viagemService.confirmar(viagem);
            criadas.add(viagem);
            proximo += quantidade;
        }
        return criadas;
    }

    // estudantes da rota que viajam nesse turno, adimplentes e ainda sem viagem nele
    private ArrayList<Estudante> selecionarCandidatos(LocalDate data, Turno turno, Rota rota,
                                                      ArrayList<Estudante> estudantes) {
        ArrayList<Estudante> candidatos = new ArrayList<>();
        for (Estudante estudante : estudantes) {
            boolean mesmaRota = estudante.getRota().mesmaRota(rota);
            boolean viajaNoTurno = estudante.getTurnoIda() == turno || estudante.getTurnoVolta() == turno;
            if (mesmaRota && viajaNoTurno && !estudante.isInadimplente()
                    && !viagemService.viajaNoTurno(estudante, data, turno)) {
                candidatos.add(estudante);
            }
        }
        return candidatos;
    }

    // ordenação por inserção, pela instituição: deixa alunos da mesma instituição lado a lado
    private void ordenarPorInstituicao(ArrayList<Estudante> lista) {
        for (int i = 1; i < lista.size(); i++) {
            Estudante atual = lista.get(i);
            int j = i - 1;
            while (j >= 0 && lista.get(j).getInstituicao().compareToIgnoreCase(atual.getInstituicao()) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, atual);
        }
    }
}
