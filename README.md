# Naval Battle Game

<p align="center">
    <img src="./public/mainview.png" alt="Naval Battle main screen">
</p>

<p align="center">
    <img src="./public/battleship.png" alt="Naval Battle gameplay screen">
</p>

Naval Battle Game is an interactive desktop application inspired by the classic Battleship game.

The project was developed with **Java 17**, **JavaFX**, **Maven**, **JUnit 5**, **CSS** and **Git/GitHub** as part of an academic project for Event-Oriented Programming.

By:

- Juan Pablo Lozano Restrepo - 2521505
- Daniel Fernando Vallejo Cabrera - 2343154

The game allows the player to place a complete fleet, confirm the ship selection, attack the enemy board, save and load the game, and play against an artificial opponent that uses a smart shooting strategy.

## 🗝️ Key Features

- **Interactive Gameplay**: Classic Battleship mechanics adapted to a JavaFX desktop application.
- **Human vs Machine Mode**: The player competes against an artificial opponent.
- **Fleet Placement System**:
  - Select ships with left click.
  - Rotate the selected ship with right click.
  - Place ships on the player board.
  - Move and rotate ships before confirming the fleet.
- **Manual Fleet Confirmation**: The game only starts when the player confirms the ship selection.
- **Machine Fleet Placement**: The machine places its fleet automatically.
- **Smart Machine Strategy**: The artificial player uses the Strategy pattern to prioritize shots around previously hit cells.
- **Background Machine Turn**: The machine turn runs using a JavaFX `Task`, keeping the user interface responsive.
- **Safe Ocean Wave Animation**:
  - The sea cells use an animated ocean sprite sheet.
  - The animation is handled with a JavaFX `Timeline`.
  - The sprite sheet is cut into frames once using `PixelReader` and `WritableImage`.
  - The animated water texture is applied with `ImagePattern`.
  - Hidden enemy ships are not revealed by the animation.
- **Save and Resume**:
  - The complete game state is saved using serialization.
  - Player statistics are stored in a flat file.
  - A saved game can be loaded from the main screen.
  - Finished games are not loaded again as active matches.
- **Instructions Dialog**: The main screen includes an instructions button that explains how to play.
- **Custom Visual Design**:
  - Dark naval HUD interface.
  - Custom backgrounds for the main screen and battle screen.
  - Application icon.
  - Custom fonts.
  - Styled buttons, cards, panels and board cells.
  - Hover highlight for board interaction.
  - Ship and shot sprites.
- **Unit Testing**: The project includes unit tests for model, services, persistence and strategy.

## 💻 Tech Stack

