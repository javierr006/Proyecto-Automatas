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
public class PanelAFD extends javax.swing.JPanel {
    private final RepositorioDatos datos;
    private final afd.ControladorAFD controlador=new afd.ControladorAFD();
    private final JTextField nombre=Tema.campo("Nombre AFD",16), estado=Tema.campo("Nuevo estado",6), simbolo=Tema.campo("Nuevo símbolo",5);
    private final JComboBox<String> inicial=new JComboBox<>(), aceptacion=new JComboBox<>(), origen=new JComboBox<>(), letra=new JComboBox<>(), destino=new JComboBox<>();
    private final DefaultTableModel modelo=Tema.modelo("Origen","Símbolo","Destino");
    private final JTable tabla=Tema.tabla(modelo);
    private final JTextArea resumen=Tema.area(), dot=Tema.area();
    private final JLabel imagen=new JLabel("Cree un AFD y pulse Actualizar gráfico",SwingConstants.CENTER);
    private final JLabel aviso=Tema.subtitulo("Empiece creando un autómata.");
    public PanelAFD() {
        this(new RepositorioDatos());
    }
    public PanelAFD(RepositorioDatos datos) {
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
        arriba.add(Tema.cabecera("Autómatas finitos","Defina estados, construya transiciones y convierta su AFD en una gramática."),BorderLayout.NORTH);
        arriba.add(Tema.fila(new JLabel("Nombre"),nombre,Tema.boton("Crear AFD",()->Tema.ejecutar(this,this::crear)),Tema.secundario("Ejemplo binario",this::ejemplo),Tema.secundario("Abrir .afd",()->new PanelArchivos(datos,false).abrirAFD())),BorderLayout.CENTER);
        arriba.add(aviso,BorderLayout.SOUTH);
        add(arriba,BorderLayout.NORTH);
        JTabbedPane tabs=new JTabbedPane();
        tabs.addTab("  Editor  ",editor());
        tabs.addTab("  Gráfico y DOT  ",graficos());
        add(tabs,BorderLayout.CENTER);
        add(Tema.fila(Tema.boton("Validar AFD",()->Tema.ejecutar(this,this::validar)),Tema.secundario("AFD → Gramática",()->Tema.ejecutar(this,this::convertir)),Tema.secundario("Guardar .afd",()->new PanelArchivos(datos,false).guardarAFD())),BorderLayout.SOUTH);
    }
    private JPanel editor() {
        JPanel formulario=Tema.columna();
        formulario.add(Tema.fila(new JLabel("Estado"),estado,Tema.boton("Agregar estado",()->cambiar(()->controlador.agregarEstado(estado.getText())))));
        formulario.add(Tema.fila(new JLabel("Símbolo"),simbolo,Tema.boton("Agregar símbolo",()->cambiar(()->controlador.agregarSimbolo(simbolo.getText())))));
        formulario.add(Tema.fila(new JLabel("Inicial"),inicial,Tema.secundario("Establecer inicial",()->cambiar(()->controlador.establecerEstadoInicial(seleccion(inicial))))));
        formulario.add(Tema.fila(new JLabel("Aceptación"),aceptacion,Tema.secundario("Marcar / desmarcar",()->cambiar(()-> {
            String e=seleccion(aceptacion); if(!obtener().getEstadosAceptacion().remove(e))controlador.agregarEstadoAceptacion(e);
        }
        ))));
        formulario.add(Tema.fila(Tema.secundario("Eliminar estado",()->cambiar(()->eliminarEstado())),Tema.secundario("Eliminar símbolo",()->cambiar(()->eliminarSimbolo()))));
        JPanel izquierda=Tema.tarjeta("01  ·  Estados y alfabeto");
        izquierda.add(formulario,BorderLayout.NORTH);
        izquierda.add(new JScrollPane(resumen),BorderLayout.CENTER);
        JPanel derecha=Tema.tarjeta("02  ·  Transiciones");
        JPanel trans=Tema.columna();
        trans.add(Tema.fila(new JLabel("Origen"),origen,new JLabel("Símbolo"),letra,new JLabel("Destino"),destino));
        trans.add(Tema.fila(Tema.boton("Agregar transición",()->cambiar(()->controlador.agregarTransicion(seleccion(origen),seleccion(letra),seleccion(destino)))),Tema.secundario("Eliminar transición",()->cambiar(()-> {
            int i=tabla.getSelectedRow(); if(i<0)throw new IllegalArgumentException("Seleccione una transición."); obtener().getTransiciones().remove(i);
        }
        ))));
        derecha.add(trans,BorderLayout.NORTH);
        derecha.add(Tema.desplazar(tabla),BorderLayout.CENTER);
        JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,izquierda,derecha);
        split.setResizeWeight(.48);
        split.setBorder(null);
        split.setDividerLocation(400);
        izquierda.setMinimumSize(new Dimension(380,200));
        derecha.setMinimumSize(new Dimension(350,200));
        JPanel p=new JPanel(new BorderLayout());
        p.add(split);
        return p;
    }
    private JPanel graficos() {
        JPanel p=new JPanel(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createEmptyBorder(14,12,12,12));
        JTabbedPane visual=new JTabbedPane();
        visual.addTab("Vista previa",new JScrollPane(imagen));
        visual.addTab("Código DOT",new JScrollPane(dot));
        p.add(Tema.fila(Tema.boton("Actualizar gráfico",()->Tema.ejecutar(this,()-> {
            AFD a=obtener(); imagen.setText(""); imagen.setIcon(new ImageIcon(new graficos.DibujoAFD().dibujar(a))); dot.setText(new graficos.GeneradorGraphviz().generarDOT(a));
        }
        )),Tema.secundario("Exportar DOT",()->exportar("dot",false)),Tema.secundario("Exportar PNG",()->exportar("png",false)),Tema.secundario("PNG con Graphviz",()->exportar("png",true))),BorderLayout.NORTH);
        p.add(visual);
        return p;
    }
    private void exportar(String extension,boolean graphviz) {
        Tema.ejecutar(this,()-> {
            AFD a=Copias.afd(obtener()); Path ruta=ArchivosUI.elegir(this,true,extension); if(ruta==null)return; JButton b=new JButton(); ArchivosUI.trabajar(this,b,()-> {
                if(extension.equals("dot"))new graficos.GeneradorGraphviz().guardarDOT(a,ruta.toString()); else if(graphviz)new graficos.GeneradorGraphviz().generarPNG(a,ruta.toString()); else {
                    Path temp=Files.createTempFile(ruta.toAbsolutePath().getParent(),"afd-",".png"); try {
                        javax.imageio.ImageIO.write(new graficos.DibujoAFD().dibujar(a),"png",temp.toFile()); ServicioArchivos.reemplazar(temp,ruta.toAbsolutePath());
                    }
                    finally {
                        Files.deleteIfExists(temp);
                    }
                }
            }
            ,()->aviso.setText("Exportado: "+ruta.getFileName()));
        }
        );
    }
    private String seleccion(JComboBox<String> c) {
        String s=(String)c.getSelectedItem();
        if(s==null)throw new IllegalArgumentException("Agregue estados y símbolos antes de continuar.");
        return s;
    }
    private AFD obtener() {
        if(datos.getAfd()==null)throw new IllegalStateException("Primero cree o abra un AFD.");
        return datos.getAfd();
    }
    private void cambiar(Runnable r) {
        Tema.ejecutar(this,()-> {
            controlador.cargarAFD(obtener()); r.run(); datos.actualizarAFD();
        }
        );
    }
    private void crear() {
        if(datos.getAfd()!=null&&!Tema.confirmar(this,"¿Reemplazar el AFD actual? Guárdelo antes si desea conservarlo."))return;
        datos.setAfd(controlador.crearAFD(nombre.getText()));
    }
    private void ejemplo() {
        if(datos.getAfd()!=null&&!Tema.confirmar(this,"¿Reemplazar el AFD actual con el ejemplo?"))return;
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
        datos.setAfd(a);
        nombre.setText(a.getNombre());
    }
    private void validar() {
        List<String> errores=new afd.ValidadorAFD().obtenerErrores(obtener());
        JOptionPane.showMessageDialog(this,errores.isEmpty()?"AFD válido: todas las transiciones están definidas.":String.join("\n",errores),"Validación AFD",errores.isEmpty()?JOptionPane.INFORMATION_MESSAGE:JOptionPane.WARNING_MESSAGE);
    }
    private void convertir() {
        validarCompleto();
        if(datos.getGramatica()!=null&&!Tema.confirmar(this,"¿Reemplazar la gramática actual con la conversión?"))return;
        datos.setGramatica(new conversion.AFDaGramatica().convertir(obtener()));
        aviso.setText("Gramática creada. Ábrala desde el menú lateral.");
    }
    private void validarCompleto() {
        List<String> e=new afd.ValidadorAFD().obtenerErrores(obtener());
        if(!e.isEmpty())throw new IllegalArgumentException(String.join("\n",e));
    }
    private void eliminarEstado() {
        String e=seleccion(inicial);
        if(!Tema.confirmar(this,"¿Eliminar "+e+" y sus transiciones?"))return;
        AFD a=obtener();
        if(e.equals(a.getEstadoInicial())) {
            AFD c=new AFD(a.getNombre());
            a.getEstados().stream().filter(x->!x.equals(e)).forEach(c::agregarEstado);
            a.getAlfabeto().forEach(c::agregarSimbolo);
            a.getEstadosAceptacion().stream().filter(x->!x.equals(e)).forEach(c::agregarEstadoAceptacion);
            a.getTransiciones().stream().filter(t->!t.getOrigen().equals(e)&&!t.getDestino().equals(e)).forEach(t->c.agregarTransicion(t.getOrigen(),t.getSimbolo(),t.getDestino()));
            datos.setAfd(c);
            return;
        }
        a.getEstados().remove(e);
        a.getEstadosAceptacion().remove(e);
        a.getTransiciones().removeIf(t->t.getOrigen().equals(e)||t.getDestino().equals(e));
    }
    private void eliminarSimbolo() {
        String s=seleccion(letra);
        if(!Tema.confirmar(this,"¿Eliminar el símbolo "+s+" y sus transiciones?"))return;
        obtener().getAlfabeto().remove(s);
        obtener().getTransiciones().removeIf(t->t.getSimbolo().equals(s));
    }
    private void llenar(JComboBox<String> c,Collection<String> opciones) {
        Object previo=c.getSelectedItem();
        c.removeAllItems();
        opciones.forEach(c::addItem);
        if(previo!=null&&opciones.contains(previo))c.setSelectedItem(previo);
    }
    private void actualizar() {
        AFD a=datos.getAfd();
        modelo.setRowCount(0);
        Collection<String> estados=a==null?List.of():a.getEstados();
        for(JComboBox<String> c:List.of(inicial,aceptacion,origen,destino))llenar(c,estados);
        llenar(letra,a==null?List.of():a.getAlfabeto());
        imagen.setIcon(null);
        imagen.setText("Pulse Actualizar gráfico para ver la versión actual.");
        dot.setText("");
        if(a==null) {
            resumen.setText("Sin autómata. Cree uno o cargue el ejemplo.");
            return;
        }
        a.getTransiciones().forEach(t->modelo.addRow(new Object[] {
            t.getOrigen(),t.getSimbolo(),t.getDestino()
        }
        ));
        resumen.setText("Nombre: "+a.getNombre()+"\n\nEstados: "+a.getEstados()+"\nAlfabeto: "+a.getAlfabeto()+"\nInicial: "+a.getEstadoInicial()+"\nAceptación: "+a.getEstadosAceptacion());
        resumen.setLineWrap(true);
        resumen.setWrapStyleWord(true);
        aviso.setText(a.getEstados().size()+" estados  ·  "+a.getTransiciones().size()+" transiciones");
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
