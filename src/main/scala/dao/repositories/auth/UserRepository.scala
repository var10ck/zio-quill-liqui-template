package dao.repositories.auth

import dao.entities.auth.{User, UserId}
import io.getquill.context.ZioJdbc.QIO
import zio.{ULayer, ZIO}

import java.sql.SQLException
import javax.sql.DataSource

trait UserRepository {

    /** Creates a new User. */
    def create(
        login: String,
        password: String,
        firstName: String,
        lastName: String,
        birthdate: java.time.LocalDate): QIO[User]

    /** Deletes an existing User. */
    def delete(id: UserId): QIO[Unit]

    /** Retrieves a User from the database by id. */
    def get(id: UserId): QIO[Option[User]]

    /** Retrieves a User from the database by login. Login has unique constraint */
    def getByLogin(login: String): QIO[Option[User]]

    /** Retrieves all Users from the database. */
    def getAll: QIO[List[User]]

    /** Updates info of an existing User. */
    def updateInfo(
        id: UserId,
        login: Option[String],
        firstName: Option[String],
        lastName: Option[String],
        birthdate: Option[java.time.LocalDate]
    ): QIO[Unit]

    /** Generates new salt and passwordHash and updates User */
    def updatePassword(
        id: UserId,
        newPassword: String
    ): QIO[Unit]
}

object UserRepository {

    /** Creates a new User. */
    def create(
        login: String,
        password: String,
        firstName: String,
        lastName: String,
        birthdate: java.time.LocalDate
    ): ZIO[DataSource with UserRepository, SQLException, User] =
        ZIO.serviceWithZIO[UserRepository](_.create(login, password, firstName, lastName, birthdate))

    /** Deletes an existing User. */
    def delete(id: UserId): ZIO[DataSource with UserRepository, SQLException, Unit] =
        ZIO.serviceWithZIO[UserRepository](_.delete(id))

    /** Retrieves a User from the database. */
    def get(id: UserId): ZIO[DataSource with UserRepository, SQLException, Option[User]] =
        ZIO.serviceWithZIO[UserRepository](_.get(id))

    def getByLogin(login: String): ZIO[DataSource with UserRepository, SQLException, Option[User]] =
        ZIO.serviceWithZIO[UserRepository](_.getByLogin(login))

    /** Retrieves all Users from the database. */
    def getAll: ZIO[DataSource with UserRepository, SQLException, List[User]] =
        ZIO.serviceWithZIO[UserRepository](_.getAll)

    /** Updates info of an existing User. */
    def updateInfo(
        id: UserId,
        login: Option[String],
        firstName: Option[String],
        lastName: Option[String],
        birthdate: Option[java.time.LocalDate]
    ): ZIO[DataSource with UserRepository, SQLException, Unit] =
        ZIO.serviceWithZIO[UserRepository](_.updateInfo(id, login, firstName, lastName, birthdate))

    /** Generates new salt and passwordHash and updates User */
    def updatePassword(
        id: UserId,
        newPassword: String
    ): ZIO[DataSource with UserRepository, SQLException, Unit] =
        ZIO.serviceWithZIO[UserRepository](_.updatePassword(id, newPassword))

    val live: ULayer[UserRepository] = UserRepositoryLive.layer
}
