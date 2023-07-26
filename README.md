# Functional (feature) flags for Scala

Intended to eventually provide a decent common interface
for multiple feature flag platforms, using Cats/Cats Effect.

Very WIP. If using LaunchDarkly there's always typelevel/catapult

## Installing 

```sbt
libraryDependencies += "io.cardell" %% "ff4s" % "0.0.1"
```

## Usage

See [examples](./examples)

## Backends 

| Backend        | On/Off Flags        | Variants       | Segments       |
| -------        | ------------        | --------       |  ------        |
| Flipt          | :construction:      | :construction: | :construction: |
| LaunchDarkly   | :construction:      | :construction: | :construction: |
| GrowthBook     | :construction:      | :construction: | :construction: |

## License

This software is licensed under the MIT license. See [LICENSE](./LICENSE)

## Developing

To set up development dependencies use Nix >2.4
with flakes enabled, and use the `nix develop` command.
