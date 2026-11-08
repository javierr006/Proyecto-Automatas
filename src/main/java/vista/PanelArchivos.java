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
public class PanelArchivos extends javax.swing.JPanel {
    private final RepositorioDatos datos;
    private final JLabel estado=Tema.subtitulo("Los archivos se guardan en UTF-8.");
    private final JTextArea resumen=Tema.area();
    public PanelArchivos() {
        this(new RepositorioDatos());
    }
    public PanelArchivos(RepositorioDatos datos) {
        this(datos,true);
    }
    public PanelArchivos(RepositorioDatos datos,boolean observar) {
        this.datos=datos;
        initComponents();
        construir();
        if(observar)datos.escuchar(this::actualizar);
        actualizar();
    }
    private void construir() {
        Tema.preparar(this);
        add(Tema.cabecera("Archivos del proyecto","Guarde su trabajo, cargue modelos existentes y conserve todos sus símbolos."),BorderLayout.NORTH);
        JPanel p=new JPanel(new BorderLayout(15,15));
        p.setOpaque(false);
        JPanel tarjetas=new JPanel(new GridLayout(1,2,16,0));
        tarjetas.setOpaque(false);
        JPanel afd=Tema.tarjeta("AFD  ·  Autómatas"),gtk=Tema.tarjeta("GTK  ·  Gramáticas");
        afd.add(Tema.fila(Tema.boton("Abrir AFD",this::abrirAFD),Tema.secundario("Guardar AFD",this::guardarAFD)));
        afd.add(Tema.subtitulo("Estados, alfabeto, inicial, aceptación y transiciones."),BorderLayout.SOUTH);
        gtk.add(Tema.fila(Tema.boton("Abrir GTK",this::abrirGTK),Tema.secundario("Guardar GTK",this::guardarGTK)));
        gtk.add(Tema.subtitulo("Producciones y metadatos de la gramática."),BorderLayout.SOUTH);
        tarjetas.add(afd);
        tarjetas.add(gtk);
        p.add(tarjetas,BorderLayout.NORTH);
        JPanel detalle=Tema.tarjeta("Datos disponibles en la sesión");
        detalle.add(new JScrollPane(resumen));
        p.add(detalle);
        add(p);
        add(estado,BorderLayout.SOUTH);
    }
    public void abrirAFD() {
        Tema.ejecutar(this,()-> {
            if(datos.getAfd()!=null&&!Tema.confirmar(this,"¿Reemplazar el AFD actual? Guárdelo antes si desea conservarlo."))return; Path p=ArchivosUI.elegir(this,false,"afd"); if(p==null)return; try {
                AFD a=new archivos.LectorAFD().leerArchivo(p.toString()); datos.setAfd(a); datos.marcarGuardado("AFD"); estado.setText("AFD cargado: "+p);
            }
            catch(Exception ex) {
                Tema.error(this,ex);
            }
        }
        );
    }
    public void guardarAFD() {
        Tema.ejecutar(this,()-> {
            AFD a=datos.getAfd(); if(a==null)throw new IllegalArgumentException("No hay un AFD para guardar."); Path p=ArchivosUI.elegir(this,true,"afd"); if(p==null)return; try {
                new archivos.EscritorAFD().guardarArchivo(a,p.toString()); datos.marcarGuardado("AFD"); estado.setText("AFD guardado: "+p);
            }
            catch(Exception ex) {
                Tema.error(this,ex);
            }
        }
        );
    }
    public void abrirGTK() {
        Tema.ejecutar(this,()-> {
            if(datos.getGramatica()!=null&&!Tema.confirmar(this,"¿Reemplazar la gramática actual? Guárdela antes si desea conservarla."))return; Path p=ArchivosUI.elegir(this,false,"gtk"); if(p==null)return; try {
                Gramatica g=new ServicioArchivos().leerGTK(p); datos.setGramatica(g); datos.marcarGuardado("Gramática"); estado.setText("Gramática cargada: "+p);
            }
            catch(Exception ex) {
                Tema.error(this,ex);
            }
        }
        );
    }
    public void guardarGTK() {
        Tema.ejecutar(this,()-> {
            Gramatica g=datos.getGramatica(); if(g==null)throw new IllegalArgumentException("No hay una gramática para guardar."); Path p=ArchivosUI.elegir(this,true,"gtk"); if(p==null)return; try {
                new ServicioArchivos().guardarGTK(g,p); datos.marcarGuardado("Gramática"); estado.setText("Gramática guardada: "+p);
            }
            catch(Exception ex) {
                Tema.error(this,ex);
            }
        }
        );
    }
    private void actualizar() {
        AFD a=datos.getAfd();
        Gramatica g=datos.getGramatica();
        resumen.setText("AUTÓMATA\n"+(a==null?"Sin AFD":a.getNombre()+"\n"+a.getEstados().size()+" estados / "+a.getTransiciones().size()+" transiciones")+"\n\nGRAMÁTICA\n"+(g==null?"Sin gramática":g.getNombre()+"\n"+g.getProducciones().size()+" producciones")+"\n\nFORMATO GTK\nPuede abrir archivos heredados S>aS | b | epsilon.\nAl guardar se incluyen metadatos para preservar el nombre, los símbolos y el inicial.\n\nEl historial se conserva durante la sesión; expórtelo desde Reportes.");
        resumen.setLineWrap(true);
        resumen.setWrapStyleWord(true);
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
