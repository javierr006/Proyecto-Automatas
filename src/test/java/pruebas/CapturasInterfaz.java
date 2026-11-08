package pruebas;
import app.*;
import modelo.*;
import vista.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
/** Renderiza las vistas Swing reales y comprueba su navegación interna en modo sin pantalla. */
public class CapturasInterfaz {
    private static void distribuir(Container c) {
        c.doLayout();
        for(Component h:c.getComponents())if(h instanceof Container p)distribuir(p);
    }
    public static void main(String[] args)throws Exception {
        System.setProperty("java.awt.headless","true");
        Path carpeta=Path.of(args[0]);
        Files.createDirectories(carpeta);
        SwingUtilities.invokeAndWait(()-> {
            Tema.instalar(); RepositorioDatos d=new RepositorioDatos(); d.setAfd(PruebasIntegracion.binario()); d.setGramatica(new conversion.AFDaGramatica().convertir(d.getAfd())); d.registrar(new ResultadoEvaluacion("AFD",d.getAfd().getNombre(),"101",true,new afd.EvaluadorAFD().obtenerRecorrido(d.getAfd(),"101"))); Map<String,JPanel> pantallas=new LinkedHashMap<>(); pantallas.put("espacio-trabajo",new EspacioTrabajo(d)); pantallas.put("inicio",VentanaInicio.crearPortada(()-> {
            }
            )); pantallas.put("automatas",new PanelAFD(d)); pantallas.put("gramaticas",new PanelGramatica(d)); pantallas.put("evaluacion",new PanelEvaluacion(d)); pantallas.put("archivos",new PanelArchivos(d)); pantallas.put("reportes",new PanelReportes(d)); pantallas.put("ayuda",new PanelAyuda()); for(var entrada:pantallas.entrySet())for(int ancho:new int[] {
                1320,1100
            }
            ) {
                JPanel p=entrada.getValue(); p.setSize(ancho,745); for(int i=0; i<4; i++)distribuir(p); BufferedImage imagen=new BufferedImage(ancho,745,BufferedImage.TYPE_INT_RGB); Graphics2D g=imagen.createGraphics(); p.printAll(g); g.dispose(); try {
                    ImageIO.write(imagen,"png",carpeta.resolve(entrada.getKey()+"-"+ancho+".png").toFile());
                }
                catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        );
        System.out.println("Capturadas seis vistas Swing a dos anchos.");
    }
}
