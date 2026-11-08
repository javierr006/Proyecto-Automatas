
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package afd;

import java.util.ArrayList;
import java.util.List;
import modelo.AFD;
import modelo.Transicion;

/**
 *
 * @author josecana
 */
public class EvaluadorAFD {

    // Evaluar si una cadena es aceptada por el automata
    public boolean evaluarCadena(AFD afd, String cadena) {

        String estadoFinal = recorrerCadena(afd, cadena, null);

        if (estadoFinal == null) {
            return false;
        }

        return afd.getEstadosAceptacion().contains(estadoFinal);
    }

    // Obtener el recorrido realizado durante la evaluacion
    public List<String> obtenerRecorrido(AFD afd, String cadena) {

        List<String> recorrido = new ArrayList<>();

        String estadoFinal = recorrerCadena(afd, cadena, recorrido);

        if (estadoFinal == null) {
            recorrido.add("Resultado: cadena rechazada");
        } else if (afd.getEstadosAceptacion().contains(estadoFinal)) {
            recorrido.add("Estado final: " + estadoFinal);
            recorrido.add("Resultado: cadena aceptada");
        } else {
            recorrido.add("Estado final: " + estadoFinal);
            recorrido.add("Resultado: cadena rechazada");
        }

        return recorrido;
    }

    // Obtener el estado donde termina la cadena
    public String obtenerEstadoFinal(AFD afd, String cadena) {

        return recorrerCadena(afd, cadena, null);
    }

    // Recorrer los estados del automata
    private String recorrerCadena(AFD afd, String cadena,
                                  List<String> recorrido) {

        if (afd == null) {
            throw new IllegalArgumentException(
                    "No existe un automata para evaluar");
        }

        if (cadena == null) {
            throw new IllegalArgumentException(
                    "La cadena no puede ser nula");
        }

        if (afd.getEstadoInicial() == null
                || !afd.getEstados().contains(afd.getEstadoInicial())) {

            throw new IllegalArgumentException(
                    "El automata no tiene un estado inicial valido");
        }

        String estadoActual = afd.getEstadoInicial();

        if (recorrido != null) {
            recorrido.add("Estado inicial: " + estadoActual);
        }

        // Leer cada simbolo de la cadena
        for (int i = 0; i < cadena.length(); i++) {

            String simbolo = String.valueOf(cadena.charAt(i));

            // Verificar que el simbolo pertenezca al alfabeto
            if (!afd.getAlfabeto().contains(simbolo)) {

                if (recorrido != null) {
                    recorrido.add("Simbolo no valido: " + simbolo);
                }

                return null;
            }

            // Buscar la transicion correspondiente
            Transicion transicion = buscarTransicion(
                    afd, estadoActual, simbolo);

            if (transicion == null) {

                if (recorrido != null) {
                    recorrido.add("No existe una transicion desde "
                            + estadoActual + " con el simbolo " + simbolo);
                }

                return null;
            }

            String siguienteEstado = transicion.getDestino();

            if (recorrido != null) {
                recorrido.add(estadoActual + " --" + simbolo
                        + "--> " + siguienteEstado);
            }

            estadoActual = siguienteEstado;
        }

        return estadoActual;
    }

    // Buscar una transicion segun el estado y simbolo
    private Transicion buscarTransicion(AFD afd, String estado,
                                        String simbolo) {

        for (Transicion t : afd.getTransiciones()) {

            if (t.getOrigen().equals(estado)
                    && t.getSimbolo().equals(simbolo)) {

                return t;
            }
        }

        return null;
    }
}
