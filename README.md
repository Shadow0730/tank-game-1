# Tank Game

A 2D turn-based tank battle game built with Java, LibGDX, Box2D, and Tiled maps. Two players control tanks, aim with the mouse, choose bullet types, adjust shot power, collect powerups, and try to destroy the other tank.

## Features

- Main menu with start, load, and exit buttons
- Two-player tank battle screen
- Box2D physics for tanks, wheels, projectiles, terrain, and collisions
- Mouse-controlled turret aiming
- Projectile shooting with selectable bullet types
- Bullet selection UI with icon buttons
- Power slider for adjusting shot strength
- Health bars above tanks
- Randomly spawned powerup crates
- Save and load game state with `savegame.json`
- In-game menu with save and exit options
- Tiled map rendering through LibGDX

## Controls

| Action | Control |
| --- | --- |
| Move forward | `W` or `Up Arrow` |
| Move backward | `S` or `Down Arrow` |
| Aim turret | Move mouse |
| Shoot | Left mouse click or `F` |
| Change turn | `Space` |
| Exit game | `Esc` |
| Select bullet | Click bullet icon |
| Change power | Drag power slider |

## Project Structure

```text
tank game/
  assets/                         Game images, maps, UI assets, and save data resources
  core/src/main/java/com/mygdx/PvsS/
    tankgame.java                  Main LibGDX game class
    screens/
      MainMenuScreen.java          Start/load/exit menu
      GameScreen.java              Main gameplay screen
      endGameScreen.java           Winner screen
    tanks/
      car.java                     Tank body, controls, turret, HP, and firing
      turret.java                  Projectile manager
      projectile.java              Bullet physics and rendering
    helpers/
      map.java                     Tiled map setup
      powerup.java                 Powerup crate body and sprite
      SaveGameManager.java         JSON save/load helper
      gamesavedata.java            Save data model
      worldContactListner.java     Box2D collision handling
      constants.java               Shared constants such as PPM
  lwjgl3/                          Desktop launcher project
```

## Running the Game

From the project root:

```bash
./gradlew lwjgl3:run
```

On Windows PowerShell:

```powershell
.\gradlew.bat lwjgl3:run
```

The first run may download Gradle and LibGDX dependencies, so an internet connection may be required.

## Building a Desktop Jar

```bash
./gradlew lwjgl3:jar
```

Windows-only jar:

```bash
./gradlew lwjgl3:jarWin
```

The output is created under:

```text
lwjgl3/build/libs/
```

## Save System

The game saves player positions, health, and current turn into:

```text
savegame.json
```

Use the in-game menu to save. Use the main menu load button to continue from an existing save file.

## Assets

Important asset folders:

```text
assets/bullet_icons/    Bullet selection icons
assets/powerups/        Powerup crate images
assets/ui/              UI buttons and slider assets
assets/Tanks/           Tank body and turret textures
```

External asset credit:

- Tiled map sprite sheet: https://kenney.nl/assets/new-platformer-pack

## Notes

- The desktop launcher is in the `lwjgl3` module.
- Shared gameplay code is in the `core` module.
- `PPM` is currently defined in `helpers/constants.java` and controls pixel-to-Box2D scaling.
- The game uses LibGDX internal asset paths, so files referenced in code must exist under the `assets` folder.
