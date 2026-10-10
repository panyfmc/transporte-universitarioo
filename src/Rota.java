/**
 * Rota entre Tobias Barreto e um município de destino.
 * Os dados não mudam depois de criada, então não há setters (objeto imutável).
 */
public class Rota {
    private final String destino;
    private final double distanciaKm;
    private final double mensalidade;

    public Rota(String destino, double distanciaKm, double mensalidade) {
        this.destino = Validador.exigirTexto(destino, "destino");
        Validador.exigirPositivo(distanciaKm, "distância");
        Validador.exigirPositivo(mensalidade, "mensalidade");
        this.distanciaKm = distanciaKm;
        this.mensalidade = mensalidade;
    }

    public String getDestino() {
        return destino;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public double getMensalidade() {
        return mensalidade;
    }

    /** Duas rotas são a mesma quando têm o mesmo destino. */
    public boolean mesmaRota(Rota outra) {
        return outra != null && destino.equalsIgnoreCase(outra.destino);
    }

    @Override
    public String toString() {
        return destino;
    }
}
