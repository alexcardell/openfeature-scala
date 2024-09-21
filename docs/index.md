# openfeature-scala

## Features

- Cross-platform, cross-version Scala feature flagging
- [OpenFeature](https://openfeature.dev) SDK
- [Flipt](https://flipt.io) client
- Flipt OpenFeature Provider
- In-Memory OpenFeature Provider

## Installing

```scala
libraryDependencies += "io.cardell" %%% "flipt-sdk-server" % "@VERSION@"
// or
libraryDependencies ++= Seq(
    "io.cardell" %%% "openfeature-sdk" % "@VERSION@",
    // for circe json variant types
    "io.cardell" %%% "openfeature-sdk-circe" % "@VERSION@",
    // to use flipt as a backend
    "io.cardell" %%% "openfeature-provider-flipt" % "@VERSION@"
)
```

## OpenFeature Compatibility

|Features|Status|
|---|---|
|Providers|✅|
|Targeting|✅|
|Logging|🚧|
|Domains|🚧|
|Eventing|🚧|
|Shutdown|🚧|
|Transaction Context Propagation|🚧|

## OpenFeature Usage

The OpenFeature SDK adds features like handling default values in case of errors.
Eventually the SDK will cover the full range of the [openfeature](https://openfeature.dev)
specification, like hooks, events, static vs dynamic context.

### OpenFeature Java Compatibility

The `openfeature-provider-java` module wraps existing 
[OpenFeature Java SDKs](https://github.com/open-feature/java-sdk-contrib).

#### Installation 

Using Flagd as an example:

```scala
libraryDependencies ++= Seq(
    "io.cardell" %%% "openfeature-sdk" % "@VERSION@",
    "io.cardell" %%% "openfeature-provider-java" % "@VERSION@",
    "dev.openfeature.contrib.providers" % "flagd" % "0.8.9",
)
```

```scala mdoc:compile-only
import cats.effect.IO
import dev.openfeature.contrib.providers.flagd.FlagdOptions
import dev.openfeature.contrib.providers.flagd.FlagdProvider

import io.cardell.openfeature.OpenFeature
import io.cardell.openfeature.provider.java.JavaProvider

val provider =
    new FlagdProvider(
        FlagdOptions
            .builder()
            .host("host")
            .port(8013)
            .build()
    )

JavaProvider
    .resource[IO](provider)
    .map(OpenFeature[IO])
    .evalMap(_.client)
```


### FeatureClient Evaluation

```scala mdoc:compile-only
import cats.effect.IO

import io.cardell.openfeature.FeatureClient
import io.cardell.openfeature.StructureCodec

case class SomeVariant(field: String, field2: Int)

def program(features: FeatureClient[IO])(
    implicit codec: StructureCodec[SomeVariant]
) = {
    for {
        flagEnabled <- features.getBooleanValue("boolean-flag", false)
        _ <- IO.println(s"${flagEnabled}")
        variant <- features.getStructureValue[SomeVariant](
            "structure-flag",
            SomeVariant("a", 1)
        )
        _ <- IO.println(s"${variant}")
    } yield ()
}
```

### Hooks

Hooks are work-in-progress. All four OpenFeature [hook types](https://openfeature.dev/specification/sections/hooks)
are supported but only on the `FeatureClient` and `Provider` interfaces.

Hook types:
- BeforeHook (can optionally manipulate EvaluationContext)
- AfterHook
- ErrorHook
- FinallyHook

```scala mdoc
import cats.effect.IO
import io.cardell.openfeature.FeatureClient
import io.cardell.openfeature.BeforeHook
import io.cardell.openfeature.provider.Provider

val hook = BeforeHook[IO] { case (context, hints @ _) => 
    IO.println(s"I'm about to evaluate ${context.flagKey}").as(None)
}

def providerWithHook(provider: Provider[IO]) = 
    provider.withHook(hook)

// and similarly for `client`
def clientWithHook(client: FeatureClient[IO]) = 
    client.withHook(hook)
```

### otel4s

`otel4s` trace integration is provided, offering a set of trace hooks

```scala mdoc
import cats.effect.IO
import org.typelevel.otel4s.trace.Tracer
import io.cardell.openfeature.FeatureClient
import io.cardell.openfeature.otel4s.TraceHooks

def tracedClient(
    client: FeatureClient[IO]
)(implicit T: Tracer[IO]) = TraceHooks.ioLocal
    .map(hooks => client.withHooks(hooks))
```

### Variants

Providers offer resolving a particular variant, using a Structure type. Typically this is JSON defined on the server side. 

To provide arbitrary case classes for variant decoding, a `StructureCodec[A]` is required.

This could be done explicitly, but you can also derive them from JSON codecs. Currently only Circe is supported.

#### Circe Integration

Provider implicit `Decoder[A]` and `Encoder.AsObject[A]`. Import `io.cardell.openfeature.circe._`

```scala mdoc:compile-only
import cats.effect.IO
import io.circe.Decoder
import io.circe.Encoder

import io.cardell.openfeature.FeatureClient
import io.cardell.openfeature.circe._

case class SomeVariant(field: String, field2: Int)

def circeProgram(features: FeatureClient[IO])(
    implicit d: Decoder[SomeVariant],
    e: Encoder.AsObject[SomeVariant]
) = {
    for {
        flagEnabled <- features.getBooleanValue("boolean-flag", false)
        _ <- IO.println(s"${flagEnabled}")
        variant <- features.getStructureValue[SomeVariant](
            "structure-flag",
            SomeVariant("a", 1)
        )
        _ <- IO.println(s"${variant}")
    } yield ()
}
```

Alternative, `Codec.AsObject[A]` would work.

### Implementing A New `EvaluationProvider`

`EvaluationProvider` does not need to handle any errors that aren't deemed recoverable, or need
to implement any hook logic. Running hooks, and handling default evaluations on error is handled 
in the library

Implement the call, response decoding, and handle any recoverable errors that make sense. 

## Flipt Usage

The Flipt client is bare-bones, using it is not recommended, unless as OpenFeature SDK Provider.

```scala mdoc
import cats.effect.IO
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.Uri
import io.cardell.flipt.FliptApi
import io.cardell.flipt.EvaluationRequest
import io.cardell.flipt.auth.AuthenticationStrategy

val url: Uri = Uri.unsafeFromString("https://flipt.example.com")
val token: String = "token"

val resource = EmberClientBuilder
    .default[IO]
    .build
    .map(client =>
        FliptApi[IO](client, url, AuthenticationStrategy.ClientToken("token"))
    )

resource.use { flipt => 
    for {
        res <- flipt.evaluateBoolean(
            EvaluationRequest(
                namespaceKey = "default",
                flagKey = "my-flag-1",
                entityId = None,
                context = Map.empty,
                reference = None
            )
        )
    } yield res.enabled
}
```
