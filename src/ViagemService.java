import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Guarda as viagens confirmadas e aplica as regras que dependem de VÁRIAS viagens
 * (ex.: o motorista já está em outra viagem nesse horário?).
 *
 * Os métodos "motivo..." devolvem uma mensagem explicando o problema, ou null
 * quando está tudo certo. Assim o mesmo código serve para validar (confirmar)
 * e para perguntar "está disponível?" (alocação automática) sem usar try/catch.
 */
public class ViagemService {
    private final ArrayList<Viagem> viagens = new ArrayList<>(); // só viagens confirmadas

    /** Confirma a viagem somente se TODAS as condições do enunciado forem atendidas. */
    public void confirmar(Viagem viagem) {
        Validador.exigirNaoNulo(viagem, "viagem");
        if (viagem.getSituacao() != SituacaoViagem.PLANEJADA) {
            throw new RegraNegocioException("Esta viagem já foi confirmada.");
        }

        // cada verificação só roda se a anterior passou (motivo == null)
        String motivo = motivoVeiculoIndisponivel(viagem.getVeiculo(), viagem.getData(), viagem.getTurno());
        if (motivo == null) {
            motivo = motivoMotoristaIndisponivel(viagem.getMotorista(), viagem.getData(), viagem.getTurno());
        }
        if (motivo == null) {
            motivo = motivoEstudanteInvalido(viagem);
        }
        if (motivo == null && viagem.getQuantidadeEstudantes() > viagem.getVeiculo().getCapacidade()) {
            motivo = "A capacidade do veículo foi excedida.";
        }
        if (motivo != null) {
            throw new RegraNegocioException(motivo);
        }

        viagem.marcarComoConfirmada();
        viagens.add(viagem);
    }

    // ---------------- veículo ----------------

    public String motivoVeiculoIndisponivel(Veiculo veiculo, LocalDate data, Turno turno) {
        if (!veiculo.estaDisponivel()) {
            return "O veículo " + veiculo + " não está disponível (situação: " + veiculo.getSituacao() + ").";
        }
        for (Viagem outra : viagens) {
            boolean mesmoVeiculo = outra.getVeiculo().getPlaca().equals(veiculo.getPlaca());
            if (mesmoVeiculo && outra.getData().equals(data) && outra.getTurno() == turno) {
                return "O veículo " + veiculo + " já está em outra viagem em "
                        + Formatador.data(data) + " no turno da " + turno.toString().toLowerCase() + ".";
            }
        }
        return null;
    }

    public boolean veiculoDisponivel(Veiculo veiculo, LocalDate data, Turno turno) {
        return motivoVeiculoIndisponivel(veiculo, data, turno) == null;
    }

    // ---------------- motorista ----------------

    public String motivoMotoristaIndisponivel(Motorista motorista, LocalDate data, Turno turno) {
        ArrayList<Turno> turnosDoDia = turnosDoMotoristaNoDia(motorista, data);

        // 1) não pode ter duas viagens no mesmo horário
        if (turnosDoDia.contains(turno)) {
            return "O motorista " + motorista + " já está escalado em outra viagem em "
                    + Formatador.data(data) + " no turno da " + turno.toString().toLowerCase() + ".";
        }

        // 2) a escala do dia, COM a nova viagem, precisa respeitar as regras de turnos
        ArrayList<Turno> escala = new ArrayList<>(turnosDoDia);
        escala.add(turno);
        boolean manha = escala.contains(Turno.MANHA);
        boolean tarde = escala.contains(Turno.TARDE);
        boolean noite = escala.contains(Turno.NOITE);

        if (manha && tarde && noite) {
            return "O motorista " + motorista + " não pode trabalhar nos três turnos do mesmo dia.";
        }
        if (manha && noite) {
            return "O motorista " + motorista + " não pode trabalhar de manhã e à noite no mesmo dia.";
        }
        return null;
    }

    public boolean motoristaDisponivel(Motorista motorista, LocalDate data, Turno turno) {
        return motivoMotoristaIndisponivel(motorista, data, turno) == null;
    }

    private ArrayList<Turno> turnosDoMotoristaNoDia(Motorista motorista, LocalDate data) {
        ArrayList<Turno> turnos = new ArrayList<>();
        for (Viagem viagem : viagens) {
            boolean mesmoMotorista = viagem.getMotorista().getCpf().equals(motorista.getCpf());
            if (mesmoMotorista && viagem.getData().equals(data)) {
                turnos.add(viagem.getTurno());
            }
        }
        return turnos;
    }

