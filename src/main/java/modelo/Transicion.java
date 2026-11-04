
package modelo;

public class Transicion {

    private String origen;
    private String simbolo;
    private String destino;

    public Transicion(String origen, String simbolo, String destino) {
        this.origen = origen;
        this.simbolo = simbolo;
        this.destino = destino;
    }

    public String getOrigen() {
        return origen;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getDestino() {
        return destino;
    }

    @Override
    public String toString() {
        return origen + " --" + simbolo + "--> " + destino;
    }
}
