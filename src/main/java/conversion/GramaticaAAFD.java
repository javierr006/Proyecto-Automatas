
package conversion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import modelo.AFD;
import modelo.Gramatica;
import modelo.Produccion;

public class GramaticaAAFD {

    public AFD convertir(Gramatica gramatica) {

        if (gramatica == null || gramatica.getSimboloInicial() == null) {
            throw new IllegalArgumentException("Gramatica invalida");
        }

        Map<String, Map<String, Set<String>>> movimientos =
                new LinkedHashMap<>();

        Set<String> finales = new LinkedHashSet<>();
        String estadoFinal = "FINAL";

        while (gramatica.getNoTerminales().contains(estadoFinal)) {
            estadoFinal += "_";
        }

        for (String estado : gramatica.getNoTerminales()) {
            movimientos.put(estado, new LinkedHashMap<>());
        }

        movimientos.put(estadoFinal, new LinkedHashMap<>());
        finales.add(estadoFinal);

        for (Produccion p : gramatica.getProducciones()) {

            String origen = p.getIzquierda();
            String derecha = p.getDerecha();

            if (derecha.equals("epsilon")) {
                finales.add(origen);
                continue;
            }

            String simbolo = String.valueOf(derecha.charAt(0));

            if (!gramatica.getTerminales().contains(simbolo)) {
                throw new IllegalArgumentException(
                        "Solo se permiten producciones lineales por la derecha");
            }

            String destino;

            if (derecha.length() == 1) {
                destino = estadoFinal;
            } else if (derecha.length() == 2) {
                destino = String.valueOf(derecha.charAt(1));

                if (!gramatica.getNoTerminales().contains(destino)) {
                    throw new IllegalArgumentException(
                            "Produccion no valida: " + derecha);
                }
            } else {
                throw new IllegalArgumentException(
                        "Produccion no valida: " + derecha);
            }

            movimientos.get(origen)
                    .computeIfAbsent(simbolo, k -> new LinkedHashSet<>())
                    .add(destino);
        }

        AFD afd = new AFD("AFD_" + gramatica.getNombre());

        for (String simbolo : gramatica.getTerminales()) {
            afd.agregarSimbolo(simbolo);
        }

        Map<Set<String>, String> nombres = new LinkedHashMap<>();
        ArrayList<Set<String>> pendientes = new ArrayList<>();

        Set<String> inicial = new LinkedHashSet<>();
        inicial.add(gramatica.getSimboloInicial());

        nombres.put(inicial, "q0");
        pendientes.add(inicial);
        afd.agregarEstado("q0");
        afd.establecerInicial("q0");

        int posicion = 0;

        while (posicion < pendientes.size()) {

            Set<String> conjunto = pendientes.get(posicion++);
            String nombreOrigen = nombres.get(conjunto);

            for (String estado : conjunto) {
                if (finales.contains(estado)) {
                    afd.agregarEstadoAceptacion(nombreOrigen);
                    break;
                }
            }

            for (String simbolo : gramatica.getTerminales()) {

                Set<String> destinos = new LinkedHashSet<>();

                for (String estado : conjunto) {
                    Map<String, Set<String>> mapa = movimientos.get(estado);

                    if (mapa != null && mapa.containsKey(simbolo)) {
                        destinos.addAll(mapa.get(simbolo));
                    }
                }

                if (destinos.isEmpty()) {
                    continue;
                }

                if (!nombres.containsKey(destinos)) {
                    String nuevoNombre = "q" + nombres.size();
                    Set<String> nuevo = new LinkedHashSet<>(destinos);

                    nombres.put(nuevo, nuevoNombre);
                    pendientes.add(nuevo);
                    afd.agregarEstado(nuevoNombre);
                }

                afd.agregarTransicion(
                        nombreOrigen, simbolo, nombres.get(destinos));
            }
        }

        return afd;
    }
}
