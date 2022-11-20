package dao.entities.auth
import zio.UIO
import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time._

case class Role(id: RoleId, name: String, created: LocalDateTime, updated: LocalDateTime)

object Role {

    /** Uses the `random` method defined on our RoleId wrapper to generate a random ID and assign that to the Role we
      * are creating.
      */
    def make(name: String): UIO[Role] =
        RoleId.random.map(Role(_, name, LocalDateTime.now, LocalDateTime.now))

    /** JSON codec */
    implicit val codec: JsonCodec[Role] = DeriveJsonCodec.gen[Role]
}
