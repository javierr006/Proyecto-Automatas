/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
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
public class PanelEvaluacion extends javax.swing.JPanel {
    private final RepositorioDatos datos;
    private final JComboBox<String> tipo=new JComboBox<>(new String[] {
        "AFD","Gramática"
    }
    );
    private final JTextField cadena=Tema.campo("Cadena a evaluar",24);
    private final JTextArea pasos=Tema.area();
    private final JLabel resultado=Tema.titulo("Listo para evaluar"), actual=Tema.subtitulo("");
    private final DefaultTableModel historial=Tema.modelo("Tipo","Modelo","Cadena","Resultado");
    private JButton evaluar;
    public PanelEvaluacion() {
        this(new RepositorioDatos());
    }
    public PanelEvaluacion(RepositorioDatos datos) {
        this.datos=datos;
        initComponents();
        construir();
        datos.escuchar(this::actualizar);
        actualizar();
    }
    private void construir() {
        Tema.preparar(this);
        add(Tema.cabecera("Evaluación de cadenas","Explore el recorrido del autómata o la derivación de su gramática."),BorderLayout.NORTH);
        JPanel principal=new JPanel(new BorderLayout(0,14)),entrada=Tema.tarjeta("01  ·  Prepare la evaluación"),campos=Tema.columna();
        evaluar=Tema.boton("Evaluar cadena",()->Tema.ejecutar(this,this::evaluar));
        cadena.addActionListener(e-> {
            if(evaluar.isEnabled())evaluar.doClick();
        }
        );
        campos.add(Tema.fila(new JLabel("Evaluar con"),tipo,new JLabel("Cadena"),cadena,evaluar));
        campos.add(Tema.fila(actual));
        campos.add(Tema.fila(Tema.subtitulo("Deje el campo vacío para evaluar ε. Los espacios se consideran símbolos.")));
        entrada.add(campos);
        entrada.setMaximumSize(new Dimension(Integer.MAX_VALUE,170));
        principal.add(entrada,BorderLayout.NORTH);
        JPanel salida=Tema.tarjeta(null);
        salida.add(resultado,BorderLayout.NORTH);
        salida.add(new JScrollPane(pasos));
        JPanel h=Tema.tarjeta("Historial de esta sesión");
        JTable t=Tema.tabla(historial);
        h.add(Tema.desplazar(t));
        h.setPreferredSize(new Dimension(0,190));
        t.getSelectionModel().addListSelectionListener(e-> {
            if(!e.getValueIsAdjusting()) {
                int i=t.getSelectedRow(); if(i>=0&&i<datos.getHistorial().size())mostrar(datos.getHistorial().get(i));
            }
        }
        );
        JSplitPane split=new JSplitPane(JSplitPane.VERTICAL_SPLIT,salida,h);
        split.setResizeWeight(.7);
        split.setBorder(null);
        principal.add(split,BorderLayout.CENTER);
        add(principal);
        tipo.addActionListener(e->actualizarModelo());
    }
    private void evaluar() {
        String texto=cadena.getText();
        if(texto.length()>2000)throw new IllegalArgumentException("Use cadenas de hasta 2000 caracteres.");
        boolean usarAFD=tipo.getSelectedIndex()==0;
        AFD a=Copias.afd(datos.getAfd());
        Gramatica g=datos.getGramatica()==null?null:new ServicioGramatica().copiar(datos.getGramatica());
        if(usarAFD) {
            List<String> errores=new afd.ValidadorAFD().obtenerErrores(a);
            if(!errores.isEmpty())throw new IllegalArgumentException(String.join("\n",errores));
        }
        else new ServicioGramatica().validar(g,true);
        final ResultadoEvaluacion[] salida=new ResultadoEvaluacion[1];
        resultado.setText("Evaluando…");
        resultado.setForeground(Tema.SUAVE);
        ArchivosUI.trabajar(this,evaluar,()-> {
            boolean aceptada; List<String> recorrido; if(usarAFD) {
                afd.EvaluadorAFD e=new afd.EvaluadorAFD(); aceptada=e.evaluarCadena(a,texto); recorrido=e.obtenerRecorrido(a,texto);
            }
            else {
                var d=new ServicioGramatica().derivar(g,texto); aceptada=d.isPresent(); recorrido=d.orElse(List.of("No existe una derivación para esta cadena."));
            }
            salida[0]=new ResultadoEvaluacion(usarAFD?"AFD":"Gramática",usarAFD?a.getNombre():g.getNombre(),texto,aceptada,recorrido);
        }
        ,()-> {
            datos.registrar(salida[0]); mostrar(salida[0]);
        }
        );
    }
    private void mostrar(ResultadoEvaluacion r) {
        resultado.setText(r.isAceptada()?"✓  Cadena aceptada":"×  Cadena rechazada");
        resultado.setForeground(r.isAceptada()?Tema.VERDE:new Color(187,62,73));
        pasos.setText(r.getTipo()+" · "+r.getNombre()+"\nCadena: "+(r.getCadena().isEmpty()?"ε":r.getCadena())+"\n\n"+String.join("\n",r.getPasos()));
        pasos.setCaretPosition(0);
    }
    private void actualizarModelo() {
        boolean a=tipo.getSelectedIndex()==0;
        actual.setText(a?(datos.getAfd()==null?"No hay AFD cargado.":"AFD: "+datos.getAfd().getNombre()):(datos.getGramatica()==null?"No hay gramática cargada.":"Gramática: "+datos.getGramatica().getNombre()));
    }
    private void actualizar() {
        actualizarModelo();
        historial.setRowCount(0);
        for(ResultadoEvaluacion r:datos.getHistorial())historial.addRow(new Object[] {
            r.getTipo(),r.getNombre(),r.getCadena().isEmpty()?"ε":r.getCadena(),r.isAceptada()?"Aceptada":"Rechazada"
        }
        );
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
