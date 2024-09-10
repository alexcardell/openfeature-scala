# ff4s - Feature Flags For Scala

## Features

- Cross-platform, cross-version Scala feature flagging
- [Flipt](https://flipt.io) client
- [OpenFeature](https://openfeature.dev) SDK
    - Provider Evaluation
    - 🚧 Hooks
- Flipt OpenFeature provider
- LaunchDarkly OpenFeature provider with [Catalyst](https://typelevel.org/catapult)

## Installing

```scala
libraryDependencies += "io.cardell" %%% "flipt-sdk-server" % "@VERSION@"
// or
libraryDependencies ++= Seq(
    "io.cardell" %%% "openfeature-sdk-server" % "@VERSION@",
    "io.cardell" %%% "openfeature-provider-flipt" % "@VERSION@"
)
```

## OpenFeature Usage

The OpenFeature SDK adds features like handling default values in case of errors.
Eventually the SDK will cover the full range of the [openfeature](https://openfeature.dev)
specification, like hooks, events, static vs dynamic context.

See `Flipt usage` on how to set up the `FliptApi`. Once done, set up a provider:

```scala mdoc
import cats.effect.IO
import io.cardell.openfeature.OpenFeature
import io.cardell.flipt.FliptApi
import io.cardell.openfeature.provider.flipt.FliptProvider

def provider(flipt: FliptApi[IO]) = {
    val featureSdk = OpenFeature[IO](new FliptProvider[IO](flipt, "some-namespace"))

    featureSdk.client.flatMap { featureClient =>
        featureClient.getBooleanValue("boolean-flag", false)
    }
}
```

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

