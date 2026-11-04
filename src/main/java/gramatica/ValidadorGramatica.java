
package gramatica;

import modelo.Gramatica;
import modelo.Produccion;

public class ValidadorGramatica {

    public void validarProduccion(Gramatica gramatica, Produccion produccion) {

        if (gramatica == null || produccion == null) {
            throw new IllegalArgumentException("Gramatica o produccion invalida");
        }

        String izquierda = produccion.getIzquierda();
        String derecha = produccion.getDerecha();

        if (izquierda == null || !gramatica.getNoTerminales().contains(izquierda)) {
            throw new IllegalArgumentException("El no terminal no existe");
        }

        if (derecha == null || derecha.trim().isEmpty()) {
            throw new IllegalArgumentException("La produccion esta vacia");
        }

        String[] alternativas = derecha.split("\\|", -1);

        for (String alternativa : alternativas) {

            String texto = alternativa.trim();

            if (texto.isEmpty()) {
                throw new IllegalArgumentException("Alternativa vacia");
            }

            if (texto.equals("epsilon")) {
                continue;
            }

            int cantidadNoTerminales = 0;

            for (int i = 0; i < texto.length(); i++) {

                String simbolo = String.valueOf(texto.charAt(i));

                if (!gramatica.getTerminales().contains(simbolo)
                        && !gramatica.getNoTerminales().contains(simbolo)) {

                    throw new IllegalArgumentException(
                            "Simbolo no definido: " + simbolo);
                }

                if (gramatica.getNoTerminales().contains(simbolo)) {
                    cantidadNoTerminales++;
                }
            }

            if (cantidadNoTerminales > 1) {
                throw new IllegalArgumentException(
                        "La produccion no es regular");
            }

            if (cantidadNoTerminales == 1) {

                String primero = String.valueOf(texto.charAt(0));
                String ultimo = String.valueOf(texto.charAt(texto.length() - 1));

                if (!gramatica.getNoTerminales().contains(primero)
                        && !gramatica.getNoTerminales().contains(ultimo)) {

                    throw new IllegalArgumentException(
                            "El no terminal debe estar al inicio o al final");
                }
            }
        }
    }
}
