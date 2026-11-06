
package reportes;

import java.util.ArrayList;
import java.util.List;

public class DetalleReporte {

    private String nombreGramatica;
    private String cadena;
    private boolean aceptada;
    private List<String> derivacion;

    public DetalleReporte(String nombreGramatica, String cadena,
            boolean aceptada, List<String> derivacion) {

        this.nombreGramatica = nombreGramatica;
        this.cadena = cadena;
        this.aceptada = aceptada;

        if (derivacion == null) {
            this.derivacion = new ArrayList<>();
        } else {
            this.derivacion = new ArrayList<>(derivacion);
        }
    }

    public String getNombreGramatica() {
        return nombreGramatica;
    }

    public String getCadena() {
        return cadena;
    }

    public boolean isAceptada() {
        return aceptada;
    }

    public List<String> getDerivacion() {
        return new ArrayList<>(derivacion);
    }

    public String getResultado() {
        if (aceptada) {
            return "ACEPTADA";
        } else {
            return "RECHAZADA";
        }
    }

    public void mostrarDetalle() {

        System.out.println("GRAMATICA: " + nombreGramatica);
        System.out.println("CADENA: " + cadena);
        System.out.println("RESULTADO: " + getResultado());

        if (!derivacion.isEmpty()) {

            System.out.println("DERIVACION:");

            for (String paso : derivacion) {
                System.out.println(paso);
            }
        }
    }
}
