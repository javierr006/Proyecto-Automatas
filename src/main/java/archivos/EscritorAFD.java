/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package archivos;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import modelo.AFD;
import modelo.Transicion;
/**
 *
 * @author josecana
 */
public class EscritorAFD {
    // Guardar un automata en un archivo
    public void guardarArchivo(AFD afd, String ruta) throws IOException {
        if (afd == null) {
            throw new IllegalArgumentException( "No existe un automata para guardar");
        }
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException( "Debe indicar la ruta del archivo");
        }
        // Verificar los datos principales del automata
        validarDatos(afd);
        StringBuilder contenido = new StringBuilder();
        // Guardar el nombre
        contenido.append("nombre=") .append(afd.getNombre()) .append("\n");
        // Guardar los estados
        contenido.append("estados=") .append(String.join(",", afd.getEstados())) .append("\n");
        // Guardar el alfabeto
        contenido.append("alfabeto=") .append(String.join(",", afd.getAlfabeto())) .append("\n");
        // Guardar el estado inicial
        contenido.append("inicial=") .append(afd.getEstadoInicial()) .append("\n");
        // Guardar los estados de aceptacion
        contenido.append("aceptacion=") .append(String.join(",", afd.getEstadosAceptacion())) .append("\n");
        // Agregar la seccion de transiciones
        contenido.append("\n[TRANSICIONES]\n");
        for (Transicion transicion : afd.getTransiciones()) {
            contenido.append(transicion.getOrigen()) .append(",") .append(transicion.getSimbolo()) .append(",") .append(transicion.getDestino()) .append("\n");
        }
        // Escribir el contenido en el archivo
        app.ServicioArchivos.escribirAtomico(Paths.get(ruta), contenido.toString());
    }
    // Verificar los datos antes de guardar
    private void validarDatos(AFD afd) {
        if (afd.getNombre() == null || afd.getNombre().trim().isEmpty() || afd.getNombre().contains("\n") || afd.getNombre().contains("\r")) {
            throw new IllegalArgumentException( "El automata debe tener un nombre valido");
        }
        if (afd.getEstados().isEmpty()) {
            throw new IllegalArgumentException( "El automata debe tener estados");
        }
        // Verificar los nombres de los estados
        for (String estado : afd.getEstados()) {
            validarValor(estado, "estado");
        }
        // Verificar los simbolos del alfabeto
        for (String simbolo : afd.getAlfabeto()) {
            validarValor(simbolo, "simbolo");
            new app.ServicioGramatica().validarSimbolo(simbolo);
            if (simbolo.length() != 1 || simbolo.equals("ε")) {
                throw new IllegalArgumentException( "El alfabeto debe tener simbolos individuales");
            }
        }
        // Verificar el estado inicial
        if (afd.getEstadoInicial() == null || !afd.getEstados().contains( afd.getEstadoInicial())) {
            throw new IllegalArgumentException( "El estado inicial no es valido");
        }
        // Verificar los estados de aceptacion
        for (String estado : afd.getEstadosAceptacion()) {
            if (!afd.getEstados().contains(estado)) {
                throw new IllegalArgumentException( "Existe un estado de aceptacion invalido");
            }
        }
        // Verificar las transiciones
        Set<String> encontradas = new HashSet<>();
        for (Transicion transicion : afd.getTransiciones()) {
            if (transicion == null) {
                throw new IllegalArgumentException( "Existe una transicion vacia");
            }
            String origen = transicion.getOrigen();
            String simbolo = transicion.getSimbolo();
            String destino = transicion.getDestino();
            if (!afd.getEstados().contains(origen) || !afd.getEstados().contains(destino)) {
                throw new IllegalArgumentException( "Existe una transicion con estados invalidos");
            }
            if (!afd.getAlfabeto().contains(simbolo)) {
                throw new IllegalArgumentException( "Existe una transicion con un simbolo invalido");
            }
            String clave = origen + "\u0000" + simbolo;
            if (!encontradas.add(clave)) {
                throw new IllegalArgumentException( "Existen transiciones duplicadas");
            }
        }
    }
    // Verificar que un valor se pueda guardar correctamente
    private void validarValor(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty() || !valor.equals(valor.trim()) || valor.startsWith("#") || valor.startsWith("//") || valor.contains(",") || valor.contains("\n") || valor.contains("\r")) {
            throw new IllegalArgumentException( "El " + campo + " contiene un valor invalido");
        }
    }
}
