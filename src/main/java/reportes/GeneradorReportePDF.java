
package reportes;

import modelo.Gramatica;
import modelo.Produccion;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class GeneradorReportePDF {

    private final Color azul = new Color(24, 48, 83);
    private final Color azulClaro = new Color(232, 239, 248);
    private final Color gris = new Color(100, 110, 125);
    private final Color negro = new Color(35, 42, 52);
    private final Color verde = new Color(28, 115, 75);
    private final Color rojo = new Color(170, 55, 55);

    private final PDType1Font normal =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private final PDType1Font negrita =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    public boolean generar(Gramatica gramatica, String ruta) {
        return generar(gramatica, null, ruta);
    }

    public boolean generar(Gramatica gramatica,
            DetalleReporte detalle, String ruta) {

        if (gramatica == null || ruta == null || ruta.isBlank()) {
            return false;
        }

        try (PDDocument documento = new PDDocument()) {

            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            PDPageContentStream contenido =
                    new PDPageContentStream(documento, pagina);

            float ancho = pagina.getMediaBox().getWidth();
            float alto = pagina.getMediaBox().getHeight();
            float y = alto - 145;

            encabezado(contenido, ancho, alto);

            escribir(contenido, "INFORMACION GENERAL",
                    45, y, 13, negrita, azul);

            y -= 15;

            rectangulo(contenido, 45, y - 105,
                    ancho - 90, 105, azulClaro);

            escribir(contenido, "Nombre de la gramatica",
                    60, y - 20, 9, normal, gris);

            escribir(contenido, gramatica.getNombre(),
                    60, y - 37, 12, negrita, negro);

            escribir(contenido, "Simbolo inicial",
                    320, y - 20, 9, normal, gris);

            escribir(contenido,
                    String.valueOf(gramatica.getSimboloInicial()),
                    320, y - 37, 12, negrita, negro);

            escribir(contenido, "No terminales",
                    60, y - 65, 9, normal, gris);

            escribir(contenido,
                    gramatica.getNoTerminales().toString(),
                    60, y - 82, 11, negrita, negro);

            escribir(contenido, "Terminales",
                    320, y - 65, 9, normal, gris);

            escribir(contenido,
                    gramatica.getTerminales().toString(),
                    320, y - 82, 11, negrita, negro);

            y -= 150;

            escribir(contenido, "PRODUCCIONES DE LA GRAMATICA",
                    45, y, 13, negrita, azul);

            y -= 30;

            rectangulo(contenido, 45, y - 5,
                    ancho - 90, 30, azul);

            escribir(contenido, "No.",
                    60, y + 5, 10, negrita, Color.WHITE);

            escribir(contenido, "PRODUCCION",
                    125, y + 5, 10, negrita, Color.WHITE);

            y -= 35;

            List<Produccion> producciones =
                    new ArrayList<>(gramatica.getProducciones());

            int numero = 1;

            for (Produccion p : producciones) {

                if (y < 115) {
                    pie(contenido, ancho, documento.getNumberOfPages());
                    contenido.close();

                    pagina = new PDPage(PDRectangle.A4);
                    documento.addPage(pagina);

                    contenido = new PDPageContentStream(documento, pagina);
                    encabezado(contenido, ancho, alto);
                    y = alto - 145;
                }

                if (numero % 2 == 1) {
                    rectangulo(contenido, 45, y - 9,
                            ancho - 90, 28,
                            new Color(245, 247, 250));
                }

                escribir(contenido, String.valueOf(numero),
                        60, y, 10, normal, negro);

                escribir(contenido, p.toString(),
                        125, y, 10, normal, negro);

                linea(contenido, 45, y - 12,
                        ancho - 45, y - 12,
                        new Color(220, 225, 232));

                y -= 29;
                numero++;
            }

            if (producciones.isEmpty()) {
                escribir(contenido, "No hay producciones registradas.",
                        60, y, 10, normal, gris);
                y -= 35;
            }

            if (detalle != null) {

                if (y < 260) {
                    pie(contenido, ancho, documento.getNumberOfPages());
                    contenido.close();

                    pagina = new PDPage(PDRectangle.A4);
                    documento.addPage(pagina);

                    contenido = new PDPageContentStream(documento, pagina);
                    encabezado(contenido, ancho, alto);
                    y = alto - 145;
                }

                y -= 30;

                escribir(contenido, "RESULTADO DE EVALUACION",
                        45, y, 13, negrita, azul);

                y -= 25;

                rectangulo(contenido, 45, y - 65,
                        ancho - 90, 80, azulClaro);

                escribir(contenido, "Cadena evaluada",
                        60, y, 9, normal, gris);

                escribir(contenido, detalle.getCadena(),
                        60, y - 22, 12, negrita, negro);

                escribir(contenido, "Resultado",
                        320, y, 9, normal, gris);

                Color colorResultado =
                        detalle.isAceptada() ? verde : rojo;

                escribir(contenido, detalle.getResultado(),
                        320, y - 22, 12, negrita, colorResultado);

                y -= 110;

                escribir(contenido, "DERIVACION",
                        45, y, 12, negrita, azul);

                y -= 25;

                List<String> pasos = detalle.getDerivacion();

                if (pasos.isEmpty()) {
                    escribir(contenido,
                            "No hay pasos de derivacion disponibles.",
                            60, y, 10, normal, gris);
                }

                int pasoNumero = 1;

                for (String paso : pasos) {

                    if (y < 105) {
                        pie(contenido, ancho,
                                documento.getNumberOfPages());
                        contenido.close();

                        pagina = new PDPage(PDRectangle.A4);
                        documento.addPage(pagina);

                        contenido =
                                new PDPageContentStream(documento, pagina);

                        encabezado(contenido, ancho, alto);
                        y = alto - 145;
                    }

                    escribir(contenido,
                            pasoNumero + ".  " + paso,
                            60, y, 10, normal, negro);

                    y -= 22;
                    pasoNumero++;
                }
            }

            pie(contenido, ancho, documento.getNumberOfPages());
            contenido.close();

            documento.save(ruta);
            return true;

        } catch (IOException e) {
            System.out.println("Error al generar PDF: " + e.getMessage());
            return false;
        }
    }

    private void encabezado(PDPageContentStream contenido,
            float ancho, float alto) throws IOException {

        rectangulo(contenido, 0, alto - 105,
                ancho, 105, azul);

        escribir(contenido, "PROYECTO AUTOMATAS",
                45, alto - 43, 20, negrita, Color.WHITE);

        escribir(contenido, "REPORTE TECNICO DE GRAMATICA",
                45, alto - 70, 11, normal, Color.WHITE);

        escribir(contenido,
                "UNIVERSIDAD - INGENIERIA EN SISTEMAS",
                45, alto - 88, 9, normal,
                new Color(205, 220, 238));
    }

    private void pie(PDPageContentStream contenido,
            float ancho, int numeroPagina) throws IOException {

        linea(contenido, 45, 65,
                ancho - 45, 65,
                new Color(210, 218, 228));

        escribir(contenido,
                "Proyecto Automatas y Lenguajes Formales",
                45, 47, 9, normal, gris);

        escribir(contenido, "Pagina " + numeroPagina,
                ancho - 110, 47, 9, normal, gris);
    }

    private void escribir(PDPageContentStream contenido,
            String texto, float x, float y, int tamano,
            PDType1Font fuente, Color color) throws IOException {

        contenido.beginText();
        contenido.setNonStrokingColor(color);
        contenido.setFont(fuente, tamano);
        contenido.newLineAtOffset(x, y);
        contenido.showText(texto == null ? "" : texto);
        contenido.endText();
    }

    private void rectangulo(PDPageContentStream contenido,
            float x, float y, float ancho, float alto,
            Color color) throws IOException {

        contenido.setNonStrokingColor(color);
        contenido.addRect(x, y, ancho, alto);
        contenido.fill();
    }

    private void linea(PDPageContentStream contenido,
            float x1, float y1, float x2, float y2,
            Color color) throws IOException {

        contenido.setStrokingColor(color);
        contenido.setLineWidth(0.7f);
        contenido.moveTo(x1, y1);
        contenido.lineTo(x2, y2);
        contenido.stroke();
    }
}
