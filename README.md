# Proyecto Autómatas

Aplicación Swing de José y Diego para editar AFD y gramáticas regulares, convertir modelos, evaluar cadenas, guardar archivos, graficar y exportar reportes PDF.

## Abrir en NetBeans

1. Abrir esta carpeta como proyecto Maven.
2. Seleccionar JDK 21 como plataforma del proyecto.
3. Ejecutar el proyecto. El punto de entrada configurado es `com.proyectoautomatas.proyectoautomatas.ProyectoAutomatas`, que abre la bienvenida.

También se puede ejecutar `app.Main`, `vista.VentanaInicio` o `vista.VentanaMenu`. Las vistas se construyen después de `initComponents()`: los archivos `.form` y los bloques generados de NetBeans se conservan literalmente. El diseñador muestra la estructura original; la interfaz completa se ve al ejecutar.

## Construir y ejecutar

```sh
mvn clean package
mvn exec:java
```

`mvn test` ejecuta las regresiones de integración mediante `exec-maven-plugin`, además de las fases habituales de Maven. Las pruebas funcionan sin pantalla y no necesitan JUnit. Una regresión lanza una excepción y hace fallar el build.

En esta Mac, Maven viene con NetBeans en `/Applications/netbeans/java/maven/bin/mvn`. Si no está en PATH, puede usarse esa ruta.

La distribución entregada incluye `ProyectoAutomatas.jar` y `lib/`. Para esa distribución, basta Java 21 y `java -jar ProyectoAutomatas.jar`, o abrir `iniciar.command` en macOS. El JAR producido por Maven necesita las dependencias PDFBox indicadas en su manifiesto junto a él en `lib/`; la ejecución con Maven las resuelve automáticamente.

## Recorrido de prueba

- Autómatas → Ejemplo binario. Acepta cadenas binarias terminadas en `1`.
- Evaluación → AFD: `101` aceptada; `100` rechazada; campo vacío rechazada.
- Autómatas → AFD → Gramática. Abrir Gramáticas para revisar las producciones.
- Evaluación → Gramática: los resultados deben coincidir con el AFD.
- Archivos: guardar y volver a abrir `.afd` y `.gtk`.
- Autómatas → Gráfico y DOT: actualizar el gráfico y exportar PNG o DOT.
- Reportes: exportar PDF con los modelos y los resultados de la sesión.

Las acciones que reemplazan modelos o archivos existentes piden confirmación. El historial se conserva en memoria durante la sesión; se incluye en el PDF y no se reabre con los archivos de modelos.

## Reglas y formatos

Los terminales y no terminales de gramática ocupan un carácter UTF-16. Se admiten `epsilon` o `ε` como producción vacía; en la entrada de evaluación, la cadena vacía se representa dejando el campo vacío. Un AFD no admite transiciones epsilon.

Las gramáticas admiten alternativas `|`, producciones terminales de varios caracteres, reglas unitarias y ciclos. Pueden ser lineales por la derecha o por la izquierda, sin mezclar orientaciones. La conversión aplica cierre epsilon y construcción por subconjuntos, con estado sumidero para producir un AFD total. Evaluación y conversión se realizan fuera del hilo de la interfaz.

Se admite el alfabeto vacío para representar el lenguaje vacío o `{epsilon}`. Las evaluaciones de la interfaz admiten hasta 2000 caracteres, la derivación hasta 100000 configuraciones y la conversión hasta 4096 estados. Estos límites producen un mensaje explícito, no un rechazo silencioso de la cadena. La vista gráfica integrada admite hasta 150 estados; DOT permite exportar modelos mayores.

### AFD UTF-8

```text
nombre=Termina en 1
estados=q0,q1
alfabeto=0,1
inicial=q0
aceptacion=q1

[TRANSICIONES]
q0,0,q0
q0,1,q1
q1,0,q0
q1,1,q1
```

Los nombres de estado no pueden contener comas ni saltos de línea ni comenzar con `#` o `//`. Los símbolos reservados `ε | > , = #` y espacios no se admiten como terminales. Se permite guardar un AFD con transiciones aún incompletas para continuar su edición; la evaluación en la interfaz requiere completarlas.

