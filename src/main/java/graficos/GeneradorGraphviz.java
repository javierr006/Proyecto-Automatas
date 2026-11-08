/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package graficos;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import modelo.AFD;
import modelo.Transicion;
/**
 *
 * @author josecana
 */
public class GeneradorGraphviz {
    // Generar el codigo DOT de un automata
    public String generarDOT(AFD afd) {
        if (afd == null) {
            throw new IllegalArgumentException( "No existe un automata para graficar");
        }
        if (afd.getEstados().isEmpty()) {
            throw new IllegalArgumentException( "El automata no tiene estados");
        }
        if (afd.getEstadoInicial() == null || !afd.getEstados().contains(afd.getEstadoInicial())) {
            throw new IllegalArgumentException( "El automata no tiene un estado inicial valido");
        }
        StringBuilder codigo = new StringBuilder();
        // Crear los identificadores de los estados
        Map<String, String> identificadores = new LinkedHashMap<>();
        int contador = 0;
        for (String estado : afd.getEstados()) {
            if (estado == null || estado.trim().isEmpty()) {
                throw new IllegalArgumentException( "Existe un estado sin nombre");
            }
            identificadores.put(estado, "q" + contador);
            contador++;
        }
        // Configuracion principal del grafico
        codigo.append("digraph AFD {\n");
        codigo.append("    rankdir=LR;\n");
        codigo.append("    graph [label=\"") .append(escaparTexto(afd.getNombre() == null ? "AFD" : afd.getNombre())) .append("\", labelloc=t];\n");
        codigo.append("    node [shape=circle, style=filled, fillcolor=\"#e9f2ff\", color=\"#2563eb\", fontname=\"Helvetica\"];\n");
        // Dibujar los estados
        for (String estado : afd.getEstados()) {
            String identificador = identificadores.get(estado);
            codigo.append("    ") .append(identificador) .append(" [label=\"") .append(escaparTexto(estado)) .append("\"");
            if (afd.getEstadosAceptacion().contains(estado)) {
                codigo.append(", shape=doublecircle");
            }
            codigo.append("];\n");
        }
        // Agregar el indicador del estado inicial
        codigo.append("    inicio [shape=point, label=\"\"];\n");
        codigo.append("    inicio -> ") .append(identificadores.get(afd.getEstadoInicial())) .append(";\n");
        // Dibujar las transiciones
        for (Transicion transicion : afd.getTransiciones()) {
            if (transicion == null) {
                throw new IllegalArgumentException( "Existe una transicion vacia");
            }
            String origen = identificadores.get( transicion.getOrigen());
            String destino = identificadores.get( transicion.getDestino());
            String simbolo = transicion.getSimbolo();
            if (origen == null || destino == null || simbolo == null || !afd.getAlfabeto().contains(simbolo)) {
                throw new IllegalArgumentException( "Existe una transicion invalida");
            }
            codigo.append("    ") .append(origen) .append(" -> ") .append(destino) .append(" [label=\"") .append(escaparTexto(simbolo)) .append("\"];\n");
        }
        codigo.append("}\n");
        return codigo.toString();
    }
    // Guardar el codigo DOT en un archivo
    public void guardarDOT(AFD afd, String ruta) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException( "Debe indicar la ruta del archivo");
        }
        String contenido = generarDOT(afd);
        Files.write( Paths.get(ruta), contenido.getBytes(StandardCharsets.UTF_8));
    }
    // Generar una imagen PNG del automata
    public void generarPNG(AFD afd, String rutaImagen) throws IOException, InterruptedException {
        if (rutaImagen == null || rutaImagen.trim().isEmpty()) {
            throw new IllegalArgumentException( "Debe indicar la ruta de la imagen");
        }
        Path archivoTemporal = Files.createTempFile("automata_", ".dot");
        Path log = Files.createTempFile("graphviz_", ".log");
        Path salida = Files.createTempFile(Paths.get(rutaImagen).toAbsolutePath().getParent(), "graphviz_", ".png");
        Process ejecucion = null;
        try {
            guardarDOT(afd, archivoTemporal.toString());
            String dot = System.getProperty("graphviz.dot", "dot");
            if (dot.equals("dot")) {
                for (String candidato : new String[] {
                    "/opt/homebrew/bin/dot", "/usr/local/bin/dot", "/opt/local/bin/dot"
                }
                ) if (Files.isExecutable(Path.of(candidato))) {
                    dot = candidato;
                    break;
                }
            }
            try {
                ejecucion = new ProcessBuilder(dot, "-Tpng", archivoTemporal.toString(), "-o", salida.toString()) .redirectErrorStream(true).redirectOutput(log.toFile()).start();
            }
            catch (IOException ex) {
                throw new IOException("Graphviz no está instalado o no se encontró dot. Puede usar Exportar PNG, que no requiere Graphviz.", ex);
            }
            if (!ejecucion.waitFor(30, java.util.concurrent.TimeUnit.SECONDS)) throw new IOException("Graphviz excedió el tiempo de espera de 30 segundos.");
            if (ejecucion.exitValue() != 0 || Files.size(salida) == 0) throw new IOException("Graphviz: " + Files.readString(log));
            app.ServicioArchivos.reemplazar(salida, Path.of(rutaImagen).toAbsolutePath());
        }
        finally {
            if (ejecucion != null && ejecucion.isAlive()) {
                ejecucion.destroyForcibly();
                ejecucion.waitFor();
            }
            Files.deleteIfExists(archivoTemporal);
            Files.deleteIfExists(log);
            Files.deleteIfExists(salida);
        }
    }
    // Evitar problemas con caracteres especiales en DOT
    private String escaparTexto(String texto) {
        return texto.replace("\\", "\\\\") .replace("\"", "\\\"") .replace("\n", "\\n") .replace("\r", "\\r");
    }
}
