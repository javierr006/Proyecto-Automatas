/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
/** Estilo y controles compartidos por las vistas. @author josecana */
public final class Tema {
    public static final Color FONDO=new Color(241,245,251), AZUL=new Color(37,99,235), TINTA=new Color(24,40,67), SUAVE=new Color(104,120,145), VERDE=new Color(18,128,91);
    private Tema() {
    }
    public static void instalar() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch(Exception ex) {
            java.util.logging.Logger.getLogger(Tema.class.getName()).log(java.util.logging.Level.FINE,"Estilo predeterminado",ex);
        }
        Font fuente=new Font("SansSerif",Font.PLAIN,13);
        for(String k:new String[] {
            "Label.font","Button.font","TextField.font","ComboBox.font","Table.font","TabbedPane.font","CheckBox.font","OptionPane.messageFont","OptionPane.buttonFont"
        }
        )UIManager.put(k,fuente);
        UIManager.put("Panel.background",FONDO);
        UIManager.put("Label.foreground",TINTA);
        UIManager.put("Table.selectionBackground",new Color(217,232,255));
        UIManager.put("Table.selectionForeground",TINTA);
        UIManager.put("TextField.caretForeground",AZUL);
        UIManager.put("TabbedPane.selected",Color.WHITE);
    }
    public static JLabel titulo(String s) {
        JLabel l=new JLabel(s);
        l.setFont(new Font("SansSerif",Font.BOLD,26));
        l.setForeground(TINTA);
        return l;
    }
    public static JLabel subtitulo(String s) {
        JLabel l=new JLabel(s);
        l.setForeground(SUAVE);
        return l;
    }
    public static JPanel cabecera(String titulo,String detalle) {
        JPanel p=new JPanel(new BorderLayout(0,7));
        p.setOpaque(false);
        p.add(titulo(titulo),BorderLayout.NORTH);
        p.add(subtitulo(detalle),BorderLayout.SOUTH);
        p.setBorder(new EmptyBorder(0,0,14,0));
        return p;
    }
    public static JPanel columna() {
        JPanel p=new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setOpaque(false);
        return p;
    }
    public static JPanel tarjeta(String titulo) {
        JPanel p=new JPanel(new BorderLayout(10,12));
        p.setBackground(Color.WHITE);
        Border borde=new LineBorder(new Color(222,230,242),1,true);
        if(titulo!=null)borde=BorderFactory.createTitledBorder(borde,titulo,TitledBorder.LEFT,TitledBorder.TOP,new Font("SansSerif",Font.BOLD,15),TINTA);
        p.setBorder(new CompoundBorder(borde,new EmptyBorder(16,18,16,18)));
        return p;
    }
    public static JScrollPane desplazar(Component c) {
        JScrollPane scroll=new JScrollPane(c);
        if(c instanceof JTable t)scroll.setColumnHeaderView(t.getTableHeader());
        return scroll;
    }
    public static JPanel fila(Component... cs) {
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,8,5));
        p.setOpaque(false);
        for(Component c:cs)p.add(c);
        return p;
    }
    public static JTextField campo(String nombre,int columnas) {
        JTextField f=new JTextField(columnas);
        f.setName(nombre);
        f.getAccessibleContext().setAccessibleName(nombre);
        f.setFont(new Font("SansSerif",Font.PLAIN,14));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(195,208,228),1,true),new EmptyBorder(8,9,8,9)));
        return f;
    }
    public static JButton boton(String texto,Runnable accion) {
        JButton b=new JButton(texto);
        b.setName(texto);
        b.setFocusPainted(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setBorder(new CompoundBorder(new LineBorder(AZUL,1,true),new EmptyBorder(9,14,9,14)));
        b.addActionListener(e->accion.run());
        return b;
    }
    public static JButton secundario(String texto,Runnable accion) {
        JButton b=boton(texto,accion);
        b.setBackground(new Color(233,240,254));
        b.setForeground(AZUL);
        b.setBorder(new CompoundBorder(new LineBorder(new Color(211,224,248),1,true),new EmptyBorder(9,14,9,14)));
        return b;
    }
    public static JTextArea area() {
        JTextArea a=new JTextArea();
        a.setEditable(false);
        a.setFont(new Font("Monospaced",Font.PLAIN,13));
        a.setMargin(new Insets(12,12,12,12));
        a.setForeground(TINTA);
        return a;
    }
    public static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas,0) {
            @Override public boolean isCellEditable(int r,int c) {
                return false;
            }
        }
        ;
    }
    public static JTable tabla(DefaultTableModel m) {
        JTable t=new JTable(m);
        t.setRowHeight(31);
        t.setShowVerticalLines(false);
        t.setGridColor(new Color(232,238,247));
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));
        t.getTableHeader().setPreferredSize(new Dimension(0,34));
        return t;
    }
    public static void preparar(JPanel p) {
        p.setLayout(new BorderLayout(14,14));
        p.setBorder(new EmptyBorder(25,26,22,26));
        p.setBackground(FONDO);
    }
    public static void ejecutar(Component padre,Runnable accion) {
        try {
            accion.run();
        }
        catch(RuntimeException ex) {
            error(padre,ex);
        }
    }
    public static void error(Component padre,Throwable ex) {
        JOptionPane.showMessageDialog(padre,ex.getMessage()==null?ex.toString():ex.getMessage(),"Revise los datos",JOptionPane.ERROR_MESSAGE);
    }
    public static boolean confirmar(Component p,String mensaje) {
        return JOptionPane.showConfirmDialog(p,mensaje,"Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
    }
}
