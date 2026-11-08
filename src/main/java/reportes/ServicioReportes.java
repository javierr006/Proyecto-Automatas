/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import modelo.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
/** Reportes paginados para ambos módulos, sin alterar el generador de Diego. @author josecana */
public class ServicioReportes {
    private final PDType1Font fuente=new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    public void generar(AFD afd,Gramatica g,List<ResultadoEvaluacion> historial,Path destino) throws IOException {
        if(afd==null && g==null && historial.isEmpty())throw new IllegalArgumentException("No hay datos para exportar.");
        List<String> lineas=new ArrayList<>();
        lineas.add("RESUMEN DE LA SESION");
        lineas.add("");
        if(afd!=null) {
            lineas.add("AUTOMATA: "+afd.getNombre());
            lineas.add("Estados: "+afd.getEstados());
            lineas.add("Alfabeto: "+afd.getAlfabeto());
            lineas.add("Inicial: "+afd.getEstadoInicial()+" | Aceptación: "+afd.getEstadosAceptacion());
            afd.getTransiciones().forEach(t->lineas.add(t.getOrigen()+" --"+t.getSimbolo()+"--> "+t.getDestino()));
            lineas.add("");
        }
        if(g!=null) {
            lineas.add("GRAMATICA: "+g.getNombre());
            lineas.add("No terminales: "+g.getNoTerminales());
            lineas.add("Terminales: "+g.getTerminales());
            lineas.add("Inicial: "+g.getSimboloInicial());
            g.getProducciones().forEach(p->lineas.add(p.toString()));
            lineas.add("");
        }
        lineas.add("HISTORIAL DE EVALUACIONES (datos al momento de cada evaluación)");
        if(historial.isEmpty())lineas.add("Todavía no se han evaluado cadenas.");
        for(ResultadoEvaluacion r:historial) {
            DetalleReporte d=new DetalleReporte(r.getNombre(),r.getCadena(),r.isAceptada(),r.getPasos());
            lineas.add(r.getFecha().toString()+" | "+r.getTipo()+" | "+d.getNombreGramatica());
            lineas.add("Cadena: "+(d.getCadena().isEmpty()?"epsilon":d.getCadena())+" | "+d.getResultado());
            d.getDerivacion().forEach(lineas::add);
            lineas.add("");
        }
        Path absoluto=destino.toAbsolutePath(),temp=Files.createTempFile(absoluto.getParent(),"reporte-",".pdf");
        try(PDDocument doc=new PDDocument()) {
            List<String> ajustadas=new ArrayList<>();
            for(String linea:lineas)ajustadas.addAll(ajustar(seguro(linea)));
            int porPagina=42,paginas=Math.max(1,(ajustadas.size()+porPagina-1)/porPagina);
            for(int pagina=0; pagina<paginas; pagina++) {
                PDPage hoja=new PDPage(PDRectangle.A4);
                doc.addPage(hoja);
                try(PDPageContentStream c=new PDPageContentStream(doc,hoja)) {
                    c.setNonStrokingColor(new Color(20,38,69));
                    c.addRect(0,745,596,97);
                    c.fill();
                    texto(c,"PROYECTO AUTOMATAS",44,797,19,Color.WHITE);
                    texto(c,"AFD / Gramáticas / Evaluaciones",44,773,11,new Color(182,210,255));
                    float y=716;
                    for(int i=pagina*porPagina; i<Math.min(ajustadas.size(),(pagina+1)*porPagina); i++) {
                        texto(c,ajustadas.get(i),44,y,10,new Color(38,51,70));
                        y-=15;
                    }
                    texto(c,"José y Diego  |  Página "+(pagina+1)+" de "+paginas,44,44,9,new Color(90,106,125));
                }
            }
            doc.save(temp.toFile());
            ServicioMover.mover(temp,absoluto);
        }
        finally {
            Files.deleteIfExists(temp);
        }
    }
    private void texto(PDPageContentStream c,String s,float x,float y,int tamano,Color color)throws IOException {
        c.beginText();
        c.setFont(fuente,tamano);
        c.setNonStrokingColor(color);
        c.newLineAtOffset(x,y);
        c.showText(s);
        c.endText();
    }
    private String seguro(String s) {
        StringBuilder b=new StringBuilder();
        for(int cp:s.replace("ε","epsilon").replace("→","->").codePoints().toArray()) {
            String letra=new String(Character.toChars(cp));
            if(Character.isISOControl(cp)) {
                b.append(' ');
                continue;
            }
            try {
                fuente.encode(letra);
                b.append(letra);
            }
            catch(IOException|IllegalArgumentException ex) {
                b.append("[U+").append(Integer.toHexString(cp).toUpperCase(Locale.ROOT)).append(']');
            }
        }
        return b.toString();
    }
    private List<String> ajustar(String s)throws IOException {
        List<String> r=new ArrayList<>();
        StringBuilder linea=new StringBuilder();
        for(char c:s.toCharArray()) {
            if(fuente.getStringWidth(linea.toString()+c)/1000*10>505) {
                r.add(linea.toString());
                linea.setLength(0);
            }
            linea.append(c);
        }
        r.add(linea.toString());
        return r;
    }
    private static class ServicioMover {
        static void mover(Path a,Path b)throws IOException {
            app.ServicioArchivos.reemplazar(a,b);
        }
    }
}
