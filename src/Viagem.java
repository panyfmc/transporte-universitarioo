import java.util.ArrayList;

public class Viagem {
    private String data;
    private String turno;
    private String rota;
    private String veiculo;
    private Motorista motorista;
    private ArrayList<Estudante> estudantes;

    //sets da classe
    public void setData(String data) {
        this.data = data;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public void setRota(String rota) {
        this.rota = rota;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    
    //gets da classe
    public String getData() {
        return data;
    }

    public String getTurno() {
        return turno;
    } 

    public String getRota() {
        return rota;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    // falta criar os metodos do parametro estudantes

    public Viagem() {}


}
