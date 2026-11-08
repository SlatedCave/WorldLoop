# Worldloop — Minecraft 26.3 (Fabric)

Worldloop is an early prototype for a Minecraft world that looks flat but wraps around horizontally. It changes player coordinates at the edges; it does **not** turn Minecraft into a rendered sphere.

## Current status

### Implemented in this prototype

- Overworld players walking on foot wrap across the east/west and north/south coordinate edges.
- The horizontal period is **40,075,000 blocks** in both X and Z. Assuming roughly one metre per block, a full lap in either direction is approximately 40,075 km.
- The valid coordinate interval on each axis is `[-20,037,500, 20,037,500)`.
- The wrap runs on the logical server, including an integrated singleplayer server.
- Coordinate wrapping is isolated in a small math helper and has automated edge-case tests.

### Not implemented yet

- Periodic terrain generation. Opposite edges do not currently generate matching terrain.
- Seamless chunk handoff. Crossing an edge is a server-side position teleport and may cause chunks to load on the other side.
- Wrapping for mobs, dropped items, projectiles, boats, minecarts, horses, or other non-player entities.
- Player wrapping while riding. Mounted players are deliberately skipped so the player is not separated from their vehicle.
- Nether/End wrapping, maps, compasses, structures, portals, commands, or multiplayer proximity across opposite edges.
- Configuration UI or per-world saved circumference settings.

This is a prototype, not a finished planet mod. Test it in a **new Superflat world** and back up worlds before trying it in a real save.

## Requirements

- Minecraft Java Edition **26.3**
- Fabric Loader **0.19.5 or newer**
- Fabric API **0.165.0+26.3 or newer for 26.3**
- Java Development Kit **25**
- Gradle **9.6.0** (the included launcher scripts download this version if it is not already present in the project's Gradle distribution cache)

The project targets Mojang's unobfuscated class names used by Minecraft 26.3 and uses the current Fabric Loom plugin version configured in `gradle.properties`.

## Build automatically with GitHub Actions (no local Java install needed)

This repository includes `.github/workflows/build.yml`. GitHub's hosted runner installs Java 25, runs the coordinate tests, compiles the mod, and uploads the compiled JAR as a downloadable workflow artifact.

1. Create a repository on GitHub.
2. Add the contents of this project folder to the repository root, including the `.github` folder. GitHub Desktop is a convenient way to commit and push the extracted folder.
3. Push the files to GitHub. The workflow starts automatically. You can also open the repository's **Actions** tab and run **Build Worldloop** manually with **Run workflow**.
4. Open the completed workflow run. Under **Artifacts**, download `worldloop-minecraft-26.3`.
5. Extract the downloaded artifact and put `worldloop-0.1.0.jar` into your Minecraft 26.3 Fabric instance's `mods` folder alongside Fabric API.

A successful workflow means the project passed its automated tests and Gradle build. It does not prove that every in-game wrapping behavior works; test in a new world and keep backups.

## Build on Windows

1. Install a JDK 25 distribution and make sure `java -version` reports Java 25.
2. Extract this folder.
3. Open Command Prompt or PowerShell in the `Worldloop-26.3` folder.
4. Run:

   ```bat
   gradlew.bat build
   ```

The included `gradlew.bat` is a small bootstrap script: it downloads Gradle 9.6.0 and then runs the build. It does **not** depend on `gradle-wrapper.jar`.

The built mod JAR will be in `build\libs\`.

## Build on macOS/Linux

Make the launcher executable once, then build:

```sh
chmod +x gradlew
./gradlew build
```

The launcher downloads Gradle 9.6.0 if needed. The built JAR will be in `build/libs/`.

## Run tests

```bat
gradlew.bat test
```

The tests cover the exact positive/negative boundaries, multiple full laps, interval guarantees, and invalid helper inputs. They test the coordinate math only; they do not replace an in-game Fabric test.

## In-game test

1. Install Fabric Loader 0.19.5+ for Minecraft 26.3 and Fabric API 0.165.0+26.3.
2. Build Worldloop and put the resulting `worldloop-0.1.0.jar` in that instance's `mods` folder.
3. Create a **new Superflat world** with cheats enabled.
4. Make sure the world border is centered at `(0, 0)` and is larger than 40,075,000 blocks in diameter. For a simple test, run:

   ```mcfunction
   /worldborder center 0 0
   /worldborder set 45000000
   ```

5. Test east/west wrapping:

   ```mcfunction
   /tp @s 20037499 100 0
   ```

   Walk east across X = 20,037,500. You should wrap to the negative edge.

   ```mcfunction
   /tp @s -20037500 100 0
   ```

   Walk west. You should wrap to the positive edge.

6. Repeat on the Z axis by teleporting to `0 100 20037499` and `0 100 -20037500`.

A world border smaller than the wrap diameter, or one moved away from `(0, 0)`, can stop players before they reach the wrapping point. The command above gives the test world extra clearance.

## Change the circumference

Edit `WORLD_WIDTH_BLOCKS` in `WorldLoopMod.java`. It must be a positive, even number. Keep half the width below Minecraft's horizontal coordinate limit. Rebuild the JAR after changing it.

## Geometry and scale notes

- This is a flat **torus-like coordinate system**, not a sphere. It has no poles, curved horizon, or planet-centred gravity.
- Both horizontal axes currently use Earth's approximate **equatorial** circumference for a simple square test layout. This is a gameplay approximation, not an exact map projection of Earth.
- The terrain generator still receives large world coordinates. This prototype does not correct high-coordinate terrain precision or create matching terrain at opposite edges.
- Minecraft continues to generate/load chunks normally near the player; Worldloop does not pre-generate or keep the entire 40-million-by-40-million-block area in memory.

## Development roadmap

1. Verify player wrapping in Superflat.
2. Add safe wrapping for vehicles and passenger trees.
3. Add server-side entity wrapping.
4. Design periodic biome, terrain, structure, and feature generation so opposite edges match.
5. Improve chunk handoff and coordinate-aware maps/compasses.
6. Add configurable dimensions and persistent per-world settings.

Worldloop is an experimental community project and is not an official Minecraft product.
