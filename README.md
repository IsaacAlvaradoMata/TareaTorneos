# Tarea Programada BracketChamp

*Torneo Pro Manager* es una aplicación de escritorio desarrollada en JavaFX que permite gestionar torneos deportivos mediante llaves eliminatorias, registrar estadísticas por partido, desbloquear logros únicos para cada equipo y visualizar animaciones al alcanzar metas deportivas.

---

## Funcionalidades Destacadas

- Creación y administración de torneos eliminatorios con emparejamiento automático.
- Registro de estadísticas por equipo: anotaciones, goles en contra, resultados y más.
- Desbloqueo de logros con efectos visuales llamativos.
- Persistencia de datos en formato JSON.
- Interfaz moderna y personalizable usando JavaFX.

---

## Tips Interactivos en la Interfaz

La aplicación incorpora **íconos informativos** (ℹ️) en diferentes pantallas. Al pasar el mouse sobre estos íconos, se despliegan **tooltips explicativos** con consejos y detalles importantes sobre el uso de cada funcionalidad.

Estas ayudas contextuales permiten a los usuarios:
- Comprender mejor cómo usar los módulos (torneos, desempates, estadísticas).
- Aprender el propósito de cada botón o animación.
- Identificar cómo se obtienen ciertos logros.

> Ejemplo: En la vista de desempates, el ícono ℹ️ muestra instrucciones detalladas sobre cómo funciona la mecánica del minijuego para desempatar en caso de empate.

---

## Instrucciones de Ejecución

## Requisitos

- Java 21 o superior
- Maven 3.x
- JavaFX JDK 23.0.1
- Scene Builder (opcional, para editar FXML)

## Clonar y Ejecutar

```bash
git clone https://github.com/IsaacAlvaradoMata/TareaTorneos.git
cd TareaTorneos
mvn clean 
javafx:run
```

 En caso de errores, asegurarse de tener el JavaFX JDK configurado correctamente en tu IDE (IntelliJ/Eclipse) o variable de entorno `PATH`.

---

## Estructura del Proyecto
```bash

TareaTorneos/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── cr.ac.una.tareatorneos/
│       │       ├── App.java                       # Punto de entrada de la aplicación
│       │       ├── controller/                    # Controladores JavaFX (FXML)
│       │       ├── model/                         # Clases de datos (Equipo, Torneo, Logro)
│       │       ├── service/                       # Lógica del negocio y persistencia
│       │       └── util/                          # Utilidades, animaciones, control de flujo
│       │
│       └── resources/
│           └── cr/ac/una/tareatorneos/
│               ├── view/                          # Vistas FXML
│               └── resources/                     # Imágenes, íconos y estilos CSS
│
├── data/                                          # Datos persistentes en formato JSON
├── pom.xml                                        # Configuración del proyecto Maven
└── README.md                                      # Documentación del proyecto
```
---

## Logros del Torneo

| Logro               | Descripción                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| Máxima Potencia     | Anotar 20 o más goles en un torneo y ganar el partido donde se cumple.     |
| Muralla Imbatible   | Ganar un torneo sin recibir goles.                                          |
| Imparable           | Ganar 3 partidos consecutivos.                                              |
| Equilibrio Perfecto | Ganar 5 partidos tras empate en tiempo regular.                             |
| Dominador Supremo   | Ganar 8 torneos.                                                            |
| Leyenda Plateada    | Ganar 6 torneos.                                                            |
| Tricampeón          | Ganar 3 torneos.                                                            |
| Regreso Triunfal    | Ganar un torneo después de perder el anterior.                              |
| Campeón Inaugural   | Ganar el primer torneo en el que se participa.                              |

---

## Dependencias Usadas

- `javafx-controls`, `javafx-fxml` (23.0.1)
- `materialfx` (11.16.1)
- `jackson-databind`, `jackson-datatype-jsr310` (2.16.1)
- `webcam-capture` (0.3.12)
- `slf4j-api`, `slf4j-simple` (1.7.36)
- `itextpdf` (7.2.3 – módulos `kernel`, `layout`, `io`)

---

## Detalles Técnicos

- Persistencia: se guardan torneos, equipos, estadísticas y logros usando archivos `.json`.
- Control de flujo: se navega entre vistas usando `FlowController.java`.
- Animaciones: se gestionan en `AnimationDepartment.java` y se muestran con `UnlockAchievementController.java`.
- Diseño desacoplado: cada vista tiene su propio FXML y controlador.

---

## Información Académica

*Estudiantes Autores:*
  - Isaac Alvarado Mata  
  - Matiw Rivera Cascante  
  - Emmanuel Gamboa Retana

- Curso: Programación 2  
- Profesor: Carlos Carranza  
- Universidad Nacional, Sede Region Branca, PZ,  Costa Rica

---

## Licencia

Este proyecto fue creado únicamente con fines educativos. Todos los derechos pertenecen a los autores mencionados.

---