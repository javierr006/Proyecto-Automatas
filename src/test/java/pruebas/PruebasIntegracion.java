/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pruebas;
import afd.*;
import app.*;
import archivos.*;
import conversion.*;
import graficos.*;
import modelo.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
/** Regresiones ejecutables con Maven, sin dependencias de pruebas adicionales. @author josecana */
public class PruebasIntegracion {
    private static int verificaciones;
    private static final ServicioGramatica servicio=new ServicioGramatica();
    private static final EvaluadorAFD evaluador=new EvaluadorAFD();
    private static void comprobar(boolean valor,String mensaje) {
        verificaciones++;
        if(!valor)throw new AssertionError(mensaje);
    }
    private static void falla(Runnable accion,String mensaje) {
        try {
            accion.run();
        }
        catch(IllegalArgumentException|IllegalStateException ex) {
            verificaciones++;
            return;
        }
        throw new AssertionError(mensaje);
    }
    public static AFD binario() {
        AFD a=new AFD("Binarias que terminan en 1");
        a.agregarEstado("q0");
        a.agregarEstado("q1");
        a.agregarSimbolo("0");
        a.agregarSimbolo("1");
        a.establecerInicial("q0");
        a.agregarEstadoAceptacion("q1");
        for(String e:a.getEstados()) {
            a.agregarTransicion(e,"0","q0");
            a.agregarTransicion(e,"1","q1");
        }
        return a;
    }
    private static Gramatica gramatica(String nts,String ts,String inicial,String... reglas) {
        Gramatica g=new Gramatica("Prueba ñ");
        for(char c:nts.toCharArray())g.agregarNoTerminal(""+c);
        for(char c:ts.toCharArray())g.agregarTerminal(""+c);
        g.setSimboloInicial(inicial);
        for(String regla:reglas) {
            String[] p=regla.split(">",2);
            servicio.agregarProduccion(g,p[0],p[1]);
        }
        return g;
    }
    private static List<String> cadenas(String alfabeto,int max) {
        List<String> todas=new ArrayList<>();
        todas.add("");
        List<String> nivel=List.of("");
        for(int i=0; i<max; i++) {
            List<String> siguiente=new ArrayList<>();
            for(String prefijo:nivel)for(char c:alfabeto.toCharArray())siguiente.add(prefijo+c);
            todas.addAll(siguiente);
            nivel=siguiente;
        }
        return todas;
    }
    private static void equivalentes(Gramatica g,String alfabeto) {
        AFD a=servicio.convertir(g);
        comprobar(new ValidadorAFD().esValido(a),"Conversión debe generar AFD total");
        for(String c:cadenas(alfabeto,6))comprobar(servicio.derivar(g,c).isPresent()==evaluador.evaluarCadena(a,c),"Diferencia para "+c+" en "+g.getProducciones());
    }
    public static void main(String[] args)throws Exception {
        System.setProperty("java.awt.headless", "true");
        Path carpeta=Files.createTempDirectory("automatas-pruebas-");
        try {
            AFD a=binario();
            comprobar(new ValidadorAFD().esValido(a),"Binario válido");
            comprobar(evaluador.evaluarCadena(a,"101"),"101 aceptada");
            comprobar(!evaluador.evaluarCadena(a,"100"),"100 rechazada");
            comprobar(!evaluador.evaluarCadena(a,""),"Vacía rechazada");
            comprobar(!evaluador.evaluarCadena(a,"2"),"Fuera de alfabeto");
            a.agregarEstadoAceptacion("q0");
            comprobar(evaluador.evaluarCadena(a,""),"Vacía aceptada con inicial final");
            Gramatica convertida=new AFDaGramatica().convertir(a);
            servicio.validar(convertida,true);
            AFD vuelta=servicio.convertir(convertida);
            for(String c:cadenas("01",7)) {
                comprobar(evaluador.evaluarCadena(a,c)==servicio.derivar(convertida,c).isPresent(),"AFD → G "+c);
                comprobar(evaluador.evaluarCadena(a,c)==evaluador.evaluarCadena(vuelta,c),"AFD → G → AFD "+c);
            }
            // Interoperabilidad directa con las clases originales de Diego.
            Gramatica diego=gramatica("S","ab","S","S>aS | b | epsilon");
            AFD original=new GramaticaAAFD().convertir(diego);
            gramatica.EvaluadorGramatica legado=new gramatica.EvaluadorGramatica();
            for(String c:cadenas("ab",4))comprobar(evaluador.evaluarCadena(original,c)==legado.evaluar(diego,c),"Compatibilidad Diego "+c);
            equivalentes(diego,"ab");
            equivalentes(gramatica("SAB","ab","S","S>aA | aB | epsilon","A>b","B>a"),"ab");
            equivalentes(gramatica("SA","ab","S","S>abA | a","A>baS | epsilon"),"ab");
            equivalentes(gramatica("SA","ab","S","S>Aa | b | epsilon","A>Sb | a"),"ab");
            equivalentes(gramatica("SAB","ab","S","S>A","A>B | aA","B>S | epsilon | b"),"ab");
            Gramatica vacia=gramatica("S","","S","S>ε");
            AFD soloVacia=servicio.convertir(vacia);
            comprobar(new ValidadorAFD().esValido(soloVacia),"Alfabeto vacío válido");
            comprobar(evaluador.evaluarCadena(soloVacia,""),"Lenguaje epsilon");
            Gramatica sinReglas=gramatica("S","a","S");
            equivalentes(sinReglas,"a");
            comprobar(!servicio.derivar(sinReglas,"").isPresent(),"Lenguaje vacío");
            Gramatica mezclada=gramatica("SA","ab","S","S>aA");
            int anteriores=mezclada.getProducciones().size();
            falla(()->servicio.agregarProduccion(mezclada,"A","Sb"),"Mezcla rechazada");
            comprobar(anteriores==mezclada.getProducciones().size(),"Operación atómica");
            falla(()->servicio.agregarProduccion(mezclada,"A","a | a"),"Alternativas duplicadas");
            falla(()->servicio.agregarProduccion(mezclada,"A","a | "),"Alternativa vacía");
            falla(()->servicio.agregarProduccion(mezclada,"A","x"),"Símbolo desconocido");
            falla(()->servicio.validarSimbolo("ab"),"Símbolo múltiple");
            falla(()->servicio.validarSimbolo("ε"),"Epsilon no es terminal");
            ControladorAFD ca=new ControladorAFD();
            ca.crearAFD("Prueba");
            ca.agregarEstado("q0");
            falla(()->ca.agregarEstado("q0"),"Duplicado estado");
            falla(()->ca.agregarSimbolo("ab"),"Alfabeto de un carácter");
            ca.agregarSimbolo("0");
            ca.establecerEstadoInicial("q0");
            comprobar(!new ValidadorAFD().esValido(ca.getAfdActual()),"AFD incompleto");
            ca.agregarTransicion("q0","0","q0");
            falla(()->ca.agregarTransicion("q0","0","q0"),"Determinismo");
            Path afd=carpeta.resolve("ejemplo con espacios.afd");
            new EscritorAFD().guardarArchivo(a,afd.toString());
            AFD leido=new LectorAFD().leerArchivo(afd.toString());
            for(String c:cadenas("01",6))comprobar(evaluador.evaluarCadena(a,c)==evaluador.evaluarCadena(leido,c),"AFD roundtrip");
            new EscritorAFD().guardarArchivo(soloVacia,afd.toString());
            comprobar(evaluador.evaluarCadena(new LectorAFD().leerArchivo(afd.toString()),""),"AFD epsilon roundtrip");
            String buenAFD=Files.readString(afd);
            for(String malo:List.of(buenAFD.replace("inicial=q0","inicial=noexiste"),buenAFD.replace("[TRANSICIONES]",""),buenAFD+"q0,1,q0\n",buenAFD.replace("nombre=","extra=x\nnombre="))) {
                Files.writeString(afd,malo);
                try {
                    new LectorAFD().leerArchivo(afd.toString());
                    throw new AssertionError("AFD inválido aceptado");
                }
                catch(java.io.IOException ex) {
                    verificaciones++;
                }
            }
            ServicioArchivos archivos=new ServicioArchivos();
            Path gtk=carpeta.resolve("gramática.gtk");
            Gramatica numerica=gramatica("SA","01","A","S>0S | 1","A>ε | 1S");
            archivos.guardarGTK(numerica,gtk);
            Gramatica carga=archivos.leerGTK(gtk);
            comprobar(carga.getNombre().equals(numerica.getNombre()),"Nombre GTK");
            comprobar(carga.getSimboloInicial().equals("A"),"Inicial GTK");
            comprobar(carga.getTerminales().equals(Set.of("0","1")),"Terminales GTK");
            for(String c:cadenas("01",5))comprobar(servicio.derivar(numerica,c).isPresent()==servicio.derivar(carga,c).isPresent(),"GTK roundtrip "+c);
            Files.writeString(gtk,"S>0S | 1 | ε\n");
            carga=archivos.leerGTK(gtk);
            comprobar(carga.getTerminales().equals(Set.of("0","1")),"GTK legado números");
            comprobar(servicio.derivar(carga,"").isPresent(),"GTK legado epsilon");
            comprobar(servicio.derivar(carga,"001").isPresent(),"GTK alternativas");
            Gramatica unicode=gramatica("Σ","a","Σ","Σ>aΣ | epsilon");
            archivos.guardarGTK(unicode,gtk);
            comprobar(archivos.leerGTK(gtk).getNoTerminales().contains("Σ"),"UTF8 GTK");
            new EscritorGTK().guardar(diego,gtk.toString());
            comprobar(archivos.leerGTK(gtk).getSimboloInicial().equals("S"),"GTK escritor Diego");
            var imagen=new DibujoAFD().dibujar(a);
            Path png=carpeta.resolve("afd.png");
            ImageIO.write(imagen,"png",png.toFile());
            comprobar(ImageIO.read(png.toFile()).getWidth()>=700,"PNG válido");
            String dot=new GeneradorGraphviz().generarDOT(a);
            comprobar(dot.contains("digraph AFD")&&dot.contains("doublecircle"),"DOT inicial y finales");
            AFD especial=new AFD("Comillas \" y salto\n");
            especial.agregarEstado("q\"0");
            especial.establecerInicial("q\"0");
            comprobar(new GeneradorGraphviz().generarDOT(especial).contains("q\\\"0"),"Escape DOT");
            String anterior=System.getProperty("graphviz.dot");
            System.setProperty("graphviz.dot",carpeta.resolve("no-existe-dot").toString());
            Files.writeString(png,"conservar");
            try {
                new GeneradorGraphviz().generarPNG(a,png.toString());
                throw new AssertionError("Graphviz ausente no detectado");
            }
            catch(java.io.IOException ex) {
                comprobar(ex.getMessage().contains("Graphviz"),"Error Graphviz claro");
                comprobar(Files.readString(png).equals("conservar"),"No sobrescribir ante error Graphviz");
            }
            finally {
                if(anterior==null)System.clearProperty("graphviz.dot");
                else System.setProperty("graphviz.dot",anterior);
            }
            List<ResultadoEvaluacion> historial=new ArrayList<>();
            for(int i=0; i<55; i++)historial.add(new ResultadoEvaluacion("Gramática","Nombre muy largo "+"modelo ".repeat(30),"a".repeat(160),true,List.of("Σ → aΣ → ε", "a".repeat(260))));
            Path pdf=carpeta.resolve("reporte.pdf");
            new reportes.ServicioReportes().generar(a,unicode,historial,pdf);
            try(var doc=Loader.loadPDF(pdf.toFile())) {
                String texto=new PDFTextStripper().getText(doc);
                comprobar(doc.getNumberOfPages()>1,"PDF multipágina");
                comprobar(texto.contains("ACEPTADA")&&texto.contains("U+3A3"),"PDF resultados y Unicode");
                comprobar(texto.contains("55" )||texto.contains("HISTORIAL"),"PDF historial");
            }
            RepositorioDatos datos=new RepositorioDatos();
            final int[] eventos= {
                0
            }
            ;
            datos.escuchar(()->eventos[0]++);
            datos.setAfd(a);
            datos.setGramatica(diego);
            datos.marcarGuardado("AFD");
            comprobar(datos.isModificado(),"Guardar AFD no limpia gramática");
            datos.marcarGuardado("Gramática");
            comprobar(!datos.isModificado(),"Ambos guardados");
            datos.registrar(historial.get(0));
            comprobar(eventos[0]>=5&&datos.getHistorial().size()==1,"Eventos e historial");
            SwingUtilities.invokeAndWait(()-> {
                vista.Tema.instalar(); for(JPanel panel:List.of(new vista.PanelAFD(datos),new vista.PanelGramatica(datos),new vista.PanelEvaluacion(datos),new vista.PanelArchivos(datos),new vista.PanelReportes(datos),new vista.PanelAyuda())) {
                    panel.setSize(1100,730); panel.doLayout(); comprobar(panel.getComponentCount()>0,"Panel integrado");
                }
            }
            );
            PruebasInterfaz.ejecutar();
            System.out.println("Pruebas de integración: "+verificaciones+" verificaciones correctas.");
        }
        finally {
            try(Stream<Path> rutas=Files.walk(carpeta)) {
                for(Path p:rutas.sorted(Comparator.reverseOrder()).toList())Files.deleteIfExists(p);
            }
        }
    }
}
