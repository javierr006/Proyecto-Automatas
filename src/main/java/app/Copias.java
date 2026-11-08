/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;
import modelo.*;
/** Instantáneas para exportar y evaluar mientras se edita. @author josecana */
public final class Copias {
    private Copias() {
    }
    public static AFD afd(AFD a) {
        if(a==null)return null;
        AFD c=new AFD(a.getNombre());
        a.getEstados().forEach(c::agregarEstado);
        a.getAlfabeto().forEach(c::agregarSimbolo);
        if(a.getEstadoInicial()!=null)c.establecerInicial(a.getEstadoInicial());
        a.getEstadosAceptacion().forEach(c::agregarEstadoAceptacion);
        a.getTransiciones().forEach(t->c.agregarTransicion(t.getOrigen(),t.getSimbolo(),t.getDestino()));
        return c;
    }
}
