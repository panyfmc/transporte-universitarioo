import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Classe central: guarda rotas, frota, motoristas e estudantes e oferece as
 * operações de cadastro, consulta, receita e alocação.
 */
public class AssociacaoTransporte {
    private final String nome;
    private final ArrayList<Rota> rotas = new ArrayList<>();
    private final ArrayList<Veiculo> frota = new ArrayList<>();
    private final ArrayList<Motorista> motoristas = new ArrayList<>();
    private final ArrayList<Estudante> estudantes = new ArrayList<>();
    private final ViagemService viagemService = new ViagemService();
    private final AlocacaoService alocacaoService = new AlocacaoService(viagemService);

    public AssociacaoTransporte(String nome) {
        this.nome = Validador.exigirTexto(nome, "nome");
    }

    // ---------------- cadastros ----------------

    public void cadastrarRota(Rota rota) {
        Validador.exigirNaoNulo(rota, "rota");
        if (buscarRota(rota.getDestino()) != null) {
            throw new RegraNegocioException("Já existe a rota " + rota + ".");
        }
        rotas.add(rota);
    }

    public void cadastrarVeiculo(Veiculo veiculo) {
        Validador.exigirNaoNulo(veiculo, "veículo");
        for (Veiculo existente : frota) {
            if (existente.getPlaca().equalsIgnoreCase(veiculo.getPlaca())) {
                throw new RegraNegocioException("Já existe um veículo com a placa " + veiculo.getPlaca() + ".");
            }
        }
        frota.add(veiculo);
    }

    public void cadastrarMotorista(Motorista motorista) {
        Validador.exigirNaoNulo(motorista, "motorista");
        for (Motorista existente : motoristas) {
            if (existente.getCpf().equals(motorista.getCpf())) {
                throw new RegraNegocioException("Já existe um motorista com o CPF " + motorista.getCpf() + ".");
            }
        }
        motoristas.add(motorista);
    }

    public void cadastrarEstudante(Estudante estudante) {
        Validador.exigirNaoNulo(estudante, "estudante");
        if (buscarEstudante(estudante.getMatricula()) != null) {
            throw new RegraNegocioException("Já existe um estudante com a matrícula " + estudante.getMatricula() + ".");
        }
        if (buscarRota(estudante.getRota().getDestino()) == null) {
            throw new RegraNegocioException("A rota " + estudante.getRota() + " não está cadastrada.");
        }
        if (estudante.getTurnoIda() == estudante.getTurnoVolta()) {
            throw new RegraNegocioException("Ida e retorno de " + estudante + " não podem ser no mesmo turno.");
        }
        estudantes.add(estudante);
    }

    // ---------------- buscas ----------------

    public Rota buscarRota(String destino) {
        for (Rota rota : rotas) {
            if (rota.getDestino().equalsIgnoreCase(destino)) {
                return rota;
            }
        }
        return null;
    }

    public Estudante buscarEstudante(String matricula) {
        for (Estudante estudante : estudantes) {
            if (estudante.getMatricula().equals(matricula)) {
                return estudante;
            }
        }
        return null;
    }

    // ---------------- contagens (SOBRECARGA de contarEstudantes) ----------------

    public int contarEstudantes() {
        return estudantes.size();
    }

    public int contarEstudantes(Rota rota) {
        int total = 0;
        for (Estudante estudante : estudantes) {
            if (estudante.getRota().mesmaRota(rota)) {
                total++;
            }
        }
        return total;
    }

    public int contarEstudantes(String instituicao) {
        int total = 0;
        for (Estudante estudante : estudantes) {
            if (estudante.getInstituicao().equalsIgnoreCase(instituicao)) {
                total++;
            }
        }
        return total;
    }

    public int contarInadimplentes() {
        return getInadimplentes().size();
    }

    public int contarAdimplentes(Rota rota) {
        int total = 0;
        for (Estudante estudante : estudantes) {
            if (estudante.getRota().mesmaRota(rota) && !estudante.isInadimplente()) {
                total++;
            }
        }
        return total;
    }

    public int contarInadimplentes(Rota rota) {
        return contarEstudantes(rota) - contarAdimplentes(rota);
    }

    public ArrayList<Estudante> getInadimplentes() {
        ArrayList<Estudante> inadimplentes = new ArrayList<>();
        for (Estudante estudante : estudantes) {
            if (estudante.isInadimplente()) {
                inadimplentes.add(estudante);
            }
        }
        return inadimplentes;
    }

    /** Lista de instituições, sem repetição. */
    public ArrayList<String> getInstituicoes() {
        ArrayList<String> instituicoes = new ArrayList<>();
        for (Estudante estudante : estudantes) {
            boolean jaExiste = false;
            for (String instituicao : instituicoes) {
                if (instituicao.equalsIgnoreCase(estudante.getInstituicao())) {
                    jaExiste = true;
                }
            }
            if (!jaExiste) {
                instituicoes.add(estudante.getInstituicao());
            }
        }
        return instituicoes;
    }

    public ArrayList<Veiculo> getVeiculosDisponiveis() {
        ArrayList<Veiculo> disponiveis = new ArrayList<>();
        for (Veiculo veiculo : frota) {
            if (veiculo.estaDisponivel()) {
                disponiveis.add(veiculo);
            }
        }
        return disponiveis;
    }

    // ---------------- financeiro ----------------

    /** Receita mensal prevista: só estudantes adimplentes, cada um pagando a mensalidade da sua rota. */
    public double calcularReceitaMensalPrevista() {
        double total = 0;
        for (Estudante estudante : estudantes) {
            if (!estudante.isInadimplente()) {
                total += estudante.getRota().getMensalidade();
            }
        }
        return total;
    }

    /** Valor mensal que NÃO está sendo contado por causa dos inadimplentes (aparece separado no relatório). */
    public double calcularValorInadimplente() {
        double total = 0;
        for (Estudante estudante : getInadimplentes()) {
            total += estudante.getRota().getMensalidade();
        }
        return total;
    }

    public double calcularCustoPrevisto(LocalDate data) {
        double total = 0;
        for (Viagem viagem : viagemService.getViagensDoDia(data)) {
            total += viagem.calcularCusto();
        }
        return total;
    }

    // ---------------- viagens (delegam para os services) ----------------

    public ArrayList<Viagem> alocarViagens(LocalDate data, Turno turno, Rota rota) {
        return alocacaoService.alocarViagens(data, turno, rota, estudantes, frota, motoristas);
    }

    public ArrayList<Estudante> verificarRetornos(LocalDate data) {
        return viagemService.verificarRetornos(data, estudantes);
    }

    public ArrayList<Viagem> getViagensDoDia(LocalDate data) {
        return viagemService.getViagensDoDia(data);
    }

    // ---------------- getters (cópias das listas) ----------------

    public String getNome() {
        return nome;
    }

    public ArrayList<Rota> getRotas() {
        return new ArrayList<>(rotas);
    }

    public ArrayList<Veiculo> getFrota() {
        return new ArrayList<>(frota);
    }

    public ArrayList<Motorista> getMotoristas() {
        return new ArrayList<>(motoristas);
    }

    public ArrayList<Estudante> getEstudantes() {
        return new ArrayList<>(estudantes);
    }
}
