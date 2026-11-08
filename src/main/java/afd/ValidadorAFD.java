/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package afd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import modelo.AFD;
import modelo.Transicion;
/**
 *
 * @author josecana
 */
public class ValidadorAFD {
    // Verificar si el automata es valido
    public boolean esValido(AFD afd) {
        return obtenerErrores(afd).isEmpty();
    }
    // Obtener los errores encontrados en el automata
    public List<String> obtenerErrores(AFD afd) {
        List<String> errores = new ArrayList<>();
        if (afd == null) {
            errores.add("No existe un automata para validar");
            return errores;
        }
        // Verificar que tenga estados
        if (afd.getEstados().isEmpty()) {
            errores.add("El automata debe tener al menos un estado");
        }
        // Verificar que tenga un alfabeto
        // Verificar que los estados tengan nombres validos
        for (String estado : afd.getEstados()) {
            if (estado == null || estado.trim().isEmpty()) {
                errores.add("Hay un estado sin nombre");
            }
        }
        // Verificar los simbolos del alfabeto
        for (String simbolo : afd.getAlfabeto()) {
            if (simbolo == null || simbolo.length() != 1 || simbolo.equals("ε") || simbolo.isBlank() || ",#=>|".contains(simbolo)) {
                errores.add("Cada símbolo debe ser un carácter válido distinto de epsilon y de los separadores reservados.");
            }
        }
        // Verificar el estado inicial
        if (afd.getEstadoInicial() == null) {
            errores.add("No se ha definido el estado inicial");
        }
        else if (!afd.getEstados().contains(afd.getEstadoInicial())) {
            errores.add("El estado inicial no existe");
        }
        // Verificar los estados de aceptacion
        for (String estado : afd.getEstadosAceptacion()) {
            if (!afd.getEstados().contains(estado)) {
                errores.add("El estado de aceptacion " + estado + " no existe");
            }
        }
        // Guardar las transiciones encontradas
        Map<String, Set<String>> transiciones = new HashMap<>();
        for (String estado : afd.getEstados()) {
            transiciones.put(estado, new HashSet<>());
        }
        // Revisar cada transicion
        for (Transicion t : afd.getTransiciones()) {
            if (t == null) {
                errores.add("Se encontro una transicion nula");
                continue;
            }
            String origen = t.getOrigen();
            String simbolo = t.getSimbolo();
            String destino = t.getDestino();
            if (!afd.getEstados().contains(origen)) {
                errores.add("El estado de origen " + origen + " no existe");
            }
            if (!afd.getEstados().contains(destino)) {
                errores.add("El estado de destino " + destino + " no existe");
            }
            if (!afd.getAlfabeto().contains(simbolo)) {
                errores.add("El simbolo " + simbolo + " no pertenece al alfabeto");
            }
            // Verificar que no existan transiciones repetidas
            if (transiciones.containsKey(origen) && afd.getAlfabeto().contains(simbolo)) {
                Set<String> simbolos = transiciones.get(origen);
                if (simbolos.contains(simbolo)) {
                    errores.add("Transicion duplicada desde " + origen + " con el simbolo " + simbolo);
                }
                else {
                    simbolos.add(simbolo);
                }
            }
        }
        // Verificar que cada estado tenga sus transiciones
        for (String estado : afd.getEstados()) {
            for (String simbolo : afd.getAlfabeto()) {
                if (!transiciones.get(estado).contains(simbolo)) {
                    errores.add("Falta una transicion desde " + estado + " con el simbolo " + simbolo);
                }
            }
        }
        return errores;
    }
}
