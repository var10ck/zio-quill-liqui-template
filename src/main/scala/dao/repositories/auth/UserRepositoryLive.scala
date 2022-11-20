package dao.repositories.auth

import com.github.t3hnar.bcrypt._
import dao.entities.auth.{User, UserId}
import io.getquill.context.ZioJdbc.QIO
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument.Import
import zio._
import zio.metrics._

/** UserRepositoryLive is a service which provides the "live" implementation of the UserRepository. This implementation
  * uses a DataSource, which will concretely be a connection pool.
  */
final case class UserRepositoryLive() extends UserRepository {

    // QuillContext needs to be imported here to expose the methods in the QuillContext object.
    import db.Ctx._

    /** `create` uses the User model's `make` method to create a new User. The User is formatted into a query string,
      * then inserted into the database using `provideEnvironment` to provide the datasource to the effect returned by
      * `run`. The created User is returned.
      */
    override def create(
        login: String,
        password: String,
        firstName: String,
        lastName: String,
        birthdate: java.time.LocalDate): QIO[User] =
        for {
            salt <- ZIO.succeed(generateSalt)
            hashedPassword <- ZIO.succeed(password.bcryptBounded(salt))
            user <- User.make(login, hashedPassword, salt, firstName, lastName, birthdate)
            _ <- run(query[User].insertValue(lift(user)))
            _ <- Metric.counter("user.created").increment
        } yield user

    /** `delete` uses `filter` to find a User in the database whose ID matches the one provided and deletes it.
      *
      * Unit is returned to indicate that we are running this method for its side effects, a deleted User gives us no
      * information. This will either fail or succeed.
      */
    override def delete(id: UserId): QIO[Unit] =
        run(query[User].filter(_.id == lift(id)).delete).unit

    /** `get` uses `filter` to find a User in the database whose ID matches the one provided and returns it.
      */
    override def get(id: UserId): QIO[Option[User]] =
        run(query[User].filter(_.id == lift(id)))
            .map(_.headOption)

    /** Retrieves a User from the database by login. Login has unique constraint */
    override def getByLogin(login: String): QIO[Option[User]] =
        run(query[User].filter(_.login == lift(login)))
            .map(_.headOption)

    /** `getAll` uses `query` to find all entries in the database of type User and returns them.
      */
    override def getAll: QIO[List[User]] =
        run(query[User].sortBy(_.birthdate))

    /** `update` uses `filter` to find a User in the database whose ID matches the one provided and updates it with the
      * provided optional values.
      *
      * Because a user may not provide all optional values, `setOpt` is used to preserve the existing value in the case
      * one is not provided to replace it. Unit is returned to indicate side-effecting code. Note that this
      * `dynamicQuery` is not generated at compile time.
      *
      * For more information on dynamic queries, see: https://getquill.io/#writing-queries-dynamic-queries
      */
    override def updateInfo(
        id: UserId,
        login: Option[String],
        firstName: Option[String],
        lastName: Option[String],
        birthdate: Option[java.time.LocalDate]
    ): QIO[Unit] = {
        run(
          dynamicQuery[User]
              .filter(_.id == lift(id))
              .update(
                setOpt(_.login, login),
                setOpt(_.firstName, firstName),
                setOpt(_.lastName, lastName),
                setOpt(_.birthdate, birthdate)
              )
        ).unit
    }

    /**
     * generates new salt, computes new password hash and updates User
     */
    override def updatePassword(id: UserId, newPassword: String): QIO[Unit] =
        for {
            newSalt <- ZIO.succeed(generateSalt)
            newHashedPassword <- ZIO.succeed(newPassword.bcryptBounded(newSalt))
            _ <- run(
              dynamicQuery[User]
                  .filter(_.id == lift(id))
                  .update(
                    set(_.passwordHash, newHashedPassword),
                    set(_.salt, newSalt)
                  )
            )
        } yield ()
}

/** Here in the companion object we define the layer that provides the live implementation of the UserRepository.
  */
object UserRepositoryLive {

    val layer: ULayer[UserRepositoryLive] = ZLayer.succeed(UserRepositoryLive())
}
