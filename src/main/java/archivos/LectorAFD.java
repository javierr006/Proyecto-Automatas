/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package archivos;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import modelo.AFD;
/**
 *
 * @author josecana
 */
public class LectorAFD {
    // Leer un automata desde un archivo
    public AFD leerArchivo(String ruta) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException( "Debe indicar la ruta del archivo");
        }
        List<String> lineas = Files.readAllLines( Paths.get(ruta), StandardCharsets.UTF_8);
        Map<String, String> datos = new LinkedHashMap<>();
        List<String[]> transiciones = new ArrayList<>();
        boolean leyendoTransiciones = false;
        boolean tieneTransiciones = false;
        int numeroLinea = 0;
        // Leer cada linea del archivo
        for (String texto : lineas) {
            numeroLinea++;
            String linea = texto.trim();
            // Eliminar marca UTF-8 si existe
            if (numeroLinea == 1 && linea.startsWith("\uFEFF")) {
                linea = linea.substring(1).trim();
            }
            // Ignorar lineas vacias y comentarios
            if (linea.isEmpty() || linea.startsWith("#") || linea.startsWith("//")) {
                continue;
            }
            // Identificar la seccion de transiciones
            if (linea.equalsIgnoreCase("[TRANSICIONES]")) {
                if (tieneTransiciones) {
                    throw new IOException( "La seccion de transiciones esta repetida");
                }
                leyendoTransiciones = true;
                tieneTransiciones = true;
                continue;
            }
            if (leyendoTransiciones) {
                String[] partes = linea.split(",", -1);
                if (partes.length != 3) {
                    throw new IOException( "Transicion incorrecta en linea " + numeroLinea);
                }
                String origen = partes[0].trim();
                String simbolo = partes[1].trim();
                String destino = partes[2].trim();
                if (origen.isEmpty() || simbolo.isEmpty() || destino.isEmpty()) {
                    throw new IOException( "Transicion incompleta en linea " + numeroLinea);
                }
                transiciones.add(new String[] {
                    origen, simbolo, destino
                }
                );
            }
            else {
                // Leer los datos principales del automata
                String[] partes = linea.split("=", 2);
                if (partes.length != 2) {
                    throw new IOException( "Formato incorrecto en linea " + numeroLinea);
                }
                String clave = partes[0].trim().toLowerCase();
                String valor = partes[1].trim();
                if (!clave.equals("nombre") && !clave.equals("estados") && !clave.equals("alfabeto") && !clave.equals("inicial") && !clave.equals("aceptacion")) {
                    throw new IOException( "Campo desconocido: " + clave);
                }
                if (datos.containsKey(clave)) {
                    throw new IOException( "El campo " + clave + " esta repetido");
                }
                datos.put(clave, valor);
            }
        }
        // Verificar que existan los campos necesarios
        String[] campos = {
            "nombre", "estados", "alfabeto", "inicial", "aceptacion"
        }
        ;
        for (String campo : campos) {
            if (!datos.containsKey(campo)) {
                throw new IOException( "Falta el campo: " + campo);
            }
        }
        if (!tieneTransiciones) {
            throw new IOException( "Falta la seccion de transiciones");
        }
        if (datos.get("nombre").isEmpty()) {
            throw new IOException( "El automata debe tener un nombre");
        }
        // Crear el automata con los datos obtenidos
        AFD afd = new AFD(datos.get("nombre"));
        try {
            // Agregar los estados
            for (String estado : separarValores( datos.get("estados"))) {
                if(estado.startsWith("#") || estado.startsWith("//"))throw new IOException("El estado contiene un prefijo reservado para comentarios.");
                afd.agregarEstado(estado);
            }
            // Agregar los simbolos del alfabeto
            for (String simbolo : separarValores( datos.get("alfabeto"))) {
                new app.ServicioGramatica().validarSimbolo(simbolo);
                afd.agregarSimbolo(simbolo);
            }
            if (afd.getEstados().isEmpty()) {
                throw new IOException( "El automata no tiene estados");
            }
            // Establecer el estado inicial
            afd.establecerInicial(datos.get("inicial"));
            // Agregar los estados de aceptacion
            for (String estado : separarValores( datos.get("aceptacion"))) {
                afd.agregarEstadoAceptacion(estado);
            }
            // Agregar las transiciones
            for (String[] transicion : transiciones) {
                afd.agregarTransicion( transicion[0], transicion[1], transicion[2]);
            }
        }
        catch (IllegalArgumentException e) {
            throw new IOException( "Error en los datos del AFD: " + e.getMessage(), e);
        }
        return afd;
    }
    // Separar los valores que estan divididos por comas
    private List<String> separarValores(String texto) throws IOException {
        List<String> valores = new ArrayList<>();
        Set<String> encontrados = new HashSet<>();
        if (texto == null || texto.isEmpty()) {
            return valores;
        }
        String[] partes = texto.split(",", -1);
        for (String parte : partes) {
            String valor = parte.trim();
            if (valor.isEmpty()) {
                throw new IOException( "Se encontro un valor vacio en el archivo");
            }
            if (!encontrados.add(valor)) {
                throw new IOException( "El valor " + valor + " esta repetido");
            }
            valores.add(valor);
        }
        return valores;
    }
}
