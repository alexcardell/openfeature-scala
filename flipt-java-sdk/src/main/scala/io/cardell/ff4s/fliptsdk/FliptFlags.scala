package io.cardell.ff4s.fliptsdk

import cats.effect.kernel.Sync
import cats.syntax.all._
import com.flipt.api.resources.flags.{FlagsClient => JFlagsClient}

import io.cardell.ff4s.FlagCase
import io.cardell.ff4s.Flags
import io.cardell.ff4s.Key

protected class FliptFlags[F[_]: Sync](flipt: JFlagsClient, namespace: String)
    extends Flags[F] {

  def get(key: Key): F[FlagCase] = {
    val fliptFlag = Sync[F].blocking { flipt.get(key, namespace) }

    for {
      flag <- fliptFlag
      flagCase =
        if (flag.getEnabled()) { FlagCase.On.asInstanceOf[FlagCase] }
        else { FlagCase.Off.asInstanceOf[FlagCase] }
    } yield flagCase
  }

}
