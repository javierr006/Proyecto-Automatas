
package archivos;

import modelo.Gramatica;
import modelo.Produccion;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LectorGTK {

    public Gramatica leer(String ruta) {

        if (ruta == null || ruta.isBlank()) {
            return null;
        }

        try (BufferedReader archivo = new BufferedReader(new FileReader(ruta))) {

            Gramatica gramatica = new Gramatica("GramaticaCargada");
            String linea;
            String inicial = null;

            while ((linea = archivo.readLine()) != null) {

                linea = linea.trim();

                if (linea.isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(">", 2);

                if (partes.length != 2) {
                    System.out.println("Produccion incorrecta: " + linea);
                    return null;
                }

                String izquierda = partes[0].trim();
                String derecha = partes[1].trim();

                if (izquierda.isEmpty() || derecha.isEmpty()) {
                    return null;
                }

                if (inicial == null) {
                    inicial = izquierda;
                }

                if (!gramatica.getNoTerminales().contains(izquierda)) {
                    gramatica.agregarNoTerminal(izquierda);
                }

                for (int i = 0; i < derecha.length(); i++) {

                    char letra = derecha.charAt(i);
                    String simbolo = String.valueOf(letra);

                    if (Character.isUpperCase(letra)) {

                        if (!gramatica.getNoTerminales().contains(simbolo)) {
                            gramatica.agregarNoTerminal(simbolo);
                        }

                    } else if (Character.isLowerCase(letra)) {

                        if (!gramatica.getTerminales().contains(simbolo)) {
                            gramatica.agregarTerminal(simbolo);
                        }
                    }
                }
            }

            if (inicial == null) {
                return null;
            }

            gramatica.setSimboloInicial(inicial);

            try (BufferedReader archivo2 = new BufferedReader(new FileReader(ruta))) {

                while ((linea = archivo2.readLine()) != null) {

                    linea = linea.trim();

                    if (linea.isEmpty()) {
                        continue;
                    }

                    String[] partes = linea.split(">", 2);

                    String izquierda = partes[0].trim();
                    String derecha = partes[1].trim();

                    gramatica.agregarProduccion(
                            new Produccion(izquierda, derecha)
                    );
                }
            }

            return gramatica;

        } catch (IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
            return null;
        }
    }
}
