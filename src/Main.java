import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    private static final LocalDate DATA = LocalDate.of(2026, 10, 12); // uma segunda-feira

    public static void main(String[] args) {
        System.out.println("##### PARTE 1 - TESTES DAS REGRAS (situações inválidas) #####\n");
        testeOnibusLotado();
        testeVanLotada();
        testeMotoristaViagensSimultaneas();
        testeMotoristaManhaENoite();
        testeMotoristaTresTurnos();
        testeEstudanteInadimplente();
        testeSemTransporteDeRetorno();
        testeEstudanteDuplicado();
        testeVeiculoViagensSimultaneas();
        testeTrocaDeHorario();

        System.out.println("\n##### PARTE 2 - SIMULAÇÃO DE UM DIA COMPLETO #####\n");
        simularDiaCompleto();
    }

    // =====================================================================
    // PARTE 1 - cada teste usa objetos novos, para um não interferir no outro
    // =====================================================================

    private static void testeOnibusLotado() {
        String teste = "41 estudantes em um ônibus";
        Rota rota = new Rota("Lagarto", 60, 600);
        Viagem viagem = new Viagem(DATA, Turno.MANHA, rota,
                new Veiculo("ONI-0001", 1, TipoVeiculo.ONIBUS), novoMotorista("111", "Carlos"));
        try {
            for (int i = 1; i <= 41; i++) {
                viagem.adicionarEstudante(novoEstudante("E" + i, rota, Turno.MANHA, Turno.TARDE));
            }
            falhou(teste);
        } catch (RegraNegocioException e) {
            bloqueado(teste, e);
        }
    }

    private static void testeVanLotada() {
        String teste = "21 estudantes em uma van";
        Rota rota = new Rota("Lagarto", 60, 600);
        Viagem viagem = new Viagem(DATA, Turno.MANHA, rota,
                new Veiculo("VAN-0001", 1, TipoVeiculo.VAN), novoMotorista("111", "Carlos"));
        try {
            for (int i = 1; i <= 21; i++) {
                viagem.adicionarEstudante(novoEstudante("E" + i, rota, Turno.MANHA, Turno.TARDE));
            }
            falhou(teste);
        } catch (RegraNegocioException e) {
            bloqueado(teste, e);
        }
    }

    private static void testeMotoristaViagensSimultaneas() {
        String teste = "mesmo motorista em duas viagens simultâneas";
        Rota rota = new Rota("Lagarto", 60, 600);
        ViagemService service = new ViagemService();
        Motorista motorista = novoMotorista("111", "Carlos");
        Viagem v1 = new Viagem(DATA, Turno.MANHA, rota, new Veiculo("VAN-0001", 1, TipoVeiculo.VAN), motorista);
        Viagem v2 = new Viagem(DATA, Turno.MANHA, rota, new Veiculo("VAN-0002", 2, TipoVeiculo.VAN), motorista);
        try {
            service.confirmar(v1);
            service.confirmar(v2);
            falhou(teste);
        } catch (RegraNegocioException e) {
            bloqueado(teste, e);
        }
    }

    private static void testeMotoristaManhaENoite() {
        String teste = "motorista pela manhã e pela noite no mesmo dia";
        Rota rota = new Rota("Lagarto", 60, 600);
        ViagemService service = new ViagemService();
        Motorista motorista = novoMotorista("111", "Carlos");
        Veiculo van = new Veiculo("VAN-0001", 1, TipoVeiculo.VAN);
        try {
            service.confirmar(new Viagem(DATA, Turno.MANHA, rota, van, motorista));
            service.confirmar(new Viagem(DATA, Turno.NOITE, rota, van, motorista));
            falhou(teste);
        } catch (RegraNegocioException e) {
            bloqueado(teste, e);
        }
    }

    private static void testeMotoristaTresTurnos() {
        String teste = "motorista nos três turnos";
        Rota rota = new Rota("Lagarto", 60, 600);
        ViagemService service = new ViagemService();
        Motorista motorista = novoMotorista("111", "Carlos");
        Veiculo van = new Veiculo("VAN-0001", 1, TipoVeiculo.VAN);
        try {
            service.confirmar(new Viagem(DATA, Turno.MANHA, rota, van, motorista));
            service.confirmar(new Viagem(DATA, Turno.TARDE, rota, van, motorista)); // manhã + tarde: permitido
            service.confirmar(new Viagem(DATA, Turno.NOITE, rota, van, motorista));
            falhou(teste);
        } catch (RegraNegocioException e) {
            bloqueado(teste, e);
        }
    }

    private static void testeEstudanteInadimplente() {
        String teste = "estudante inadimplente viajando";
        Rota rota = new Rota("Lagarto", 60, 600);
        Viagem viagem = new Viagem(DATA, Turno.MANHA, rota,
                new Veiculo("VAN-0001", 1, TipoVeiculo.VAN), novoMotorista("111", "Carlos"));

        // 2 atrasos ainda NÃO é inadimplente (a regra é 3 ou mais)
        Estudante comDoisAtrasos = novoEstudante("A1", rota, Turno.MANHA, Turno.TARDE);
        adicionarAtrasos(comDoisAtrasos, 2);
        viagem.adicionarEstudante(comDoisAtrasos);
        System.out.println("[OK] " + comDoisAtrasos + " com 2 atrasos ainda pode viajar (limite é 3).");

        Estudante inadimplente = novoEstudante("A2", rota, Turno.MANHA, Turno.TARDE);
        adicionarAtrasos(inadimplente, 3);
        try {
            viagem.adicionarEstudante(inadimplente);
            falhou(teste);
        } catch (RegraNegocioException e) {
            bloqueado(teste, e);
        }
    }

    private static void testeSemTransporteDeRetorno() {
        String teste = "estudante que foi e ficou sem transporte de retorno";
        Rota rota = new Rota("Lagarto", 60, 600);
        ViagemService service = new ViagemService();
        Estudante aluno = novoEstudante("R1", rota, Turno.MANHA, Turno.TARDE);

        Viagem ida = new Viagem(DATA, Turno.MANHA, rota,
                new Veiculo("VAN-0001", 1, TipoVeiculo.VAN), novoMotorista("111", "Carlos"));
        ida.adicionarEstudante(aluno);
        service.confirmar(ida); // só a ida foi criada; não existe viagem da tarde

        ArrayList<Estudante> lista = new ArrayList<>();
        lista.add(aluno);
        ArrayList<Estudante> semRetorno = service.verificarRetornos(DATA, lista);
        if (semRetorno.contains(aluno)) {
            System.out.println("[OK] Bloqueado/detectado: " + teste + " -> " + aluno);
        } else {
            falhou(teste);
        }
    }

    private static void testeEstudanteDuplicado() {
        String teste = "mesmo estudante duas vezes na mesma viagem";
        Rota rota = new Rota("Lagarto", 60, 600);
        Viagem viagem = new Viagem(DATA, Turno.MANHA, rota,
                new Veiculo("VAN-0001", 1, TipoVeiculo.VAN), novoMotorista("111", "Carlos"));
        Estudante aluno = novoEstudante("D1", rota, Turno.MANHA, Turno.TARDE);
        try {
            viagem.adicionarEstudante(aluno);
            viagem.adicionarEstudante(aluno);
            falhou(teste);
        } catch (RegraNegocioException e) {
            bloqueado(teste, e);
        }
    }

    private static void testeVeiculoViagensSimultaneas() {
        String teste = "mesmo veículo em duas viagens simultâneas";
        Rota rota = new Rota("Lagarto", 60, 600);
        ViagemService service = new ViagemService();
        Veiculo van = new Veiculo("VAN-0001", 1, TipoVeiculo.VAN);
        try {
            service.confirmar(new Viagem(DATA, Turno.TARDE, rota, van, novoMotorista("111", "Carlos")));
            service.confirmar(new Viagem(DATA, Turno.TARDE, rota, van, novoMotorista("222", "Marcos")));
            falhou(teste);
        } catch (RegraNegocioException e) {
            bloqueado(teste, e);
        }
    }

    private static void testeTrocaDeHorario() {
        String teste = "troca de horário sem vaga";
        Rota rota = new Rota("Lagarto", 60, 600);
        ViagemService service = new ViagemService();
        Estudante aluno = novoEstudante("T0", rota, Turno.MANHA, Turno.NOITE);

        Viagem manha = new Viagem(DATA, Turno.MANHA, rota,
                new Veiculo("VAN-0001", 1, TipoVeiculo.VAN), novoMotorista("111", "Carlos"));
        manha.adicionarEstudante(aluno);
        service.confirmar(manha);

        Viagem tardeCheia = new Viagem(DATA, Turno.TARDE, rota,
                new Veiculo("VAN-0002", 2, TipoVeiculo.VAN), novoMotorista("222", "Marcos"));
        for (int i = 1; i <= 20; i++) {
            tardeCheia.adicionarEstudante(novoEstudante("T" + i, rota, Turno.TARDE, Turno.NOITE));
        }
        service.confirmar(tardeCheia);

        try {
            service.trocarViagem(aluno, manha, tardeCheia);
            falhou(teste);
        } catch (RegraNegocioException e) {
            bloqueado(teste, e);
        }

        // contraprova: com vaga a troca funciona
        Viagem tardeComVaga = new Viagem(DATA, Turno.TARDE, rota,
                new Veiculo("ONI-0001", 3, TipoVeiculo.ONIBUS), novoMotorista("333", "Paulo"));
        service.confirmar(tardeComVaga);
        service.trocarViagem(aluno, manha, tardeComVaga);
        System.out.println("[OK] Troca com vaga funcionou: turno de ida de " + aluno + " agora é " + aluno.getTurnoIda() + ".");
    }

    // =====================================================================
    // PARTE 2 - um dia completo
    // =====================================================================

    private static void simularDiaCompleto() {
        AssociacaoTransporte associacao =
                new AssociacaoTransporte("Associação de Transporte Universitário de Tobias Barreto");

        // distâncias aproximadas (ajustar se o professor passar outras)
        Rota lagarto = new Rota("Lagarto", 60, 600);
        Rota paripiranga = new Rota("Paripiranga", 45, 700);
        Rota aracaju = new Rota("Aracaju", 120, 900);
        associacao.cadastrarRota(lagarto);
        associacao.cadastrarRota(paripiranga);
        associacao.cadastrarRota(aracaju);

        for (int i = 1; i <= 3; i++) {
            associacao.cadastrarVeiculo(new Veiculo("ONI-000" + i, i, TipoVeiculo.ONIBUS));
            associacao.cadastrarVeiculo(new Veiculo("VAN-000" + i, 10 + i, TipoVeiculo.VAN));
        }

        String[] nomes = {"Carlos", "Marcos", "Paulo", "Renato", "Jorge", "Sérgio"};
        for (int i = 0; i < nomes.length; i++) {
            associacao.cadastrarMotorista(new Motorista("000.000.000-0" + i, nomes[i],
                    "CNH10" + i, "(79) 99999-000" + i));
        }

        // Lagarto: ida de manhã, volta à tarde
        cadastrarGrupo(associacao, "LAG", 25, lagarto, Turno.MANHA, Turno.TARDE,
                new String[]{"IFS Lagarto", "UFS Lagarto", "AGES"}, 0);
        cadastrarGrupo(associacao, "LAGX", 1, lagarto, Turno.MANHA, Turno.TARDE,
                new String[]{"UFS Lagarto"}, 3); // inadimplente
        // Paripiranga: ida de manhã, volta à noite
        cadastrarGrupo(associacao, "PAR", 15, paripiranga, Turno.MANHA, Turno.NOITE,
                new String[]{"AGES", "IFS Lagarto"}, 0);
        cadastrarGrupo(associacao, "PARX", 1, paripiranga, Turno.MANHA, Turno.NOITE,
                new String[]{"AGES"}, 2); // 2 atrasos: ainda adimplente
        // Aracaju: ida à tarde, volta à noite
        cadastrarGrupo(associacao, "ARA", 45, aracaju, Turno.TARDE, Turno.NOITE,
                new String[]{"UNIT", "Pio X", "UFS São Cristóvão"}, 0);
        cadastrarGrupo(associacao, "ARAX", 2, aracaju, Turno.TARDE, Turno.NOITE,
                new String[]{"UNIT"}, 4); // inadimplentes

        System.out.println("Estudantes cadastrados: " + associacao.contarEstudantes()
                + " | Inadimplentes: " + associacao.contarInadimplentes() + "\n");

        // Planejamento: para cada turno e rota, o sistema escolhe veículo e motorista
        System.out.println("--- Viagens planejadas e confirmadas ---");
        for (Turno turno : Turno.values()) {
            for (Rota rota : associacao.getRotas()) {
                try {
                    ArrayList<Viagem> criadas = associacao.alocarViagens(DATA, turno, rota);
                    for (Viagem viagem : criadas) {
                        System.out.println(viagem);
                    }
                } catch (RegraNegocioException e) {
                    System.out.println("[ATENÇÃO] " + e.getMessage());
                }
            }
        }

        // Execução do dia
        System.out.println("\n--- Execução do dia ---");
        for (Viagem viagem : associacao.getViagensDoDia(DATA)) {
            viagem.iniciar();
            System.out.println("Saiu    -> " + viagem.getTurno() + " | " + viagem.getRota() + " | "
                    + viagem.getVeiculo() + " [" + viagem.getVeiculo().getSituacao() + "]");
            viagem.concluir();
            System.out.println("Chegou  -> " + viagem.getTurno() + " | " + viagem.getRota() + " | "
                    + viagem.getVeiculo() + " [" + viagem.getVeiculo().getSituacao() + "]");
        }

        // Conferência da regra do retorno
        System.out.println("\n--- Conferência de retornos ---");
        ArrayList<Estudante> semRetorno = associacao.verificarRetornos(DATA);
        if (semRetorno.isEmpty()) {
            System.out.println("Todos os estudantes que foram têm transporte de retorno.");
        } else {
            for (Estudante estudante : semRetorno) {
                System.out.println("[ATENÇÃO] Sem retorno: " + estudante);
            }
        }
        System.out.println();

        new RelatorioOperacao(associacao).imprimir(DATA);
    }

    // =====================================================================
    // Métodos auxiliares
    // =====================================================================

    private static void cadastrarGrupo(AssociacaoTransporte associacao, String prefixo, int quantidade,
                                       Rota rota, Turno ida, Turno volta, String[] instituicoes, int atrasos) {
        for (int i = 1; i <= quantidade; i++) {
            Estudante estudante = new Estudante(prefixo + String.format("%02d", i),
                    "Aluno " + rota + " " + prefixo + i,
                    instituicoes[(i - 1) % instituicoes.length], rota, ida, volta);
            if (atrasos == 0) {
                estudante.adicionarPagamento(new Pagamento("09/2026", rota.getMensalidade(), SituacaoPagamento.PAGO));
            } else {
                adicionarAtrasos(estudante, atrasos);
            }
            associacao.cadastrarEstudante(estudante);
        }
    }

    private static void adicionarAtrasos(Estudante estudante, int quantidade) {
        for (int i = 1; i <= quantidade; i++) {
            estudante.adicionarPagamento(new Pagamento("0" + i + "/2026",
                    estudante.getRota().getMensalidade(), SituacaoPagamento.ATRASADO));
        }
    }

    private static Estudante novoEstudante(String matricula, Rota rota, Turno ida, Turno volta) {
        return new Estudante(matricula, "Aluno " + matricula, "IFS Lagarto", rota, ida, volta);
    }

    private static Motorista novoMotorista(String cpf, String nome) {
        return new Motorista(cpf, nome, "CNH" + cpf, "(79) 90000-0000");
    }

    private static void bloqueado(String teste, RegraNegocioException e) {
        System.out.println("[OK] Bloqueado: " + teste);
        System.out.println("     Motivo: " + e.getMessage());
    }

    private static void falhou(String teste) {
        System.out.println("[FALHOU] A regra NÃO foi aplicada: " + teste);
    }
}
