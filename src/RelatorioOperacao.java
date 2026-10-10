import java.time.LocalDate;

/** Só imprime: toda a conta fica na AssociacaoTransporte (separação entre lógica e exibição). */
public class RelatorioOperacao {
    private final AssociacaoTransporte associacao;

    public RelatorioOperacao(AssociacaoTransporte associacao) {
        Validador.exigirNaoNulo(associacao, "associação");
        this.associacao = associacao;
    }

    public void imprimir(LocalDate data) {
        System.out.println("==================================================");
        System.out.println(" RELATÓRIO DA OPERAÇÃO - " + associacao.getNome());
        System.out.println("==================================================");

        System.out.println("\n-- Estudantes por rota --");
        for (Rota rota : associacao.getRotas()) {
            System.out.println(rota + ": " + associacao.contarEstudantes(rota) + " ("
                    + associacao.contarAdimplentes(rota) + " adimplentes, "
                    + associacao.contarInadimplentes(rota) + " inadimplentes)");
        }

        System.out.println("\n-- Estudantes por instituição --");
        for (String instituicao : associacao.getInstituicoes()) {
            System.out.println(instituicao + ": " + associacao.contarEstudantes(instituicao));
        }

        System.out.println("\n-- Inadimplentes (" + associacao.contarInadimplentes() + ") --");
        for (Estudante estudante : associacao.getInadimplentes()) {
            System.out.println(estudante + " - " + estudante.getRota() + " - "
                    + estudante.contarPagamentosAtrasados() + " pagamentos em atraso");
        }

        System.out.println("\n-- Veículos disponíveis (" + associacao.getVeiculosDisponiveis().size() + ") --");
        for (Veiculo veiculo : associacao.getVeiculosDisponiveis()) {
            System.out.println(veiculo);
        }

        System.out.println("\n-- Viagens programadas em " + Formatador.data(data) + " --");
        for (Viagem viagem : associacao.getViagensDoDia(data)) {
            System.out.println(viagem + " | vagas livres: " + viagem.getVagasDisponiveis());
        }

        System.out.println("\n-- Custo estimado previsto (viagens do dia) --");
        System.out.println(Formatador.moeda(associacao.calcularCustoPrevisto(data)));

        System.out.println("\n-- Receita mensal prevista (somente adimplentes) --");
        for (Rota rota : associacao.getRotas()) {
            int adimplentes = associacao.contarAdimplentes(rota);
            System.out.println(adimplentes + " estudante(s) de " + rota + ": " + adimplentes + " × "
                    + Formatador.moeda(rota.getMensalidade()) + " = "
                    + Formatador.moeda(adimplentes * rota.getMensalidade()));
        }
        System.out.println("TOTAL PREVISTO: " + Formatador.moeda(associacao.calcularReceitaMensalPrevista()));
        System.out.println("Fora da previsão (inadimplentes): " + Formatador.moeda(associacao.calcularValorInadimplente()));
        System.out.println();
    }
}
