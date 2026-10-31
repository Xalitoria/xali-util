# xali-util

**xali-util** is the library the Xali plugins share: Bukkit helpers, message sending and formatting, YAML configuration
files, kotlinx.serialization serializers for Bukkit and JDK types, and Brigadier command helpers. It is published as
`mc.xalitoria:util` and targets Java 11.

## Installation

### Using the library

The plugins use `util` as a published artifact, declared in the version catalog (`libs.xalicraft.util` in
`libs.versions.toml`). A plugin lists it with `api(...)`, so the server downloads it when the plugin loads (see the
[Gradle plugin](../plugin/README.md#loading-libraries)):

**File** `chat/paper/build.gradle.kts`

```kotlin
dependencies {
    api(libs.xalicraft.util)
}
```

`util` itself uses [`color`](../color/README.md) as a published artifact (`libs.xalicraft.color`).

> [!IMPORTANT]
> A change to `util` reaches the plugins only after it is published, because they depend on the coordinates and not on
> the project. To try a change locally, publish it to the local Maven repository (the first repository every module
> reads) and build the plugin again:
>
> ```console
> user $ ./gradlew :util:publishToMavenLocal
> ```
>
> After a release, raise the version in `libs.versions.toml`.

### Publishing

```console
user $ ./gradlew :util:publish
```

The artifact goes to `https://maven.xali.host/releases`. The credentials are read from the environment variables
`MAVEN_USER` and `MAVEN_TOKEN`, or the Gradle properties `maven.user` and `maven.token`.

## Usage

All packages are under `mc.xalitoria.util`.

| Package | Holds |
|---|---|
| `bukkit` | Scheduler helpers (`Plugin.runTask`, `runTaskTimer` and the other variants take a lambda that receives a `TaskContext`), permission helpers (`Permissible.hasAnyPermission` and others), the `SenderPlayer` and `TargetPlayer` aliases, and the Brigadier helpers `nextArg`, `orElse` and `getPlayer`. |
| `bukkit.placeholder` | `PlayerPlaceholder` and the time and world placeholders. |
| `message` | `send`, `pass`, `info`, `warn` and `fail` for `CommandSender` and `Logger` (colored with `PASS_COLOR` and the others), `formatArgs` (`{0}`, `{1}`), `String.toComponent()`, `Pager` and `Placeholderable`. |
| `config` | `YamlFile<T>`: a YAML file read and written through kaml. Missing files are created from the defaults, unknown keys are ignored, and defaults are written out. |
| `serial` | Serializers for `BigDecimal`, `Location`, `Vector` and `UUID`, and `message` (`State`, `Recipient`, `StringList`) for configurable messages. |
| `kotlin` | Collection helpers such as `toggle` and `stringFilter`. |

**Reading a configuration file**

```kotlin
val config = YamlFile(dataPath.resolve("config.yml"), MainConfig.serializer(), ::MainConfig)
config.load()                       // writes the defaults when the file is missing
config.data.language                // the current values
```

## Development

Sources are under `util/kotlin`. There are no tests yet. `util` is a plain Java 11 library, so avoid APIs newer than
Java 11. Every plugin uses the public helpers, so keep names and signatures stable: the plugins see a change only after a
release and a version bump.

## See also

- [color](../color/README.md) — the color library `util` builds on
- [Gradle plugin](../plugin/README.md) — how `api(...)` libraries are loaded
