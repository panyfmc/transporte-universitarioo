import java.util.ArrayList;

public class Estudante {
    // regra do PDF: 3 ou mais pagamentos em atraso = inadimplente
    public static final int LIMITE_ATRASOS = 3;

    private String matricula;
    private String nome;
    private String instituicao;
    private Rota rota;
    private Turno turnoIda;
    private Turno turnoVolta;
    // COMPOSIÇÃO: os pagamentos pertencem ao estudante e só existem por causa dele
    private final ArrayList<Pagamento> pagamentos = new ArrayList<>();

    public Estudante(String matricula, String nome, String instituicao,
                     Rota rota, Turno turnoIda, Turno turnoVolta) {
        setMatricula(matricula);
        setNome(nome);
        setInstituicao(instituicao);
        setRota(rota);
        setTurnoIda(turnoIda);
        setTurnoVolta(turnoVolta);
    }

    //sets da classe
    public void setMatricula(String matricula) {
        this.matricula = Validador.exigirTexto(matricula, "matrícula");
    }

    public void setNome(String nome) {
        this.nome = Validador.exigirTexto(nome, "nome");
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = Validador.exigirTexto(instituicao, "instituição");
    }

    public void setRota(Rota rota) {
        Validador.exigirNaoNulo(rota, "rota");
        this.rota = rota;
    }

    // antes se chamava setIda; renomeado para ficar igual ao getTurnoIda
    public void setTurnoIda(Turno turnoIda) {
        Validador.exigirNaoNulo(turnoIda, "turno de ida");
        this.turnoIda = turnoIda;
    }

    public void setTurnoVolta(Turno turnoVolta) {
        Validador.exigirNaoNulo(turnoVolta, "turno de volta");
        this.turnoVolta = turnoVolta;
    }

    //gets da classe
    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public Rota getRota() {
        return rota;
    }

    public Turno getTurnoIda() {
        return turnoIda;
    }

    public Turno getTurnoVolta() {
        return turnoVolta;
    }

    // ----- pagamentos e inadimplência -----

    public void adicionarPagamento(Pagamento pagamento) {
        Validador.exigirNaoNulo(pagamento, "pagamento");
        pagamentos.add(pagamento);
    }

    /** Devolve uma CÓPIA da lista: quem chama não consegue alterar a lista interna do estudante. */
    public ArrayList<Pagamento> getPagamentos() {
        return new ArrayList<>(pagamentos);
    }

    public int contarPagamentosAtrasados() {
        int atrasados = 0;
        for (Pagamento pagamento : pagamentos) {
            if (pagamento.estaAtrasado()) {
                atrasados++;
            }
        }
        return atrasados;
    }

    public boolean isInadimplente() {
        return contarPagamentosAtrasados() >= LIMITE_ATRASOS;
    }

    @Override
    public String toString() {
        return nome + " (" + matricula + ")";
    }
}
