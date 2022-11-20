package dao.entities.auth

import zio._
import zio.json.{DeriveJsonCodec, JsonCodec}

case class User(
    id: UserId,
    login: String,
    passwordHash: String,
    salt: String,
    firstName: String,
    lastName: String,
    birthdate: java.time.LocalDate
)

object User {

    /** Uses the `random` method defined on our UserId wrapper to generate a random ID and assign that to the User we
      * are creating.
      */
    def make(
        login: String,
        passwordHash: String,
        salt:String,
        firstName: String,
        lastName: String,
        birthdate: java.time.LocalDate
    ): UIO[User] =
        UserId.random.map(User(_, login, passwordHash, salt, firstName, lastName, birthdate))

    /** JSON codec */
    implicit val codec: JsonCodec[User] = DeriveJsonCodec.gen[User]
}
