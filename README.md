# Functional (feature) flags for Scala

Intended to eventually provide a decent common interface
for multiple feature flag platforms, using Cats/Cats Effect.

Very WIP. If using LaunchDarkly there's always 
[typelevel/catapult](https://github.com/typelevel/catapult)

The first pass is to support applications using flags,
before adding any flag administration capabilities.

## Installing 

```sbt
libraryDependencies += "io.cardell" %% "ff4s" % "0.0.1"
```

## Usage

See [examples](./examples)

## Supported Backends 

- [Flipt](./flipt/README.md)

## License

This software is licensed under the MIT license. See [LICENSE](./LICENSE)

## Developing

To set up development dependencies use Nix >2.4
with flakes enabled, and use the `nix develop` command.
