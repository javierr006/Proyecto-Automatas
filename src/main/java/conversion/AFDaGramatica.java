/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conversion;
import java.util.LinkedHashMap;
import java.util.Map;
import modelo.AFD;
import modelo.Gramatica;
import modelo.Produccion;
import modelo.Transicion;
/**
 *
 * @author josecana
 */
public class AFDaGramatica {
    // Convertir un automata a una gramatica regular
    public Gramatica convertir(AFD afd) {
        if (afd == null) {
            throw new IllegalArgumentException( "No existe un automata para convertir");
        }
        if (afd.getEstados().isEmpty()) {
            throw new IllegalArgumentException( "El automata no tiene estados");
        }
        if (afd.getEstadoInicial() == null || !afd.getEstados().contains(afd.getEstadoInicial())) {
            throw new IllegalArgumentException( "El automata no tiene un estado inicial valido");
        }
        // Crear la gramatica
        Gramatica gramatica = new Gramatica( "Gramatica_" + afd.getNombre());
        // Relacionar los estados con los no terminales
        Map<String, String> equivalencias = new LinkedHashMap<>();
        int contador = 0;
        for (String estado : afd.getEstados()) {
            int letra = 'A' + contador;
            while (!Character.isLetter((char)letra) || afd.getAlfabeto().contains(String.valueOf((char)letra)) || letra == 'ε') letra++;
            String noTerminal = String.valueOf((char)letra);
            contador = letra - 'A';
            equivalencias.put(estado, noTerminal);
            gramatica.agregarNoTerminal(noTerminal);
            contador++;
        }
        // Agregar los simbolos del alfabeto
        for (String simbolo : afd.getAlfabeto()) {
            if (simbolo == null || simbolo.length() != 1 || simbolo.equals("ε")) {
                throw new IllegalArgumentException( "El alfabeto debe tener simbolos individuales validos");
            }
            gramatica.agregarTerminal(simbolo);
        }
        // Definir el simbolo inicial
        String inicial = equivalencias.get(afd.getEstadoInicial());
        gramatica.setSimboloInicial(inicial);
        // Convertir las transiciones en producciones
        for (Transicion transicion : afd.getTransiciones()) {
            String origen = equivalencias.get( transicion.getOrigen());
            String destino = equivalencias.get( transicion.getDestino());
            String simbolo = transicion.getSimbolo();
            if (origen == null || destino == null || !afd.getAlfabeto().contains(simbolo)) {
                throw new IllegalArgumentException( "El automata contiene una transicion invalida");
            }
            String derecha = simbolo + destino;
            Produccion produccion = new Produccion( origen, derecha);
            gramatica.agregarProduccion(produccion);
        }
        // Agregar producciones vacias a estados de aceptacion
        for (String estado : afd.getEstadosAceptacion()) {
            String noTerminal = equivalencias.get(estado);
            if (noTerminal == null) {
                throw new IllegalArgumentException( "Existe un estado de aceptacion invalido");
            }
            Produccion produccion = new Produccion( noTerminal, "epsilon");
            gramatica.agregarProduccion(produccion);
        }
        return gramatica;
    }
}
