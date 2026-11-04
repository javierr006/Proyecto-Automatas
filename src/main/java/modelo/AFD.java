
package modelo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AFD {

    private String nombre;
    private Set<String> estados = new LinkedHashSet<>();
    private Set<String> alfabeto = new LinkedHashSet<>();
    private String estadoInicial;
    private Set<String> estadosAceptacion = new LinkedHashSet<>();
    private List<Transicion> transiciones = new ArrayList<>();

    public AFD(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public Set<String> getEstados() {
        return estados;
    }

    public Set<String> getAlfabeto() {
        return alfabeto;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public Set<String> getEstadosAceptacion() {
        return estadosAceptacion;
    }

    public List<Transicion> getTransiciones() {
        return transiciones;
    }

    public void agregarEstado(String estado) {
        estados.add(estado);
    }

    public void agregarSimbolo(String simbolo) {
        alfabeto.add(simbolo);
    }

    public void establecerInicial(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("El estado no existe");
        }

        estadoInicial = estado;
    }

    public void agregarEstadoAceptacion(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("El estado no existe");
        }

        estadosAceptacion.add(estado);
    }

    public void agregarTransicion(String origen, String simbolo,
                                 String destino) {

        if (!estados.contains(origen) || !estados.contains(destino)) {
            throw new IllegalArgumentException("El estado no existe");
        }

        if (!alfabeto.contains(simbolo)) {
            throw new IllegalArgumentException("El simbolo no existe");
        }

        for (Transicion t : transiciones) {
            if (t.getOrigen().equals(origen)
                    && t.getSimbolo().equals(simbolo)) {

                throw new IllegalArgumentException(
                        "Ya existe una transicion para ese estado y simbolo");
            }
        }

        transiciones.add(new Transicion(origen, simbolo, destino));
    }
}