### GTK UTF-8

Se pueden abrir los archivos heredados de producciones, por ejemplo:

```text
S>aS | b | epsilon
```

En el formato heredado, el primer lado izquierdo determina el inicial; los lados izquierdos y las letras mayúsculas del lado derecho son no terminales. Los demás caracteres son terminales, incluidos los dígitos. El formato guardado por la aplicación agrega metadatos opcionales `#GTK2`, nombre en Base64 UTF-8, listas de símbolos e inicial, para conservar también símbolos sin producciones e iniciales distintos del primer lado izquierdo. La aplicación conserva las clases originales de lectura y escritura de Diego; su lector antiguo no interpreta los nuevos metadatos. Usar la pantalla Archivos para leer los nuevos GTK.

## Integración y conservación del trabajo de Diego

Se mantienen intactos `modelo/AFD`, `Gramatica`, `Produccion`, `Transicion`, las tres clases de `gramatica/`, `conversion/GramaticaAAFD`, `archivos/LectorGTK`, `EscritorGTK` y `reportes/DetalleReporte`, `GeneradorReportePDF`. Los nuevos servicios adaptan y amplían sus formatos y capacidades sin sustituir esos archivos.

`ControladorGramatica` y los modelos originales siguen usándose en el editor. `DetalleReporte` se reutiliza en los reportes. La implementación original de conversión y evaluación de Diego se verifica en las pruebas con su subconjunto compatible; la aplicación utiliza `ServicioGramatica` para unificar epsilon, completar el AFD y admitir derivaciones y reglas más generales.

## Gráficos y reportes

La vista previa y Exportar PNG usan Java2D y no requieren instalación externa. DOT y PNG con Graphviz usan `GeneradorGraphviz`; Graphviz debe estar instalado para esta segunda opción. Se busca `dot` en PATH y en las rutas habituales de macOS. Puede indicarse otra ruta con `-Dgraphviz.dot=/ruta/a/dot`. El proceso tiene un límite de 30 segundos; los errores no reemplazan el archivo de destino.

Los reportes PDF paginan y ajustan el texto largo. Para conservar símbolos fuera de la fuente PDF estándar, los representan de manera explícita con su código Unicode, por ejemplo `[U+3A3]`. Las evaluaciones del historial conservan nombre, tipo, cadena, resultado y pasos tal como estaban al evaluarse.

## Verificación realizada

- Build Maven y 1429 comprobaciones de lógica, conversiones, archivos, gráficos, PDF y sesión.
- Flujo de botones Swing: ejemplo → conversión → navegación → cuatro evaluaciones, con historial compartido.
- Render de las ocho vistas reales y del espacio de trabajo a dos anchos.
- Comparación literal de las clases originales de Diego, de todos los `.form` y de los bloques protegidos.
- Graphviz no está instalado en la Mac revisada: se verificaron DOT, PNG integrado y el manejo de su ausencia. No se afirma una prueba con el ejecutable real de Graphviz.
- La ventana nativa no pudo ejecutarse desde el entorno de pruebas por una falla de registro de Java con macOS. La lógica, los eventos Swing y las vistas renderizadas se probaron sin pantalla; abrir el proyecto en NetBeans permite revisar la ventana nativa.

No se incluyeron instrucciones de la tarea o rúbrica en el repositorio inspeccionado. Si existe una rúbrica externa, conviene compararla antes de entregar académicamente.

## Estado de Git al finalizar

Los cambios están aplicados localmente en la rama `jose/finalizar-proyecto`, sin commit ni push. `master` y la referencia remota de Diego conservan el commit `0336c9323d0974b3840192a40b25e68eaf7edabe`. Se reparó el remoto local inválido `Proyecto Automatas` renombrándolo a `origin` y preservando todas sus referencias. El ZIP de respaldo conserva el estado inicial, incluidos los cambios locales anteriores y `.git`.