- ☕ [Java](https://docs.oracle.com/en/java/) - Main programming language.
- 🏖️ [JavaFX](https://openjfx.io/) - Framework for GUI development.
- 📦 [Maven](https://maven.apache.org/) - Build and dependency management tool.
- 🧪 [JUnit 5](https://junit.org/junit5/) - Unit testing framework.
- 🎨 [CSS](https://www.w3.org/Style/CSS/) - Custom visual styling.
- ✏️ [Git](https://git-scm.com/) and GitHub - Version control and Pull Request workflow.

## 📁 Project Structure

```text
src/main/java/com/battleship
├── animation
├── app
├── controller
├── exception
├── model
│   ├── board
│   ├── enums
│   ├── history
│   ├── player
│   └── ship
│       └── factory
├── persistence
├── service
├── strategy
└── util
```

```text
src/main/resources/com/battleship
├── backgrounds
├── fonts
├── icons
├── sprites
├── styles
└── view
```

```text
src/test/java/com/battleship
├── model
├── persistence
├── service
└── strategy
```

## 🎮 How to Play

1. Enter a nickname on the main screen.
2. Click **Nueva partida** to start a new game.
3. Select a ship from the available ships panel.
4. Use left click to place the ship on your board.
5. Use right click to rotate the selected ship.
6. You can move or rotate your ships before starting the battle.
7. Once all ships are placed, click **Confirmar selección**.
8. Attack the enemy board by clicking on a cell.
9. If you hit or sink a ship, you keep your turn.
10. If your shot lands in water, the turn passes to the machine.
11. The winner is the player who sinks the entire enemy fleet first.

## 🧭 Controls

| Action | Control |
| --- | --- |
| Select a ship | Left click on an available ship |
| Rotate selected ship | Right click |
| Place selected ship | Left click on the player board |
| Move an already placed ship | Drag and drop |
| Rotate an already placed ship | Right click on the placed ship |
| Shoot | Left click on the enemy board |
| Save game | Click **Guardar partida** |
| Load game | Click **Cargar partida** on the main screen |
| View instructions | Click **Instrucciones** on the main screen |
| Reveal machine ships during placement | Use the verification checkbox |

## 🚢 Fleet

The game uses the classic Battleship fleet distribution:

| Ship | Quantity | Size |
| --- | ---: | ---: |
| Aircraft Carrier | 1 | 4 |
| Submarine | 2 | 3 |
| Destroyer | 3 | 2 |
| Frigate | 4 | 1 |

## 🌊 Ocean Wave Animation

The board includes an animated water effect using a sprite sheet:

```text
Ocean_SpriteSheet.png
```

The sprite sheet contains four horizontal frames of 64x64 pixels each. The animation system loads the image once, cuts the frames using `PixelReader` and `WritableImage`, and updates the sea-cell fill using `ImagePattern`.

The animation is designed as a visual effect only. It does not give information about the machine fleet:

- Empty sea cells are animated.
- Hidden enemy ship cells are also shown as animated water when ships are not revealed.
- Visible ships are not covered by the ocean animation.
- Shot-result cells keep their own visual state.

## 💾 Persistence

The project uses two persistence mechanisms:

- **Serialization**: Saves and loads the complete game state.
- **Flat File**: Stores basic player statistics.

Generated files are stored in the `data` folder.

The serialized file stores the current match state, including players, boards, ships, turn, phase and shot history.

The flat file stores basic player statistics such as nickname, game phase and sunk ships.

The persistence layer also uses an adapter so the controller does not need to work directly with both persistence services.

## 🧠 Design Patterns Implemented

The project implements three design patterns different from MVC and Singleton.

### 1. Factory Method - Creational Pattern

The Factory Method pattern is used to create ships without coupling the controller or service logic to concrete ship classes.

```text
ShipFactory
├── AircraftCarrierFactory
├── SubmarineFactory
├── DestroyerFactory
└── FrigateFactory

ShipFactoryRegistry
```

Instead of creating ships directly with `new`, the game uses the factory registry to request a ship based on its `ShipType`.

This improves extensibility because new ship types can be added by creating a new factory and registering it.

### 2. Adapter - Structural Pattern

The Adapter pattern is used in the persistence layer.

```text
GamePersistenceTarget
└── GamePersistenceAdapter
    ├── GameStatePersistenceService
    └── PlayerStatsFileService
```

The adapter exposes a single persistence interface to the controller while internally coordinating two different persistence mechanisms:

- Binary serialization for complete game state.
- Flat file storage for player statistics.

This reduces coupling between the JavaFX controller and the concrete persistence services.

### 3. Strategy - Behavioral Pattern

The Strategy pattern is used for the artificial player's shooting behavior.

```text
ShotStrategy
├── RandomShotStrategy
└── SmartShotStrategy
```

This allows the machine to change its shooting behavior without modifying the main game service or the JavaFX controller.

The smart strategy prioritizes shots around previously hit cells. If no useful target is found, it falls back to available random shots.

## 🧪 Tests

The project includes unit tests for:

- Board creation.
- Coordinates.
- Data structures.
- Ships.
- Ship placement.
- Shooting logic.
- Turn handling.
- Game state validation.
- Custom exceptions.
- Serialization.
- Flat file persistence.
- Smart shot strategy.

Current test suite:

```text
Tests run: 37
Failures: 0
Errors: 0
Skipped: 0
```

To run the tests:

```bash
mvn clean test
```

## 📚 Javadoc

The project includes Javadoc documentation for the main Java classes, controllers, services, model objects, persistence layer and design pattern components.

To generate the Javadoc:

```bash
mvn javadoc:javadoc
```

Depending on the Maven plugin output, the generated documentation can be found in one of these folders:

```text
target/site/apidocs
target/reports/apidocs
```

## 🚀 How to Run

1. Clone the repository:

```bash
git clone https://github.com/sodanielstereo/battleship.git
```

2. Enter the project folder:

```bash
cd battleship
```

3. Compile the project:

```bash
mvn clean compile
```

4. Run the tests:

```bash
mvn clean test
```

5. Run the application:

```bash
mvn javafx:run
```

## 🖼️ Screenshots

The screenshots used in this README must be stored in the `public` folder at the root of the project:

```text
battleship
├── public
│   ├── mainview.png
│   └── battleship.png
├── src
├── pom.xml
└── README.md
```

## 🗺️ Future Ideas

- Add sound effects for shots, hits and sunk ships.
- Add more visual animations for attacks and machine turns.
- Add a local multiplayer mode.
- Improve the statistics screen with more detailed game history.
- Add difficulty levels for the artificial player.
- Package the game as an executable JAR or installer.

## ✅ Current Status

The project currently compiles, runs and includes automated tests.

It includes core gameplay, JavaFX interface, persistence, custom exceptions, data structures, Factory Method, Adapter, Strategy, background machine turns, animated ocean tiles, custom visual assets, Javadoc documentation and unit testing.

## Source Icons

The icons for shooting, hitting and sinking were taken from:

```text
https://icons8.com
```
