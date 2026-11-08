/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;
import java.awt.Component;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
/** Selección de archivos y tareas de exportación sin bloquear la interfaz. @author josecana */
public final class ArchivosUI {
    private ArchivosUI() {
    }
    public static Path elegir(Component padre,boolean guardar,String extension) {
        JFileChooser f=new JFileChooser();
        f.setFileFilter(new FileNameExtensionFilter("Archivos ."+extension,extension));
        if((guardar?f.showSaveDialog(padre):f.showOpenDialog(padre))!=JFileChooser.APPROVE_OPTION)return null;
        Path p=f.getSelectedFile().toPath();
        if(guardar&&!p.toString().toLowerCase(java.util.Locale.ROOT).endsWith("."+extension))p=Path.of(p+"."+extension);
        if(guardar&&Files.exists(p)&&!Tema.confirmar(padre,"El archivo ya existe. ¿Desea reemplazarlo?"))return null;
        return p;
    }
    @FunctionalInterface public interface Trabajo {
        void ejecutar()throws Exception;
    }
    public static void trabajar(Component padre,JButton boton,Trabajo trabajo,Runnable fin) {
        boton.setEnabled(false);
        String texto=boton.getText();
        boton.setText("Procesando…");
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground()throws Exception {
                trabajo.ejecutar();
                return null;
            }
            @Override protected void done() {
                boton.setText(texto);
                boton.setEnabled(true);
                try {
                    get();
                    fin.run();
                }
                catch(Exception ex) {
                    Tema.error(padre,ex.getCause()==null?ex:ex.getCause());
                }
            }
        }
        .execute();
    }
}
