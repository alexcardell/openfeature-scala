# ff4s - Feature Flags For Scala

## Goals

- Cross-platform, cross-version Scala feature flagging
- Implementation of a Scala [Flipt](https://flipt.io) client
- Implementation of a Scala [OpenFeature](https://openfeature.dev) SDK
- Implementation of a Scala Flipt OpenFeature provider
- Implementation of a Scala LaunchDarkly OpenFeature provider, e.g. with
  [Catalyst](https://typelevel.org/catapult)

## Installing

```scala
libraryDependencies += "io.cardell" %%% "ff4s-flipt-server-sdk" % "@VERSION@"
```

## Usage

```scala mdoc
import cats.effect.IO
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.Uri
import io.cardell.ff4s.flipt.FliptApi
import io.cardell.ff4s.flipt.EvaluationRequest
import io.cardell.ff4s.flipt.auth.AuthenticationStrategy

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
