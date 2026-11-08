/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.time.LocalDateTime;
import java.util.List;
/** Resultado inmutable para el historial y los reportes. @author josecana */
public class ResultadoEvaluacion {
    private final String tipo, nombre, cadena;
    private final boolean aceptada;
    private final List<String> pasos;
    private final LocalDateTime fecha = LocalDateTime.now();
    public ResultadoEvaluacion(String tipo, String nombre, String cadena, boolean aceptada, List<String> pasos) {
        this.tipo=tipo;
        this.nombre=nombre;
        this.cadena=cadena;
        this.aceptada=aceptada;
        this.pasos=List.copyOf(pasos);
    }
    public String getTipo() {
        return tipo;
    }
    public String getNombre() {
        return nombre;
    }
    public String getCadena() {
        return cadena;
    }
    public boolean isAceptada() {
        return aceptada;
    }
    public List<String> getPasos() {
        return pasos;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
}
