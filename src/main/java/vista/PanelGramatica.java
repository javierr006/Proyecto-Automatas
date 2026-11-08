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
public class PanelGramatica extends javax.swing.JPanel {
    private final RepositorioDatos datos;
    private final ServicioGramatica servicio=new ServicioGramatica();
    private final gramatica.ControladorGramatica controlador=new gramatica.ControladorGramatica();
    private final JTextField nombre=Tema.campo("Nombre gramática",16), nt=Tema.campo("No terminal",3), terminal=Tema.campo("Terminal",5), derecha=Tema.campo("Producción derecha",17);
    private final JComboBox<String> inicial=new JComboBox<>(), izquierda=new JComboBox<>();
    private final DefaultTableModel modelo=Tema.modelo("No terminal","Producción");
    private final JTable tabla=Tema.tabla(modelo);
    private final JTextArea resumen=Tema.area();
    private final JLabel aviso=Tema.subtitulo("Cree una gramática o cargue el ejemplo.");
    public PanelGramatica() {
        this(new RepositorioDatos());
    }
    public PanelGramatica(RepositorioDatos datos) {
        this.datos=datos;
        initComponents();
        construir();
        datos.escuchar(this::actualizar);
        actualizar();
    }
    private void construir() {
        Tema.preparar(this);
        JPanel arriba=new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(Tema.cabecera("Gramáticas regulares","Producciones por la derecha o por la izquierda, alternativas y cadena vacía."),BorderLayout.NORTH);
        arriba.add(Tema.fila(new JLabel("Nombre"),nombre,Tema.boton("Crear gramática",()->Tema.ejecutar(this,this::crear)),Tema.secundario("Ejemplo a*b",this::ejemplo),Tema.secundario("Abrir .gtk",()->new PanelArchivos(datos,false).abrirGTK())),BorderLayout.CENTER);
        arriba.add(aviso,BorderLayout.SOUTH);
        add(arriba,BorderLayout.NORTH);
        JPanel izq=Tema.tarjeta("01  ·  Símbolos de la gramática"),form=Tema.columna();
        form.add(Tema.fila(new JLabel("No terminal"),nt,Tema.boton("Agregar NT",()->cambiar(()-> {
            String s=nt.getText().trim(); servicio.validarSimbolo(s); controlador.agregarNoTerminal(obtener(),s);
        }
        ))));
        form.add(Tema.fila(new JLabel("Terminal"),terminal,Tema.boton("Agregar terminal",()->cambiar(()-> {
            String s=terminal.getText().trim(); servicio.validarSimbolo(s); controlador.agregarTerminal(obtener(),s);
        }
        ))));
        form.add(Tema.fila(new JLabel("Inicial"),inicial,Tema.secundario("Establecer símbolo",()->cambiar(()->controlador.establecerInicial(obtener(),seleccion(inicial))))));
        form.add(Tema.fila(Tema.secundario("Eliminar NT",()->cambiar(()->eliminar(true))),Tema.secundario("Eliminar terminal",()->cambiar(()->eliminar(false)))));
        izq.add(form,BorderLayout.NORTH);
        izq.add(new JScrollPane(resumen));
        JPanel der=Tema.tarjeta("02  ·  Producciones");
        JPanel reglas=Tema.columna();
        reglas.add(Tema.fila(izquierda,new JLabel("→"),derecha));
        reglas.add(Tema.fila(Tema.boton("Agregar producción",()->cambiar(()->servicio.agregarProduccion(obtener(),seleccion(izquierda),derecha.getText()))),Tema.secundario("Eliminar producción",()->cambiar(()-> {
            int i=tabla.getSelectedRow(); if(i<0)throw new IllegalArgumentException("Seleccione una producción."); obtener().getProducciones().remove(i);
        }
        ))));
        reglas.add(Tema.fila(Tema.subtitulo("Ejemplo: aS | b | epsilon. También puede escribir ε.")));
        der.add(reglas,BorderLayout.NORTH);
        der.add(Tema.desplazar(tabla));
        JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,izq,der);
        split.setResizeWeight(.48);
        split.setDividerLocation(400);
        split.setBorder(null);
        izq.setMinimumSize(new Dimension(380,200));
        der.setMinimumSize(new Dimension(350,200));
        add(split);
        add(Tema.fila(Tema.boton("Validar gramática",()->Tema.ejecutar(this,()-> {
            servicio.validar(obtener(),true); aviso.setText("Gramática regular válida.");
        }
        )),Tema.secundario("Gramática → AFD",()->Tema.ejecutar(this,this::convertir)),Tema.secundario("Guardar .gtk",()->new PanelArchivos(datos,false).guardarGTK())),BorderLayout.SOUTH);
    }
    private Gramatica obtener() {
        if(datos.getGramatica()==null)throw new IllegalStateException("Primero cree o abra una gramática.");
        return datos.getGramatica();
    }
    private String seleccion(JComboBox<String> c) {
        String s=(String)c.getSelectedItem();
        if(s==null)throw new IllegalArgumentException("Agregue un no terminal.");
        return s;
    }
    private void cambiar(Runnable accion) {
        Tema.ejecutar(this,()-> {
            obtener(); accion.run(); datos.actualizarGramatica();
        }
        );
    }
    private void crear() {
        if(datos.getGramatica()!=null&&!Tema.confirmar(this,"¿Reemplazar la gramática actual? Guárdela antes si desea conservarla."))return;
        datos.setGramatica(controlador.crearGramatica(nombre.getText().trim()));
    }
    private void ejemplo() {
        if(datos.getGramatica()!=null&&!Tema.confirmar(this,"¿Reemplazar la gramática actual con el ejemplo?"))return;
        Gramatica g=controlador.crearGramatica("Cero o más a seguidas de b");
        g.agregarNoTerminal("S");
        g.agregarTerminal("a");
        g.agregarTerminal("b");
        g.setSimboloInicial("S");
        servicio.agregarProduccion(g,"S","aS | b");
        datos.setGramatica(g);
        nombre.setText(g.getNombre());
    }
    private void convertir() {
        Gramatica copia=servicio.copiar(obtener());
        if(datos.getAfd()!=null&&!Tema.confirmar(this,"¿Reemplazar el AFD actual con la conversión?"))return;
        JButton b=new JButton();
        final AFD[] resultado=new AFD[1];
        ArchivosUI.trabajar(this,b,()->resultado[0]=servicio.convertir(copia),()-> {
            datos.setAfd(resultado[0]); aviso.setText("AFD creado. Ábralo desde el menú lateral.");
        }
        );
    }
    private void eliminar(boolean noTerminal) {
        Gramatica g=obtener();
        String s=noTerminal?seleccion(inicial):terminal.getText().trim();
        if(!(noTerminal?g.getNoTerminales():g.getTerminales()).contains(s))throw new IllegalArgumentException("Escriba un terminal existente para eliminarlo.");
        if(!Tema.confirmar(this,"¿Eliminar "+s+" y las producciones relacionadas?"))return;
        Gramatica c=new Gramatica(g.getNombre());
        g.getNoTerminales().stream().filter(x->!noTerminal||!x.equals(s)).forEach(c::agregarNoTerminal);
        g.getTerminales().stream().filter(x->noTerminal||!x.equals(s)).forEach(c::agregarTerminal);
        if(g.getSimboloInicial()!=null&&c.getNoTerminales().contains(g.getSimboloInicial()))c.setSimboloInicial(g.getSimboloInicial());
        g.getProducciones().stream().filter(p->!p.getIzquierda().equals(s)&&(p.getDerecha().equals("epsilon")||!p.getDerecha().contains(s))).forEach(c::agregarProduccion);
        datos.setGramatica(c);
    }
    private void actualizar() {
        modelo.setRowCount(0);
        Gramatica g=datos.getGramatica();
        Object previo=izquierda.getSelectedItem();
        inicial.removeAllItems();
        izquierda.removeAllItems();
        if(g==null) {
            resumen.setText("Sin gramática.");
            return;
        }
        g.getNoTerminales().forEach(s-> {
            inicial.addItem(s); izquierda.addItem(s);
        }
        );
        if(g.getNoTerminales().contains(previo))izquierda.setSelectedItem(previo);
        inicial.setSelectedItem(g.getSimboloInicial());
        g.getProducciones().forEach(p->modelo.addRow(new Object[] {
            p.getIzquierda(),p.getDerecha()
        }
        ));
        resumen.setText("Nombre: "+g.getNombre()+"\n\nNo terminales: "+g.getNoTerminales()+"\nTerminales: "+g.getTerminales()+"\nInicial: "+g.getSimboloInicial());
        resumen.setLineWrap(true);
        resumen.setWrapStyleWord(true);
        aviso.setText(g.getNoTerminales().size()+" no terminales  ·  "+g.getProducciones().size()+" producciones");
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
