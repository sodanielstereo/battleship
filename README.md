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

## Desarrollado por los integrantes

- Juan Pablo Lozano Restrepo - 2521505
- Daniel Fernando Vallejo Cabrera - 2343154

## Estado actual

Hasta el momento el proyecto ya tiene:

- [x] Proyecto Maven configurado.
- [x] Aplicación JavaFX funcionando.
- [x] Estructura inicial MVC.
- [x] Modelo base del juego.
- [x] Tablero 10x10 en lógica.
- [x] Barcos con tamaños correctos.
- [x] Jugador real y jugador máquina.
- [x] Lógica de colocación de barcos.
- [x] Lógica de disparos.
- [x] Estados de agua, tocado y hundido.
- [x] Cambio de turnos.
- [x] Validaciones principales.
- [x] Excepciones personalizadas.
- [x] Estructuras de datos aplicadas.
- [x] Pruebas unitarias.
- [x] Pantalla inicial con nickname.
- [x] Vista inicial con dos tableros 10x10.

## Funcionalidad actual

Por ahora la aplicación permite:

1. Abrir la pantalla inicial.
2. Ingresar un nickname.
3. Crear una partida nueva.
4. Pasar a una pantalla con dos tableros:
   - Tablero del jugador.
   - Tablero enemigo.
5. Volver a la pantalla inicial.

La lógica del juego ya existe en los servicios, pero todavía falta conectarla completamente con la interfaz.

## Estructura general

```text
src/main/java/com/battleship
├── app
├── controller
├── exception
├── model
│   ├── board
│   ├── enums
│   ├── history
│   ├── player
│   └── ship
└── service
```

## Estructuras de datos usadas

| Estructura              | Uso                                 |
| ----------------------- | ----------------------------------- |
| `Map<Coordinate, Cell>` | Tablero del juego.                  |
| `List<Ship>`            | Flota de cada jugador.              |
| `Queue<Coordinate>`     | Disparos disponibles de la máquina. |
| `Deque<ShotRecord>`     | Historial de disparos.              |

## Excepciones propias

El proyecto usa excepciones personalizadas para que los errores sean más claros:

- `InvalidPlacementException`
- `InvalidShotException`
- `InvalidGameStateException`

## Pruebas

Para ejecutar las pruebas:

```bash
mvn clean test
```

Actualmente hay pruebas para:

- Tablero.
- Coordenadas.
- Barcos.
- Colocación.
- Disparos.
- Turnos.
- Victoria.
- Excepciones.
- Estructuras de datos.

## Ejecutar el proyecto

Compilar:

```bash
mvn clean compile
```

Ejecutar pruebas:

```bash
mvn clean test
```

Ejecutar la app:

```bash
mvn javafx:run
```

## Falta por hacer

- [ ] Conectar los clics del tablero enemigo con la lógica de disparos.
- [ ] Mostrar visualmente agua, tocado y hundido.
- [ ] Colocar barcos visualmente.
- [ ] Implementar turno automático de la máquina.
- [ ] Usar hilos para mejorar la experiencia del turno de la máquina.
- [ ] Guardar nickname y barcos hundidos en archivo plano.
- [ ] Guardar y cargar partida con serialización.
- [ ] Aplicar patrones de diseño obligatorios.
- [ ] Mejorar figuras 2D o Canvas.
- [ ] Ampliar pruebas unitarias.
- [ ] Completar Javadoc.
- [ ] Hacer revisión final contra el PDF.

## Patrones planeados

El enunciado pide patrones diferentes a MVC y Singleton. Los que pensamos usar son:

| Patrón         | Tipo           | Uso                                              |
| -------------- | -------------- | ------------------------------------------------ |
| Factory Method | Creacional     | Crear barcos según su tipo.                      |
| Adapter        | Estructural    | Adaptar el tablero del modelo a la vista JavaFX. |
| Strategy       | Comportamiento | Manejar la estrategia de disparo de la máquina.  |

## Nota

El proyecto se está desarrollando paso a paso por ramas, commits y Pull Requests, para que se evidencie la planeación del trabajo
y el trabajo colaborativo sea más eficiente, organizado y fácil.
