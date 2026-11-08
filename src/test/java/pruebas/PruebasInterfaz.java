package pruebas;
import app.RepositorioDatos;
import java.awt.*;
import java.util.concurrent.*;
import javax.swing.*;
import vista.*;
/** Acciona los componentes reales de Swing y verifica el flujo compartido. @author josecana */
public class PruebasInterfaz {
    private static <T extends Component> T buscar(Container raiz,Class<T> tipo,String nombre) {
        for(Component c:raiz.getComponents()) {
            if(tipo.isInstance(c)&&nombre.equals(c.getName()))return tipo.cast(c);
            if(c instanceof Container hijo) {
                T hallado=buscar(hijo,tipo,nombre);
                if(hallado!=null)return hallado;
            }
        }
        return null;
    }
    private static JComboBox<?> selector(Container raiz) {
        for(Component c:raiz.getComponents()) {
            if(c instanceof JComboBox<?> combo&&combo.getItemCount()==2&&"Gramática".equals(combo.getItemAt(1)))return combo;
            if(c instanceof Container hijo) {
                JComboBox<?> r=selector(hijo);
                if(r!=null)return r;
            }
        }
        return null;
    }
    public static void ejecutar()throws Exception {
        RepositorioDatos d=new RepositorioDatos();
        EspacioTrabajo[] espacio= {
            null
        }
        ;
        SwingUtilities.invokeAndWait(()-> {
            espacio[0]=new EspacioTrabajo(d); buscar(espacio[0],JButton.class,"Ejemplo binario").doClick();
        }
        );
        if(d.getAfd()==null)throw new AssertionError("Botón ejemplo AFD");
        SwingUtilities.invokeAndWait(()-> {
            buscar(espacio[0],JButton.class,"AFD → Gramática").doClick(); buscar(espacio[0],JButton.class,"Gramáticas").doClick();
        }
        );
        if(d.getGramatica()==null)throw new AssertionError("Conversión desde editor");
        evaluar(d,espacio[0],"101",false,true);
        evaluar(d,espacio[0],"100",false,false);
        evaluar(d,espacio[0],"101",true,true);
        evaluar(d,espacio[0],"",true,false);
        SwingUtilities.invokeAndWait(()-> {
            for(String nombre:new String[] {
                "Autómatas","Gramáticas","Evaluación","Archivos","Reportes","Ayuda"
            }
            )buscar(espacio[0],JButton.class,nombre).doClick();
        }
        );
        if(d.getHistorial().size()!=4)throw new AssertionError("Historial compartido");
        System.out.println("Interfaz: navegación, ejemplo, conversión y cuatro evaluaciones correctas.");
    }
    private static void evaluar(RepositorioDatos d,EspacioTrabajo espacio,String cadena,boolean gramatica,boolean esperada)throws Exception {
        int previo=d.getHistorial().size();
        CountDownLatch listo=new CountDownLatch(1);
        d.escuchar(()-> {
            if(d.getHistorial().size()>previo)listo.countDown();
        }
        );
        SwingUtilities.invokeAndWait(()-> {
            buscar(espacio,JButton.class,"Evaluación").doClick(); selector(espacio).setSelectedIndex(gramatica?1:0); buscar(espacio,JTextField.class,"Cadena a evaluar").setText(cadena); buscar(espacio,JButton.class,"Evaluar cadena").doClick();
        }
        );
        if(!listo.await(10,TimeUnit.SECONDS))throw new AssertionError("La evaluación de interfaz no terminó");
        if(d.getHistorial().get(previo).isAceptada()!=esperada)throw new AssertionError("Resultado desde interfaz "+cadena);
    }
}
