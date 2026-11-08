/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package graficos;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import modelo.*;
/** Vista previa y PNG disponibles sin dependencias externas. @author josecana */
public class DibujoAFD {
    public BufferedImage dibujar(AFD afd) {
        if(afd==null || afd.getEstados().isEmpty())throw new IllegalArgumentException("Agregue estados para dibujar el AFD.");
        if(afd.getEstados().size()>150)throw new IllegalArgumentException("La vista previa admite hasta 150 estados. Exporte DOT para autómatas mayores.");
        List<String> estados=new ArrayList<>(afd.getEstados());
        int columnas=Math.min(5,estados.size());
        int ancho=Math.max(700,columnas*190+100),alto=Math.max(400,((estados.size()+columnas-1)/columnas)*180+100);
        if(estados.size()>150)throw new IllegalArgumentException("La vista previa admite hasta 150 estados. Exporte DOT para autómatas mayores.");
        BufferedImage imagen=new BufferedImage(ancho,alto,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=imagen.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(247,250,255));
            g.fillRect(0,0,ancho,alto);
            g.setFont(new Font("SansSerif",Font.BOLD,19));
            g.setColor(new Color(20,38,69));
            g.drawString(afd.getNombre(),28,35);
            Map<String,Point2D.Double> puntos=new LinkedHashMap<>();
            for(int i=0; i<estados.size(); i++)puntos.put(estados.get(i),new Point2D.Double(100+(i%columnas)*190,130+(i/columnas)*180));
            Map<List<String>,List<String>> aristas=new LinkedHashMap<>();
            for(Transicion t:afd.getTransiciones())aristas.computeIfAbsent(List.of(t.getOrigen(),t.getDestino()),k->new ArrayList<>()).add(t.getSimbolo());
            g.setStroke(new BasicStroke(1.8f));
            g.setFont(new Font("SansSerif",Font.PLAIN,13));
            for(var arista:aristas.entrySet()) {
                var a=puntos.get(arista.getKey().get(0));
                var b=puntos.get(arista.getKey().get(1));
                if(a==null||b==null)continue;
                g.setColor(new Color(82,106,142));
                String texto=String.join(", ",arista.getValue());
                if(a.equals(b)) {
                    g.draw(new Arc2D.Double(a.x-22,a.y-68,44,52,-15,210,Arc2D.OPEN));
                    flecha(g,a.x+21,a.y-37,a.x+26,a.y-22);
                    g.drawString(texto,(float)a.x-18,(float)a.y-73);
                }
                else {
                    double dx=b.x-a.x,dy=b.y-a.y,len=Math.hypot(dx,dy),ux=dx/len,uy=dy/len;
                    double sx=a.x+ux*30,sy=a.y+uy*30,ex=b.x-ux*30,ey=b.y-uy*30;
                    double cx=(sx+ex)/2-uy*32,cy=(sy+ey)/2+ux*32;
                    g.draw(new QuadCurve2D.Double(sx,sy,cx,cy,ex,ey));
                    flecha(g,cx,cy,ex,ey);
                    g.drawString(texto,(float)((sx+ex)/2-uy*20),(float)((sy+ey)/2+ux*20)-5);
                }
            }
            for(String e:estados) {
                var p=puntos.get(e);
                g.setColor(afd.getEstadosAceptacion().contains(e)?new Color(210,247,231):new Color(224,237,255));
                g.fill(new Ellipse2D.Double(p.x-29,p.y-29,58,58));
                g.setColor(new Color(37,99,235));
                g.draw(new Ellipse2D.Double(p.x-29,p.y-29,58,58));
                if(afd.getEstadosAceptacion().contains(e))g.draw(new Ellipse2D.Double(p.x-24,p.y-24,48,48));
                g.setFont(new Font("SansSerif",Font.BOLD,13));
                g.setColor(new Color(20,38,69));
                String corto=e.length()>8?e.substring(0,7)+"…":e;
                g.drawString(corto,(float)p.x-g.getFontMetrics().stringWidth(corto)/2f,(float)p.y+5);
                if(e.equals(afd.getEstadoInicial())) {
                    g.drawLine((int)p.x-65,(int)p.y,(int)p.x-30,(int)p.y);
                    flecha(g,p.x-65,p.y,p.x-30,p.y);
                }
            }
        }
        finally {
            g.dispose();
        }
        return imagen;
    }
    private void flecha(Graphics2D g,double x1,double y1,double x2,double y2) {
        double a=Math.atan2(y2-y1,x2-x1);
        Path2D p=new Path2D.Double();
        p.moveTo(x2,y2);
        p.lineTo(x2-10*Math.cos(a-.45),y2-10*Math.sin(a-.45));
        p.lineTo(x2-10*Math.cos(a+.45),y2-10*Math.sin(a+.45));
        p.closePath();
        g.fill(p);
    }
}
