package dao.entities.auth
import zio.json.JsonCodec
import zio.{Random, Task, UIO, ZIO}

import java.util.UUID

case class RoleId(id: UUID) extends AnyVal

object RoleId{

    /** Generates a Random UUID and wraps it in the RoleId type. */
    def random: UIO[RoleId] = Random.nextUUID.map(RoleId.apply)

    /** Allows a UUID to be parsed from a string which is then wrapped in the RoleId type.
      */
    def fromString(id: String): Task[RoleId] =
        ZIO.attempt {
            RoleId(UUID.fromString(id))
        }

    /** Derives a codec allowing a UUID to be (de)serialized as an RoleId. */
    implicit val codec: JsonCodec[RoleId] = JsonCodec[UUID].transform(RoleId(_), _.id)

}
