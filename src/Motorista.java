public class Motorista {
    private String cpf;
    private String nome;
    private String cnh;
    private String telefone;
    private String disponibilidade;
    private String turnos;


    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCnh(String cnh) {
        this.cnh = cnh;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setDisponibilidade(String disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public void setTurnos(String turnos) {
        this.turnos = turnos;
    }

    //gets da classe
    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getCnh() {
        return cnh;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public String getTurnos() {
        return turnos;
    }

    public Motorista() {}


}
