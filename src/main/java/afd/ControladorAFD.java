/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package afd;
import modelo.AFD;
/**
 *
 * @author josecana
 */
public class ControladorAFD {
    private AFD afdActual;
    public ControladorAFD() {
        this.afdActual = null;
    }
    // Crear un nuevo automata
    public AFD crearAFD(String nombre) {
        nombre = validarTexto(nombre, "nombre");
        afdActual = new AFD(nombre);
        return afdActual;
    }
    // Obtener el automata actual
    public AFD getAfdActual() {
        return afdActual;
    }
    // Cargar un automata existente
    public void cargarAFD(AFD afd) {
        if (afd == null) {
            throw new IllegalArgumentException( "El automata no puede ser nulo");
        }
        afdActual = afd;
    }
    // Verificar si existe un automata cargado
    public boolean existeAFD() {
        return afdActual != null;
    }
    // Agregar un estado al automata
    public void agregarEstado(String estado) {
        verificarAFD();
        estado = validarTexto(estado, "estado");
        if (estado.contains(",") || estado.contains("\n") || estado.contains("\r") || estado.startsWith("#") || estado.startsWith("//")) throw new IllegalArgumentException("El estado contiene separadores reservados.");
        if (afdActual.getEstados().contains(estado)) throw new IllegalArgumentException("El estado ya existe.");
        afdActual.agregarEstado(estado);
    }
    // Agregar un simbolo al alfabeto
    public void agregarSimbolo(String simbolo) {
        verificarAFD();
        simbolo = validarTexto(simbolo, "simbolo");
        new app.ServicioGramatica().validarSimbolo(simbolo);
        if (afdActual.getAlfabeto().contains(simbolo)) throw new IllegalArgumentException("El símbolo ya existe.");
        afdActual.agregarSimbolo(simbolo);
    }
    // Establecer el estado inicial
    public void establecerEstadoInicial(String estado) {
        verificarAFD();
        estado = validarTexto(estado, "estado inicial");
        afdActual.establecerInicial(estado);
    }
    // Agregar un estado de aceptacion
    public void agregarEstadoAceptacion(String estado) {
        verificarAFD();
        estado = validarTexto(estado, "estado de aceptacion");
        afdActual.agregarEstadoAceptacion(estado);
    }
    // Agregar una transicion al automata
    public void agregarTransicion(String origen, String simbolo, String destino) {
        verificarAFD();
        origen = validarTexto(origen, "estado de origen");
        simbolo = validarTexto(simbolo, "simbolo");
        destino = validarTexto(destino, "estado de destino");
        afdActual.agregarTransicion(origen, simbolo, destino);
    }
    // Verificar que exista un automata
    private void verificarAFD() {
        if (afdActual == null) {
            throw new IllegalStateException( "Primero debe crear o cargar un AFD");
        }
    }
    // Verificar que los datos no esten vacios
    private String validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException( "El campo " + campo + " no puede estar vacio");
        }
        return texto.trim();
    }
}
