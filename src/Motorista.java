public class Motorista {
    private String cpf;
    private String nome;
    private String cnh;
    private String telefone;

    // Construtor com os dados obrigatórios: não existe mais motorista "pela metade".
    public Motorista(String cpf, String nome, String cnh, String telefone) {
        setCpf(cpf);
        setNome(nome);
        setCnh(cnh);
        setTelefone(telefone);
    }

    //sets da classe (agora validam o dado recebido)
    public void setCpf(String cpf) {
        this.cpf = Validador.exigirTexto(cpf, "cpf");
    }

    public void setNome(String nome) {
        this.nome = Validador.exigirTexto(nome, "nome");
    }

    public void setCnh(String cnh) {
        this.cnh = Validador.exigirTexto(cnh, "cnh");
    }

    public void setTelefone(String telefone) {
        this.telefone = Validador.exigirTexto(telefone, "telefone");
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

    @Override
    public String toString() {
        return nome;
    }
}
