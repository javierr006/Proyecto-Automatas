/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;
import java.util.*;
import modelo.*;
/** Adaptador de gramáticas regulares; conserva las clases originales de Diego. @author josecana */
public class ServicioGramatica {
    public static String normalizar(String texto) {
        String s=texto.trim();
        return s.equals("ε") ? "epsilon" : s;
    }
    public void validarSimbolo(String s) {
        if (s == null || s.length()!=1 || Character.isWhitespace(s.charAt(0)) || "ε|>,=#".contains(s)) throw new IllegalArgumentException("Use un único carácter; ε, |, >, coma, = y # están reservados.");
    }
    public void agregarProduccion(Gramatica g, String izquierda, String derecha) {
        if(g==null) throw new IllegalArgumentException("Primero cree una gramática.");
        List<Produccion> nuevas = new ArrayList<>();
        for(String alternativa: derecha.split("\\|",-1)) {
            Produccion p=new Produccion(izquierda.trim(),normalizar(alternativa));
            if(g.getProducciones().stream().anyMatch(x->iguales(x,p)) || nuevas.stream().anyMatch(x->iguales(x,p))) throw new IllegalArgumentException("La producción está repetida.");
            nuevas.add(p);
        }
        Gramatica copia = copiar(g);
        nuevas.forEach(copia::agregarProduccion);
        validar(copia, false);
        nuevas.forEach(g::agregarProduccion);
    }
    private boolean iguales(Produccion a,Produccion b) {
        return a.getIzquierda().equals(b.getIzquierda()) && normalizar(a.getDerecha()).equals(normalizar(b.getDerecha()));
    }
    public Gramatica copiar(Gramatica g) {
        Gramatica c=new Gramatica(g.getNombre());
        g.getNoTerminales().forEach(c::agregarNoTerminal);
        g.getTerminales().forEach(c::agregarTerminal);
        if(g.getSimboloInicial()!=null)c.setSimboloInicial(g.getSimboloInicial());
        g.getProducciones().forEach(p->c.agregarProduccion(new Produccion(p.getIzquierda(),normalizar(p.getDerecha()))));
        return c;
    }
    public void validar(Gramatica g, boolean exigirInicial) {
        if(g==null)throw new IllegalArgumentException("Primero cree o abra una gramática.");
        g.getNoTerminales().forEach(this::validarSimbolo);
        g.getTerminales().forEach(this::validarSimbolo);
        if(!Collections.disjoint(g.getTerminales(),g.getNoTerminales()))throw new IllegalArgumentException("Terminales y no terminales deben ser distintos.");
        if(exigirInicial && !g.getNoTerminales().contains(g.getSimboloInicial()))throw new IllegalArgumentException("Defina un símbolo inicial válido.");
        boolean izquierda=false,derecha=false;
        for(Produccion p:g.getProducciones()) {
            if(p==null || !g.getNoTerminales().contains(p.getIzquierda()))throw new IllegalArgumentException("No terminal de origen inválido.");
            String s=normalizar(p.getDerecha());
            if(s.equals("epsilon"))continue;
            if(s.isEmpty())throw new IllegalArgumentException("Use epsilon para una producción vacía.");
            int n=0,pos=-1;
            for(int i=0; i<s.length(); i++) {
                String c=s.substring(i,i+1);
                if(g.getNoTerminales().contains(c)) {
                    n++;
                    pos=i;
                }
                else if(!g.getTerminales().contains(c))throw new IllegalArgumentException("Símbolo no definido: "+c);
            }
            if(n>1 || (n==1 && pos!=0 && pos!=s.length()-1))throw new IllegalArgumentException("La producción no es regular: "+p);
            if(n==1 && s.length()>1) {
                izquierda |= pos==0;
                derecha |= pos==s.length()-1;
            }
        }
        if(izquierda && derecha)throw new IllegalArgumentException("No mezcle producciones lineales por la izquierda y por la derecha.");
    }
    public AFD convertir(Gramatica g) {
        validar(g,true);
        boolean porIzquierda=g.getProducciones().stream().map(p->normalizar(p.getDerecha())) .anyMatch(s->!s.equals("epsilon") && s.length()>1 && g.getNoTerminales().contains(s.substring(0,1)));
        // Los identificadores internos no dependen de los nombres introducidos.
        Map<String,Map<String,Set<String>>> mov=new LinkedHashMap<>();
        Map<String,Set<String>> eps=new LinkedHashMap<>();
        Set<String> finales=new LinkedHashSet<>();
        g.getNoTerminales().forEach(n->mov.put(n,new LinkedHashMap<>()));
        String extra="@final";
        mov.put(extra,new LinkedHashMap<>());
        String inicial=porIzquierda?extra:g.getSimboloInicial();
        finales.add(porIzquierda?g.getSimboloInicial():extra);
        int contador=0;
        for(Produccion p:g.getProducciones()) {
            String s=normalizar(p.getDerecha()), origen=p.getIzquierda(),destino=extra;
            if(s.equals("epsilon"))s="";
            if(porIzquierda) {
                destino=origen;
                origen=extra;
                if(!s.isEmpty() && g.getNoTerminales().contains(s.substring(0,1))) {
                    origen=s.substring(0,1);
                    s=s.substring(1);
                }
            }
            else if(!s.isEmpty() && g.getNoTerminales().contains(s.substring(s.length()-1))) {
                destino=s.substring(s.length()-1);
                s=s.substring(0,s.length()-1);
            }
            if(s.isEmpty()) {
                eps.computeIfAbsent(origen,k->new LinkedHashSet<>()).add(destino);
                continue;
            }
            for(int i=0; i<s.length(); i++) {
                String siguiente=i==s.length()-1?destino:"@paso"+(contador++);
                mov.computeIfAbsent(siguiente,k->new LinkedHashMap<>());
                mov.get(origen).computeIfAbsent(s.substring(i,i+1),k->new LinkedHashSet<>()).add(siguiente);
                origen=siguiente;
            }
        }
        AFD afd=new AFD("AFD_"+g.getNombre());
        g.getTerminales().forEach(afd::agregarSimbolo);
        Map<Set<String>,String> nombres=new LinkedHashMap<>();
        List<Set<String>> pendientes=new ArrayList<>();
        Set<String> inicio=cierre(Set.of(inicial),eps);
        nombres.put(inicio,"q0");
        pendientes.add(inicio);
        afd.agregarEstado("q0");
        afd.establecerInicial("q0");
        for(int i=0; i<pendientes.size(); i++) {
            if(pendientes.size()>4096)throw new IllegalArgumentException("La conversión supera 4096 estados; reduzca la gramática.");
            Set<String> conjunto=pendientes.get(i);
            String origen=nombres.get(conjunto);
            if(!Collections.disjoint(conjunto,finales))afd.agregarEstadoAceptacion(origen);
            for(String simbolo:g.getTerminales()) {
                Set<String> d=new LinkedHashSet<>();
                for(String e:conjunto)d.addAll(mov.get(e).getOrDefault(simbolo,Set.of()));
                d=cierre(d,eps);
                // El conjunto vacío es el estado sumidero: el AFD queda total.
                if(!nombres.containsKey(d)) {
                    String n="q"+nombres.size();
                    nombres.put(d,n);
                    pendientes.add(d);
                    afd.agregarEstado(n);
                }
                afd.agregarTransicion(origen,simbolo,nombres.get(d));
            }
        }
        return afd;
    }
    public Optional<List<String>> derivar(Gramatica g,String cadena) {
        validar(g,true);
        if(cadena==null)throw new IllegalArgumentException("La cadena no puede ser nula.");
        Map<String,String> padres=new LinkedHashMap<>();
        List<String> cola=new ArrayList<>();
        String inicio=g.getSimboloInicial();
        padres.put(inicio,null);
        cola.add(inicio);
        for(int i=0; i<cola.size(); i++) {
            if(cola.size()>100000)throw new IllegalArgumentException("La evaluación supera 100000 configuraciones; reduzca la cadena.");
            String actual=cola.get(i);
            if(actual.equals(cadena)) {
                List<String> pasos=new ArrayList<>();
                for(String x=actual; x!=null; x=padres.get(x))pasos.add(x.isEmpty()?"ε":x);
                Collections.reverse(pasos);
                return Optional.of(pasos);
            }
            int posicion=-1;
            for(int j=0; j<actual.length(); j++)if(g.getNoTerminales().contains(actual.substring(j,j+1))) {
                posicion=j;
                break;
            }
            if(posicion<0)continue;
            String nt=actual.substring(posicion,posicion+1);
            for(Produccion p:g.getProducciones())if(p.getIzquierda().equals(nt)) {
                String derecha=normalizar(p.getDerecha());
                if(derecha.equals("epsilon"))derecha="";
                String siguiente=actual.substring(0,posicion)+derecha+actual.substring(posicion+1);
                int n=-1;
                for(int j=0; j<siguiente.length(); j++)if(g.getNoTerminales().contains(siguiente.substring(j,j+1))) {
                    n=j;
                    break;
                }
                String prefijo=n<0?siguiente:siguiente.substring(0,n),sufijo=n<0?"":siguiente.substring(n+1);
                if(prefijo.length()+sufijo.length()>cadena.length() || !cadena.startsWith(prefijo) || !cadena.endsWith(sufijo))continue;
                if(!padres.containsKey(siguiente)) {
                    padres.put(siguiente,actual);
                    cola.add(siguiente);
                }
            }
        }
        return Optional.empty();
    }
    private Set<String> cierre(Set<String> origen,Map<String,Set<String>> eps) {
        Set<String> c=new LinkedHashSet<>(origen);
        List<String> cola=new ArrayList<>(origen);
        for(int i=0; i<cola.size(); i++)for(String d:eps.getOrDefault(cola.get(i),Set.of()))if(c.add(d))cola.add(d);
        return c;
    }
}
