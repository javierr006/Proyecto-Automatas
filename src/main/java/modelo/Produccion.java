
package modelo;

public class Produccion {

    private String izquierda;
    private String derecha;

    public Produccion(String izquierda, String derecha) {
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    public String getIzquierda() {
        return izquierda;
    }

    public String getDerecha() {
        return derecha;
    }

    @Override
    public String toString() {
        return izquierda + ">" + derecha;
    }
}
