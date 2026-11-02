# Spacecraft Log: simulador de nave y analizador forense

Práctica UD1 · Manejo de ficheros · Acceso a Datos · DAM (IES Islas Filipinas)

**Autor:** Jorge Cubilla
**Repositorio:** https://github.com/jtcf92/spacecraft-log

## 1. Temática

Una nave espacial (`Pegasus`) formada por un **motor de cohete**, un **escudo** y dos **armas** (Láser y Torpedo).

- **Simulador:** hace funcionar la nave unos pasos (mover, disparar, recibir golpes, descansar), provoca un incidente y deja las evidencias en tres ficheros.
- **Analizador forense:** programa independiente que, leyendo solo esos ficheros, averigua qué incidente ocurrió y qué elementos se vieron afectados.

## 2. Incidentes

| Incidente | Qué ocurre | Huella en los ficheros |
|---|---|---|
| `SOBRECALENTAMIENTO_MOTOR` | El motor supera su temperatura crítica | TXT: evento crítico del motor. BIN: calor anómalo. XML: motor `AVERIADO` |
| `FALLO_MOTOR` | El motor pierde toda su energía | TXT: evento crítico del motor. BIN: energía anómala. XML: motor `AVERIADO` |
| `PERDIDA_ESCUDOS` | El escudo se destruye | TXT: evento crítico del escudo. BIN: vida del escudo anómala. XML: escudo `INACTIVO` |
| `IMPACTO_DIRECTO` | Con el escudo roto, un impacto alcanza la nave | TXT: evento crítico del motor. BIN: vida del escudo anómala. XML: escudo `INACTIVO` y motor `EMERGENCIA` |
| `FALLO_ARMAMENTO` | Un arma falla | TXT: evento crítico del arma. BIN: daño del arma anómalo. XML: arma `AVERIADA` |
| `INDETERMINADO` | Solo lo devuelve el analizador cuando las pruebas no bastan | (falta alguna de las tres fuentes o no encaja ninguna regla) |

## 3. Ficheros y cómo se relacionan

El simulador genera tres ficheros en `datos/`. Cada uno tiene una función distinta:

| Fichero | Contenido | Tecnología |
|---|---|---|
| `operaciones.txt` | Histórico legible de lo que ocurre: `fecha \| TIPO \| componente \| detalle` | Java I/O (`BufferedWriter`) |
| `mediciones.dat` | Mediciones numéricas (energía, calor, vida del escudo, daño del arma) en registros de 57 bytes | `RandomAccessFile` |
| `estado.xml` | Estructura de la nave: motor, escudo y armas con su estado | DOM |

**Relación entre ellos:** los tres usan los mismos identificadores de componente: `MOTOR-<serie>`, `ESCUDO` y `ARMA-<NOMBRE>` (por ejemplo `MOTOR-1234`, `ARMA-LASER`). Así el analizador sabe que una línea del TXT, una medición del BIN y un elemento del XML hablan del mismo componente.

**Registro del binario** (57 bytes): `id` (int, 4) + `idComponente` (20 caracteres, 40) + `tipo` (int, 4) + `valor` (double, 8) + `anómala` (boolean, 1). El registro `n` empieza en el byte `(n - 1) × 57`, lo que permite el acceso directo con `seek`.

**Excepción de dominio:** `EventoCriticoException` se lanza **después** de que la anomalía se haya guardado en los tres ficheros. El `catch` no genera datos.

### Salida del analizador (`ResultadoForense/`)

| Fichero | Contenido |
|---|---|
| `informe_forense.txt` | Incidente identificado, efectos detectados y conclusión |
| `anomalias.dat` | Solo las mediciones anómalas del binario de origen |
| `afectados.xml` | Solo los elementos que no están `OPERATIVO` |

### Logs (Log4j 2, configuración en `src/main/resources/log4j2.xml`)

| Carpeta | Contenido |
|---|---|
| `logs/evidencias/` | Un fichero por ejecución: `ejecucion_dd-MM-yyyy_HH-mm-ss.log` |
| `logs/runtime/` | `aplicacion.log` con rotación por tamaño (5 KB), históricos `aplicacion-yyyy-MM-dd-N.log.gz` y máximo 5 conservados |

El log técnico es independiente de los datos y el analizador no lo lee.

## 4. Requisitos

- JDK 17 o superior
- Maven 3.x
- Dependencias (se descargan solas con Maven): Log4j 2 (`2.25.2`) y JUnit 5 (`5.14.4`)

## 5. Cómo compilar y ejecutar

```
git clone https://github.com/jtcf92/spacecraft-log.git
cd spacecraft-log
mvn compile
```

### Simulador

Clase: `Main` (`src/main/java/Main.java`, paquete por defecto). Con IntelliJ, ejecuta su `main`. Con Maven:

```
mvn compile exec:java -Dexec.mainClass=Main
```

**Modo normal** (incidente aleatorio) y **modo prueba** (incidente concreto): en el `main` de `Main.java` hay una variable `modoPrueba`:

- `boolean modoPrueba = false;` → el simulador elige un incidente al azar.
- `boolean modoPrueba = true;` → se provoca el incidente de la línea
  `simulador.ejecutar(4, TipoIncidente.FALLO_ARMAMENTO);`
  Para provocar otro, se cambia el nombre del enum por cualquiera de los incidentes de la tabla (por ejemplo `TipoIncidente.FALLO_MOTOR`).

Al terminar, el simulador captura el evento crítico y se detiene. Los ficheros quedan en `datos/`.
### Consulta y modificación con acceso directo

Clase: `herramientas.ConsultaBinario`. Hace una consulta y una modificación de un registro concreto con `RandomAccessFile`, sobre una copia (`datos/mediciones_demo.dat`):

```
mvn exec:java -Dexec.mainClass=herramientas.ConsultaBinario
```

## 6. Pruebas automáticas

```
mvn test
```

12 pruebas con JUnit 5 (usan `@TempDir`, no tocan los ficheros reales): texto, binario con `RandomAccessFile` (alta, lectura y modificación), XML, provocación de la situación crítica y comprobación de la excepción, identificación de los 5 incidentes por el analizador, y una prueba de integración simulador → analizador → salidas.

## 7. Estructura

```
spacecraft-log/
├── src/main/java/
│   ├── simulador/          Simulador (con su main)
│   ├── analizadorForense/  AnalizadorForense (con su main)
│   ├── herramientas/       ConsultaBinario (demo de acceso directo)
│   ├── modeloNave/ modeloArma/   Nave, MotorDeCohete, Escudo, Arma
│   ├── persistencia/       RegistroTexto, RegistroBinario, RegistroXML, Persistencia
│   ├── enums/ excepciones/ interfaces/
│   └── ...
├── src/main/resources/log4j2.xml
├── src/test/               pruebas y log4j2-test.xml
├── datos/                  ficheros del simulador (ejemplo)
├── ResultadoForense/       salidas del analizador (ejemplo)
├── logs/                   evidencias y rotación
├── IA.md
├── README.md
└── pom.xml
```

## 8. Uso de inteligencia artificial

Ver [IA.md](IA.md).