    // ---------------- estudantes ----------------

    // Revalida na confirmação: o estudante pode ter ficado inadimplente DEPOIS de ser incluído.
    private String motivoEstudanteInvalido(Viagem viagem) {
        for (Estudante estudante : viagem.getEstudantes()) {
            if (estudante.isInadimplente()) {
                return "O estudante " + estudante + " está inadimplente e não pode viajar.";
            }
            boolean turnoCombina = estudante.getTurnoIda() == viagem.getTurno()
                    || estudante.getTurnoVolta() == viagem.getTurno();
            if (!turnoCombina) {
                return "A viagem da " + viagem.getTurno().toString().toLowerCase() + " não corresponde ao turno de ida ("
                        + estudante.getTurnoIda() + ") nem ao de volta (" + estudante.getTurnoVolta()
                        + ") de " + estudante + ".";
            }
        }
        return null;
    }

    public boolean viajaNoTurno(Estudante estudante, LocalDate data, Turno turno) {
        for (Viagem viagem : viagens) {
            if (viagem.getData().equals(data) && viagem.getTurno() == turno
                    && viagem.possuiEstudante(estudante.getMatricula())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Regra: quem tem viagem de ida no dia precisa ter viagem de retorno.
     * Devolve os estudantes que ficariam SEM transporte de volta.
     */
    public ArrayList<Estudante> verificarRetornos(LocalDate data, ArrayList<Estudante> estudantes) {
        ArrayList<Estudante> semRetorno = new ArrayList<>();
        for (Estudante estudante : estudantes) {
            boolean foi = viajaNoTurno(estudante, data, estudante.getTurnoIda());
            boolean volta = viajaNoTurno(estudante, data, estudante.getTurnoVolta());
            if (foi && !volta) {
                semRetorno.add(estudante);
            }
        }
        return semRetorno;
    }

    // ---------------- troca de horário ----------------

    /**
     * Passa o estudante de uma viagem para outra (mesmo dia), mudando o turno dele.
     * Só acontece se houver vaga na viagem de destino.
     */
    public void trocarViagem(Estudante estudante, Viagem origem, Viagem destino) {
        Validador.exigirNaoNulo(estudante, "estudante");
        Validador.exigirNaoNulo(origem, "viagem de origem");
        Validador.exigirNaoNulo(destino, "viagem de destino");

        if (!origem.possuiEstudante(estudante.getMatricula())) {
            throw new RegraNegocioException(estudante + " não está na viagem de origem.");
        }
        if (!viagens.contains(destino)) {
            throw new RegraNegocioException("A viagem de destino não está confirmada.");
        }
        if (!origem.getData().equals(destino.getData())) {
            throw new RegraNegocioException("A troca só é permitida entre viagens do mesmo dia.");
        }
        if (origem.getTurno() == destino.getTurno()) {
            throw new RegraNegocioException("A viagem de destino é no mesmo turno da origem.");
        }

        boolean trocaIda = origem.getTurno() == estudante.getTurnoIda();
        Turno outroTurno = trocaIda ? estudante.getTurnoVolta() : estudante.getTurnoIda();
        if (destino.getTurno() == outroTurno) {
            throw new RegraNegocioException("Ida e retorno de " + estudante + " ficariam no mesmo turno.");
        }

        // se não houver vaga, adicionarEstudante lança exceção e NADA foi alterado ainda
        destino.adicionarEstudante(estudante);
        origem.removerEstudante(estudante.getMatricula());
        if (trocaIda) {
            estudante.setTurnoIda(destino.getTurno());
        } else {
            estudante.setTurnoVolta(destino.getTurno());
        }
    }

    // ---------------- consultas ----------------

    public ArrayList<Viagem> getViagens() {
        return new ArrayList<>(viagens);
    }

    public ArrayList<Viagem> getViagensDoDia(LocalDate data) {
        ArrayList<Viagem> doDia = new ArrayList<>();
        for (Viagem viagem : viagens) {
            if (viagem.getData().equals(data)) {
                doDia.add(viagem);
            }
        }
        return doDia;
    }
}
