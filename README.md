# Batalla Naval

Proyecto académico de **Fundamentos de Programación Orientada a Eventos**.

La idea es desarrollar el juego **Batalla Naval** en Java, usando JavaFX, Maven, POO, MVC, estructuras de datos, excepciones, pruebas unitarias y trabajo por ramas/Pull Requests en GitHub.

## Tecnologías

- Java 17
- JavaFX
- Maven
- JUnit 5
- Git y GitHub
- FXML
- CSS
- Archivos planos
- Serialización

## Desarrollado por los integrantes

- Juan Pablo Lozano Restrepo - 2521505
- Daniel Fernando Vallejo Cabrera - 2343154

## Estado actual

El proyecto actualmente compila, corre y pasa las pruebas unitarias.

Comandos usados para verificar:

```bash
mvn clean compile
mvn clean test
mvn javafx:run
```

Resultado actual de pruebas:

```text
Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
```

## Funcionalidad actual

Hasta este punto la aplicación permite:

- Crear una partida con nickname.
- Entrar a la pantalla de batalla.
- Ver el tablero del jugador y el tablero enemigo.
- Seleccionar una nave disponible con click izquierdo.
- Cambiar la orientación de la nave seleccionada con click derecho.
- Ubicar la nave seleccionada con click izquierdo sobre el tablero.
- Reubicar naves ya posicionadas.
- Rotar naves ya ubicadas con click derecho.
- Confirmar la selección de naves antes de iniciar la partida.
- Disparar al tablero enemigo.
- Mostrar agua, tocado y hundido.
- Ejecutar el turno de la máquina.
- Mostrar el estado actual de la partida.
- Detectar victoria o finalización.
- Guardar la partida desde la interfaz.
- Cargar una partida guardada desde la pantalla inicial.
- Guardar información básica del jugador en archivo plano.
- Guardar y cargar el estado de la partida usando serialización.

## Partes principales

### Modelo

Contiene las clases principales del juego:

- `Game`
- `Board`
- `Cell`
- `Coordinate`
- `Player`
- `RealPlayer`
- `ArtificialPlayer`
- `Ship`
- `AircraftCarrier`
- `Submarine`
- `Destroyer`
- `Frigate`
- `ShotRecord`

### Controladores

Conectan la interfaz gráfica con la lógica del juego:

- `MainController`
- `BattleController`

### Servicios

Manejan la lógica principal:

- `GameService`
- `PlacementService`
- `ShotService`

### Persistencia

Se agregaron servicios para manejar archivos:

- `GameStatePersistenceService`
- `PlayerStatsFileService`

`GameStatePersistenceService` guarda y carga la partida usando serialización.

`PlayerStatsFileService` guarda datos básicos del jugador en un archivo plano.

## Estructuras de datos usadas

| Estructura              | Uso                                            |
| ----------------------- | ---------------------------------------------- |
| `Map<Coordinate, Cell>` | Representa el tablero del juego.               |
| `List<Ship>`            | Guarda la flota de cada jugador.               |
| `Queue<Coordinate>`     | Maneja los disparos disponibles de la máquina. |
| `Deque<ShotRecord>`     | Guarda el historial de disparos.               |

## Excepciones propias

El proyecto usa excepciones personalizadas para manejar errores del juego de forma más clara:

- `InvalidPlacementException`
- `InvalidShotException`
- `InvalidGameStateException`

## Persistencia

El proyecto ya tiene persistencia conectada con la interfaz.

### Archivo plano

Se usa para guardar información básica de la partida:

- Fecha.
- Nickname.
- Barcos enemigos hundidos.
- Barcos propios hundidos.
- Fase de la partida.

### Serialización

Se usa para guardar y cargar el estado completo de la partida, incluyendo:

- Jugador real.
- Jugador máquina.
- Tableros.
- Barcos.
- Fase de la partida.
- Turno actual.
- Historial de disparos.

Los archivos generados se guardan en la carpeta `data/`.

## Sistema de posicionamiento

El sistema de posicionamiento fue ajustado para que sea más cómodo para el usuario.

