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

See `Flipt usage` on how to set up the `FliptApi`. Once done, set up a provider:

```scala mdoc
import cats.effect.IO
import io.circe.Decoder
import io.circe.Encoder

import io.cardell.flipt.FliptApi
import io.cardell.openfeature.OpenFeature
import io.cardell.openfeature.provider.flipt.FliptProvider
import io.cardell.openfeature.circe._

case class SomeVariant(field: String, field2: Int)

def provider(flipt: FliptApi[IO])(implicit d: Decoder[SomeVariant], e: Encoder.AsObject[SomeVariant]) = {
    val featureSdk = OpenFeature[IO](new FliptProvider[IO](flipt, "some-namespace"))

    featureSdk.client.flatMap { featureClient =>
        for {
            eval <- featureClient.getBooleanValue("boolean-flag", false)
            _ <- IO.println(s"${eval}")
            eval2 <- featureClient.getStructureValue[SomeVariant](
                "structure-flag",
                SomeVariant("a", 1)
            )
            _ <- IO.println(s"${eval2}")
        } yield ()
    }
}
```

### Hooks

Hooks are work-in-progress. All four OpenFeature [hook types](https://openfeature.dev/specification/sections/hooks)
are supported but only on the `FeatureClient` and `Provider` interfaces.

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

## Future Work

- Java OpenFeature Provider wrapper, to unlock more SDKs
- LaunchDarkly Provider using `typelevel/catapult`
