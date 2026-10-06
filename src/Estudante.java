public class Estudante {
    private String matricula;
    private String nome;
    private String instituicao;
    private String rota;
    private String turnoIda;
    private String turnoVolta;
    private String situacaoPag;

    //sets da classe
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public void setRota(String rota) {
        this.rota = rota;
    }

    public void setIda(String turnoIda) {
        this.turnoIda = turnoIda;
    }

    public void setTurnoVolta(String turnoVolta) {
        this.turnoVolta = turnoVolta;
    }

    public void setSituacaoPag(String situacaoPag) {
        this.situacaoPag = situacaoPag;
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

    public String getRota() {
        return rota;
    }

    public String getTurnoIda() {
        return turnoIda;
    }

    public String getTurnoVolta() {
        return turnoVolta;
    }

    public String getSituacaoPag() {
        return situacaoPag;
    }

    public Estudante() {}

}
