/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;
import java.beans.PropertyChangeSupport;
import java.util.*;
import modelo.*;
/** Datos de la sesión, compartidos por todas las vistas. @author josecana */
public class RepositorioDatos {
    private AFD afd;
    private Gramatica gramatica;
    private final List<ResultadoEvaluacion> historial = new ArrayList<>();
    private final PropertyChangeSupport cambios = new PropertyChangeSupport(this);
    private boolean afdModificado, gramaticaModificada;
    private long version;
    public AFD getAfd() {
        return afd;
    }
    public Gramatica getGramatica() {
        return gramatica;
    }
    public List<ResultadoEvaluacion> getHistorial() {
        return List.copyOf(historial);
    }
    public boolean isModificado() {
        return afdModificado || gramaticaModificada;
    }
    public long getVersion() {
        return version;
    }
    public void setAfd(AFD valor) {
        afd = valor;
        actualizarAFD();
    }
    public void setGramatica(Gramatica valor) {
        gramatica = valor;
        actualizarGramatica();
    }
    public void actualizarAFD() {
        afdModificado=true;
        notificar();
    }
    public void actualizarGramatica() {
        gramaticaModificada=true;
        notificar();
    }
    private void notificar() {
        version++;
        cambios.firePropertyChange("datos",null,version);
    }
    public void registrar(ResultadoEvaluacion valor) {
        historial.add(valor);
        cambios.firePropertyChange("historial", null, valor);
    }
    public void escuchar(Runnable accion) {
        cambios.addPropertyChangeListener(e -> accion.run());
    }
    public void marcarGuardado(String tipo) {
        if(tipo.equals("AFD"))afdModificado=false;
        else gramaticaModificada=false;
        cambios.firePropertyChange("guardado", null, true);
    }
}
