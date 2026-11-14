# Easy Harvest

A small Fabric mod for Minecraft **26.3**. Right-click a fully grown crop to harvest it and replant it in one step.

## Features

- Right-click a **fully grown** crop: it drops its normal loot and replants itself at age 0.
- Works on wheat, carrots, potatoes, beetroot and nether wart.
- One seed (or one carrot, potato or nether wart) from the drops is used for replanting.
- Unripe crops behave exactly as in vanilla.
- Works in multiplayer. All logic runs on the server, so only the server needs the mod.

## Requirements

- Minecraft 26.3
- [Fabric Loader](https://fabricmc.net/) 0.20.1 or newer
- [Fabric API](https://modrinth.com/mod/fabric-api) for 26.3
- Java 25

## Installation

1. Install Fabric Loader and Fabric API for Minecraft 26.3.
2. Download `easy-harvest-1.0.0.jar` and put it in your `mods` folder.
3. Start the game. For a server, put the jar in the server's `mods` folder.

## Building from source

You need a JDK 25 (the full JDK, not just the runtime).

```bash
./gradlew build
```

The mod jar is created at `build/libs/easy-harvest-1.0.0.jar`. Ignore the `-sources` jar.

## How it works

The mod registers a Fabric API `UseBlockCallback` event instead of using a mixin. On a right-click with the main hand it checks whether the block is a supported, fully grown crop. If so, the server rolls the block's normal loot, removes one seed for replanting, drops the rest and resets the crop to age 0. Anything else is passed back to vanilla behavior.

The code is in [`EasyHarvest.java`](src/main/java/com/easyharvest/EasyHarvest.java).

## License

CC0-1.0. See [LICENSE](LICENSE).
