package dao.entities.auth

import zio.UIO
import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time._

/**
 * User Session
 * @param id identifier of session for concrete User, save in cookie
 */
case class UserSession(
    id: UserSessionId,
    userId: UserId,
    validFrom: LocalDateTime,
    validUntil: LocalDateTime
)

object UserSession{

    /**
     * makes userSession
     */
    def make(userId: UserId,
             validFrom: LocalDateTime = LocalDateTime.now(),
             validUntil: LocalDateTime = LocalDateTime.MAX
            ): UIO[UserSession] =
        UserSessionId.random.map(UserSession(_, userId, validFrom, validUntil))

    /** JSON codec */
    implicit val codec: JsonCodec[UserSession] = DeriveJsonCodec.gen[UserSession]
}
