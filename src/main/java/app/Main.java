/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;
/**
 *
 * @author josecana
 */
public class Main {
    public static void main(String[] args) {
        vista.Tema.instalar();
        java.awt.EventQueue.invokeLater(() -> new vista.VentanaInicio().setVisible(true));
    }
}
