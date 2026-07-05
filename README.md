# Per Player Insomnia

Server-side Fabric mod for Minecraft **26.2** that gives admins per-player control over phantom spawning. Players with the right permissions can toggle phantoms for themselves.

No client mod is required — install it on the server only.

## Features

- Enable or disable phantom spawning on a per-player basis
- Admin commands to view and change any player's setting
- Permission nodes for [LuckPerms](https://luckperms.net/) (or any mod using [fabric-permissions-api](https://github.com/lucko/fabric-permissions-api))
- Settings persist across death, logout, and server restarts
- Configurable server default for new players
- Respects the vanilla `spawnPhantoms` gamerule — if phantoms are disabled globally, no one gets them regardless of individual settings

## Requirements

| Component | Version |
|-----------|---------|
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3+ |
| Fabric API | 0.154.0+26.2 |
| Java (server) | 25+ |

LuckPerms is optional. Without a permissions plugin, all players can toggle their own phantoms and operators can use admin commands. With LuckPerms installed, permission nodes control access instead.

## Installation

1. Download the latest release JAR (or build from source — see below).
2. Place `perplayerinsomnia-*.jar` in your server's `mods/` folder alongside Fabric Loader and Fabric API.
3. Restart the server.

`fabric-permissions-api` is bundled inside the mod JAR, so you do not need to install it separately.

## Commands

All commands are under `/phantoms`.

### Player commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/phantoms` | `perplayerinsomnia.toggle` or `perplayerinsomnia.admin` | Hidden from tab completion without either node |
| `/phantoms status` | `perplayerinsomnia.toggle` or `perplayerinsomnia.admin` | Check your phantom spawning status |
| `/phantoms enable` | `perplayerinsomnia.toggle` | Enable phantoms for yourself |
| `/phantoms disable` | `perplayerinsomnia.toggle` | Disable phantoms for yourself |
| `/phantoms toggle` | `perplayerinsomnia.toggle` | Toggle your setting |

### Admin commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/phantoms set <player> enable` | `perplayerinsomnia.admin` | Enable phantoms for a player |
| `/phantoms set <player> disable` | `perplayerinsomnia.admin` | Disable phantoms for a player |
| `/phantoms get <player>` | `perplayerinsomnia.admin` | Check a player's setting |
| `/phantoms reset <player>` | `perplayerinsomnia.admin` | Reset a player to the server default |
| `/phantoms default get` | `perplayerinsomnia.admin` | Show the server default |
| `/phantoms default set <true\|false>` | `perplayerinsomnia.admin` | Set the server default |

### Without LuckPerms

| Command | Who can use it |
|---------|----------------|
| `/phantoms` | Hidden unless the player can use at least one subcommand |
| `/phantoms status` | Everyone who can see `/phantoms` |
| `/phantoms enable`, `disable`, `toggle` | Everyone who can see `/phantoms` |
| Admin subcommands | Operators (level 2+) |

### With LuckPerms

Permission nodes use the mod id prefix `perplayerinsomnia`, not the command name `phantoms`. In the LuckPerms editor, look under **perplayerinsomnia** → **toggle** / **admin**.

| Node | Description | Default |
|------|-------------|---------|
| `perplayerinsomnia.toggle` | Allows a player to use `/phantoms enable`, `disable`, and `toggle` | Operators (level 2+) |
| `perplayerinsomnia.admin` | Allows all admin subcommands | Operators (level 2+) |

### LuckPerms examples

```bash
# Let all players toggle their own phantoms
/lp group default permission set perplayerinsomnia.toggle true

# Grant admin commands to moderators
/lp group moderator permission set perplayerinsomnia.admin true
```

## Configuration

Settings are stored in `config/perplayerinsomnia.json`:

```json
{
  "defaultEnabled": true,
  "playerSettings": {
    "uuid-here": false
  }
}
```

- `defaultEnabled` — whether phantoms spawn for players who have no individual override (default: `true`)
- `playerSettings` — per-player overrides keyed by UUID; managed automatically by commands

Player overrides are also saved in player data, so they survive death and dimension changes.

## How it works

When a player has phantoms disabled, the mod intercepts the phantom spawner and prevents the insomnia check from passing for that player. Other players on the same server are unaffected — one player can farm phantom membranes while another sleeps peacefully.

## Building from source

Requires **Java 25**.

```bash
./gradlew build
```

The built JAR will be at `build/libs/perplayerinsomnia-1.0.0.jar`.

If your system default Java is not 25, set `JAVA_HOME` explicitly:

```bash
JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew build
```

## License

MIT — see [LICENSE](LICENSE).