Flujo actual:

1. Click izquierdo sobre una nave disponible para seleccionarla.
2. Click derecho para cambiar su orientación.
3. Click izquierdo sobre el tablero para ubicarla.
4. Click derecho sobre una nave ya ubicada para rotarla.
5. Después de ubicar todas las naves, el usuario puede seguir acomodándolas.
6. El juego solo inicia cuando se presiona el botón de confirmar selección.

Esto evita que la partida inicie automáticamente apenas se ubiquen todas las naves.

## Pruebas unitarias

El proyecto usa JUnit 5.

Actualmente hay pruebas para:

- Creación del tablero.
- Coordenadas.
- Barcos.
- Colocación de barcos.
- Disparos.
- Cambio de turnos.
- Detección de victoria.
- Excepciones personalizadas.
- Estructuras de datos.
- Guardado y carga con serialización.
- Guardado de estadísticas en archivo plano.

Para ejecutar las pruebas:

```bash
mvn clean test
```

## Ejecutar el proyecto

Compilar:

```bash
mvn clean compile
```

Ejecutar pruebas:

```bash
mvn clean test
```

Ejecutar la aplicación:

```bash
mvn javafx:run
```

## Pull Requests trabajados

| PR    | Estado     | Descripción                                                                  |
| ----- | ---------- | ---------------------------------------------------------------------------- |
| PR 1  | Hecho      | Configuración inicial Maven + JavaFX.                                        |
| PR 2  | Hecho      | Modelo base del dominio.                                                     |
| PR 3  | Hecho      | Lógica central del juego.                                                    |
| PR 4  | Hecho      | Excepciones + estructuras de datos.                                          |
| PR 5  | Hecho      | Formato y limpieza.                                                          |
| PR 6  | Hecho      | Pantalla inicial JavaFX.                                                     |
| PR 7  | Hecho      | Vista inicial de tableros.                                                   |
| PR 8  | Hecho      | Interacción inicial con tableros.                                            |
| PR 9  | Hecho      | Simulación funcional inicial del juego.                                      |
| PR 10 | Hecho      | Panel de barcos disponibles.                                                 |
| PR 11 | Hecho      | Sistema de barcos y ajustes visuales.                                        |
| PR 12 | Hecho      | Correcciones sobre el sistema de barcos.                                     |
| PR 13 | En proceso | Persistencia, guardar/cargar partida y mejora del posicionamiento con mouse. |

## Checklist del proyecto

- [x] Proyecto Maven configurado.
- [x] JavaFX funcionando.
- [x] Estructura MVC inicial.
- [x] Modelo del juego.
- [x] Tableros 10x10.
- [x] Jugador real.
- [x] Jugador máquina.
- [x] Barcos con tamaños correctos.
- [x] Colocación de barcos.
- [x] Cambio de orientación.
- [x] Reubicación de barcos.
- [x] Confirmación manual de selección de naves.
- [x] Lógica de disparos.
- [x] Agua, tocado y hundido.
- [x] Cambio de turnos.
- [x] Detección de victoria.
- [x] Excepciones personalizadas.
- [x] Estructuras de datos.
- [x] Pruebas unitarias.
- [x] Archivo plano.
- [x] Serialización.
- [x] Guardar partida desde la interfaz.
- [x] Cargar partida desde la interfaz.
- [ ] Mejorar el turno de la máquina con hilos.
- [ ] Aplicar patrones de diseño obligatorios.
- [ ] Mejorar figuras 2D o Canvas.
- [ ] Completar Javadoc.
- [ ] Revisión final contra el PDF del enunciado.

## Patrones de diseño planeados

El enunciado pide patrones diferentes a MVC y Singleton.

Los patrones planeados son:

| Patrón         | Tipo           | Uso                                             |
| -------------- | -------------- | ----------------------------------------------- |
| Factory Method | Creacional     | Crear barcos según su tipo.                     |
| Adapter        | Estructural    | Adaptar el tablero del modelo a la vista.       |
| Strategy       | Comportamiento | Manejar la estrategia de disparo de la máquina. |
