    
package archivos;

import modelo.Gramatica;
import modelo.Produccion;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EscritorGTK {

    public boolean guardar(Gramatica gramatica, String ruta) {

        if (gramatica == null || ruta == null || ruta.isBlank()) {
            return false;
        }

        try (BufferedWriter archivo = new BufferedWriter(new FileWriter(ruta))) {

            for (Produccion produccion : gramatica.getProducciones()) {
                archivo.write(produccion.getIzquierda()
                        + ">"
                        + produccion.getDerecha());
                archivo.newLine();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar: " + e.getMessage());
            return false;
        }
    }
}
