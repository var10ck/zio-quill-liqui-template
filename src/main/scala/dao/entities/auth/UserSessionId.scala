package dao.entities.auth

import zio.json.JsonCodec
import zio.{Random, Task, UIO, ZIO}

import java.util.UUID

case class UserSessionId(id: UUID) extends AnyVal

object UserSessionId {

    /** Generates a Random UUID and wraps it in the UserSessionId type. */
    def random: UIO[UserSessionId] = Random.nextUUID.map(UserSessionId.apply)

    /** Allows a UUID to be parsed from a string which is then wrapped in the UserSessionId type.
      */
    def fromString(id: String): Task[UserSessionId] = ZIO.attempt(UserSessionId(UUID.fromString(id)))

    /** Derives a codec allowing a UUID to be (de)serialized as an UserSessionId. */
    implicit val codec: JsonCodec[UserSessionId] = JsonCodec[UUID].transform(UserSessionId.apply, _.id)
}
