/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import modelo.*;
/** Formato GTK con metadatos opcionales y lectura del formato heredado. @author josecana */
public class ServicioArchivos {
    private final ServicioGramatica servicio=new ServicioGramatica();
    public void guardarGTK(Gramatica g, Path destino) throws IOException {
        servicio.validar(g,true);
        List<String> lineas=new ArrayList<>();
        lineas.add("#GTK2");
        lineas.add("#nombre="+codificar(g.getNombre()));
        lineas.add("#noTerminales="+String.join(",",g.getNoTerminales()));
        lineas.add("#terminales="+String.join(",",g.getTerminales()));
        lineas.add("#inicial="+g.getSimboloInicial());
        g.getProducciones().forEach(p->lineas.add(p.getIzquierda()+">"+ServicioGramatica.normalizar(p.getDerecha())));
        escribirAtomico(destino,String.join("\n",lineas)+"\n");
    }
    private String codificar(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
    public Gramatica leerGTK(Path origen) throws IOException {
        List<String> lineas=Files.readAllLines(origen,StandardCharsets.UTF_8);
        Map<String,String> meta=new LinkedHashMap<>();
        List<String[]> reglas=new ArrayList<>();
        int numero=0;
        try {
            for(String texto:lineas) {
                numero++;
                String s=texto.replace("\uFEFF", "").trim();
                if(s.isEmpty()||s.equals("#GTK2"))continue;
                if(s.startsWith("#")) {
                    int p=s.indexOf('=');
                    if(p>1) {
                        String clave=s.substring(1,p);
                        if(meta.putIfAbsent(clave,s.substring(p+1))!=null)throw new IllegalArgumentException("Metadato repetido: "+clave);
                    }
                    continue;
                }
                String[] partes=s.split(">",-1);
                if(partes.length!=2 || partes[0].trim().length()!=1)throw new IllegalArgumentException("Se esperaba S>aA | epsilon.");
                for(String a:partes[1].split("\\|",-1))reglas.add(new String[] {
                    partes[0].trim(),ServicioGramatica.normalizar(a)
                }
                );
            }
            boolean extendido=meta.containsKey("noTerminales");
            String nombre=meta.containsKey("nombre")?new String(Base64.getDecoder().decode(meta.get("nombre")),StandardCharsets.UTF_8):origen.getFileName().toString();
            Gramatica g=new Gramatica(nombre);
            if(extendido) {
                for(String s:valores(meta.get("noTerminales")))g.agregarNoTerminal(s);
                for(String s:valores(meta.getOrDefault("terminales","")))g.agregarTerminal(s);
            }
            else {
                for(String[] p:reglas)if(!g.getNoTerminales().contains(p[0]))g.agregarNoTerminal(p[0]);
                for(String[] p:reglas)if(!p[1].equals("epsilon"))for(char c:p[1].toCharArray()) if(Character.isUpperCase(c) && !g.getNoTerminales().contains(String.valueOf(c)))g.agregarNoTerminal(String.valueOf(c));
                for(String[] p:reglas)if(!p[1].equals("epsilon"))for(char c:p[1].toCharArray()) {
                    String s=String.valueOf(c);
                    if(!g.getNoTerminales().contains(s) && !g.getTerminales().contains(s))g.agregarTerminal(s);
                }
            }
            String inicial=meta.getOrDefault("inicial",reglas.isEmpty()?"":reglas.get(0)[0]);
            g.setSimboloInicial(inicial);
            for(String[] p:reglas)servicio.agregarProduccion(g,p[0],p[1]);
            servicio.validar(g,true);
            return g;
        }
        catch(RuntimeException ex) {
            throw new IOException("GTK inválido cerca de la línea "+numero+": "+ex.getMessage(),ex);
        }
    }
    private List<String> valores(String s) {
        return s.isEmpty()?List.of():Arrays.asList(s.split(",",-1));
    }
    public static void escribirAtomico(Path destino,String texto) throws IOException {
        Path absoluto=destino.toAbsolutePath();
        Path temp=Files.createTempFile(absoluto.getParent(),"automatas-",".tmp");
        try {
            Files.writeString(temp,texto,StandardCharsets.UTF_8);
            reemplazar(temp,absoluto);
        }
        finally {
            Files.deleteIfExists(temp);
        }
    }
    public static void reemplazar(Path temp,Path destino) throws IOException {
        try {
            Files.move(temp,destino,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
        }
        catch(AtomicMoveNotSupportedException ex) {
            Files.move(temp,destino,StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
