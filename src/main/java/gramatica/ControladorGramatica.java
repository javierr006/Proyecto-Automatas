
package gramatica;

import modelo.Gramatica;
import modelo.Produccion;

public class ControladorGramatica {

    public Gramatica crearGramatica(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese un nombre");
        }

        return new Gramatica(nombre);
    }

    public void agregarNoTerminal(Gramatica gramatica, String simbolo) {
        gramatica.agregarNoTerminal(simbolo);
    }

    public void agregarTerminal(Gramatica gramatica, String simbolo) {
        gramatica.agregarTerminal(simbolo);
    }

    public void establecerInicial(Gramatica gramatica, String simbolo) {
        gramatica.setSimboloInicial(simbolo);
    }

    public void agregarProduccion(Gramatica gramatica,
                                  String izquierda, String derecha) {

        if (derecha == null) {
            throw new IllegalArgumentException("La produccion esta vacia");
        }

        String[] alternativas = derecha.split("\\|", -1);

        ValidadorGramatica validador = new ValidadorGramatica();

        // Primero revisamos todas las alternativas
        for (String alternativa : alternativas) {

            Produccion produccion = new Produccion(
                    izquierda, alternativa.trim());

            validador.validarProduccion(gramatica, produccion);

            for (Produccion existente : gramatica.getProducciones()) {
                if (existente.getIzquierda().equals(izquierda)
                        && existente.getDerecha().equals(produccion.getDerecha())) {
                    throw new IllegalArgumentException(
                            "La produccion ya existe");
                }
            }
        }

        // Revisamos que no se repitan dentro de la misma entrada
        for (int i = 0; i < alternativas.length; i++) {
            for (int j = i + 1; j < alternativas.length; j++) {
                if (alternativas[i].trim().equals(alternativas[j].trim())) {
                    throw new IllegalArgumentException(
                            "La produccion esta repetida");
                }
            }
        }

        // Si todas son correctas, las guardamos
        for (String alternativa : alternativas) {

            Produccion produccion = new Produccion(
                    izquierda, alternativa.trim());

            gramatica.agregarProduccion(produccion);
        }
    }
}
