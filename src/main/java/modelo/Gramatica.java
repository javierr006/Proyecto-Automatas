
package modelo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Gramatica {

    private String nombre;
    private Set<String> noTerminales = new LinkedHashSet<>();
    private Set<String> terminales = new LinkedHashSet<>();
    private String simboloInicial;
    private List<Produccion> producciones = new ArrayList<>();

    public Gramatica(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public Set<String> getNoTerminales() {
        return noTerminales;
    }

    public Set<String> getTerminales() {
        return terminales;
    }

    public String getSimboloInicial() {
        return simboloInicial;
    }

    public List<Produccion> getProducciones() {
        return producciones;
    }

    public void agregarNoTerminal(String simbolo) {
        if (simbolo == null || simbolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese un simbolo valido");
        }

        if (terminales.contains(simbolo) || !noTerminales.add(simbolo)) {
            throw new IllegalArgumentException("El simbolo ya existe");
        }
    }

    public void agregarTerminal(String simbolo) {
        if (simbolo == null || simbolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese un simbolo valido");
        }

        if (noTerminales.contains(simbolo) || !terminales.add(simbolo)) {
            throw new IllegalArgumentException("El simbolo ya existe");
        }
    }

    public void setSimboloInicial(String simbolo) {
        if (!noTerminales.contains(simbolo)) {
            throw new IllegalArgumentException("El simbolo inicial debe ser un no terminal");
        }

        simboloInicial = simbolo;
    }

    public void agregarProduccion(Produccion produccion) {
        if (produccion == null) {
            throw new IllegalArgumentException("La produccion no puede estar vacia");
        }

        if (!noTerminales.contains(produccion.getIzquierda())) {
            throw new IllegalArgumentException("El no terminal no existe");
        }

        for (Produccion p : producciones) {
            if (p.getIzquierda().equals(produccion.getIzquierda())
                    && p.getDerecha().equals(produccion.getDerecha())) {
                throw new IllegalArgumentException("La produccion ya existe");
            }
        }

        producciones.add(produccion);
    }
}
