
package gramatica;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import modelo.Gramatica;
import modelo.Produccion;

public class EvaluadorGramatica {

    private List<String> derivacion;
    private Set<String> visitados;

    public boolean evaluar(Gramatica gramatica, String cadena) {

        derivacion = new ArrayList<>();
        visitados = new HashSet<>();

        if (gramatica == null || cadena == null
                || gramatica.getSimboloInicial() == null) {
            return false;
        }

        String inicial = gramatica.getSimboloInicial();

        derivacion.add(inicial);

        return buscar(gramatica, inicial, cadena, 0);
    }

    private boolean buscar(Gramatica gramatica, String actual,
                           String cadena, int pasos) {

        if (pasos > cadena.length() + gramatica.getNoTerminales().size() + 5) {
            return false;
        }

        if (actual.equals(cadena)) {
            return true;
        }

        if (!visitados.add(actual)) {
            return false;
        }

        for (Produccion produccion : gramatica.getProducciones()) {

            String izquierda = produccion.getIzquierda();
            String derecha = produccion.getDerecha();

            if (derecha.equals("epsilon")) {
                derecha = "";
            }

            int posicion = actual.indexOf(izquierda);

            if (posicion == -1) {
                continue;
            }

            String siguiente = actual.substring(0, posicion)
                    + derecha
                    + actual.substring(posicion + izquierda.length());

            if (siguiente.equals(actual)) {
                continue;
            }

            if (contarTerminales(siguiente, gramatica) > cadena.length()) {
                continue;
            }

            derivacion.add(siguiente);

            if (buscar(gramatica, siguiente, cadena, pasos + 1)) {
                return true;
            }

            derivacion.remove(derivacion.size() - 1);
        }

        return false;
    }

    private int contarTerminales(String texto, Gramatica gramatica) {

        int cantidad = 0;

        for (int i = 0; i < texto.length(); i++) {

            String simbolo = String.valueOf(texto.charAt(i));

            if (gramatica.getTerminales().contains(simbolo)) {
                cantidad++;
            }
        }

        return cantidad;
    }

    public List<String> getDerivacion() {
        return derivacion;
    }
}
