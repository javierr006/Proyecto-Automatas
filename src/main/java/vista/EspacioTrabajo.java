/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;
import app.*;
import modelo.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author josecana
 */
public class EspacioTrabajo extends JPanel {
    private final RepositorioDatos datos;
    private final CardLayout tarjetas=new CardLayout();
    private final JPanel contenido=new JPanel(tarjetas);
    private final Map<String,JButton> botones=new LinkedHashMap<>();
    private final JLabel estado=new JLabel();
    public EspacioTrabajo(RepositorioDatos datos) {
        this.datos=datos;
        construir();
    }
    private void construir() {
        JPanel principal=new JPanel(new BorderLayout());
        principal.setBackground(Tema.FONDO);
        JPanel lateral=new JPanel(new BorderLayout());
        lateral.setPreferredSize(new Dimension(205,0));
        lateral.setBackground(new Color(20,38,69));
        lateral.setBorder(BorderFactory.createEmptyBorder(28,16,22,16));
        JLabel marca=new JLabel("<html><span style='font-size:20px;color:white'>Autómatas</span><br><span style='color:#a8c3ec'>ESTUDIO DE LENGUAJES</span></html>");
        marca.setBorder(BorderFactory.createEmptyBorder(0,7,30,0));
        lateral.add(marca,BorderLayout.NORTH);
        JPanel navegacion=new JPanel();
        navegacion.setOpaque(false);
        navegacion.setLayout(new BoxLayout(navegacion,BoxLayout.Y_AXIS));
        agregar("Autómatas",new PanelAFD(datos),navegacion);
        agregar("Gramáticas",new PanelGramatica(datos),navegacion);
        agregar("Evaluación",new PanelEvaluacion(datos),navegacion);
        agregar("Archivos",new PanelArchivos(datos),navegacion);
        agregar("Reportes",new PanelReportes(datos),navegacion);
        agregar("Ayuda",new PanelAyuda(),navegacion);
        navegacion.add(Box.createVerticalGlue());
        lateral.add(navegacion);
        JLabel autores=new JLabel("<html><span style='color:#b8cae4'>José + Diego<br>Proyecto de Autómatas</span></html>");
        lateral.add(autores,BorderLayout.SOUTH);
        principal.add(lateral,BorderLayout.WEST);
        principal.add(contenido);
        estado.setBorder(BorderFactory.createEmptyBorder(8,225,8,20));
        estado.setForeground(Tema.SUAVE);
        principal.add(estado,BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        add(principal);
        datos.escuchar(this::actualizarEstado);
        actualizarEstado();
        mostrar("Autómatas");
    }
    private void agregar(String nombre,JPanel panel,JPanel nav) {
        contenido.add(panel,nombre);
        JButton b=Tema.boton(nombre,()->mostrar(nombre));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE,45));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        nav.add(b);
        nav.add(Box.createVerticalStrut(9));
        botones.put(nombre,b);
    }
    public void mostrar(String nombre) {
        tarjetas.show(contenido,nombre);
        botones.forEach((n,b)-> {
            boolean activo=n.equals(nombre); b.setBackground(activo?Tema.AZUL:new Color(20,38,69)); b.setForeground(activo?Color.WHITE:new Color(188,207,235)); b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(activo?Tema.AZUL:new Color(20,38,69)),BorderFactory.createEmptyBorder(10,14,10,14)));
        }
        );
    }
    private void actualizarEstado() {
        estado.setText((datos.isModificado()?"● Cambios sin guardar":"● Modelos guardados")+"    |    AFD: "+(datos.getAfd()==null?"Sin cargar":datos.getAfd().getNombre())+"    |    Gramática: "+(datos.getGramatica()==null?"Sin cargar":datos.getGramatica().getNombre()));
    }
}
