package services

import com.github.t3hnar.bcrypt._
import dao.entities.auth.{User, UserSession, UserSessionId}
import dao.repositories.auth._
import dto.auth.{AuthUserDTO, CheckSessionDTO, CreateUserDTO}
import exceptions.DataValidationException
import helpers.DateHelper
import helpers.TypedIdHelper.parseUserId
import services.AuthServiceLive.validateUser
import zio.prelude.{Validation, ZValidation}
import zio.{RIO, ULayer, ZIO, ZLayer}

import java.time.LocalDate
import javax.sql._
import scala.util.Try

final case class AuthServiceLive() extends AuthService {

    /** Create user and return it
      */
    override def createUser(createUserDTO: CreateUserDTO): ZIO[DataSource with UserRepository, Throwable, User] = for {
        _ <- validateUser(createUserDTO).toZIO
        parsedBirthdate <- ZIO.from(DateHelper.stringToLocalDate(createUserDTO.birthdate))
        user <- UserRepository.create(
          login = createUserDTO.login,
          password = createUserDTO.password,
          firstName = createUserDTO.firstName,
          lastName = createUserDTO.lastName,
          birthdate = parsedBirthdate)
    } yield user

    /** Try to find user with given login/password. Returns new UserSession if succeed */
    override def authUser(authUserDTO: AuthUserDTO)
        : RIO[DataSource with UserRepository with UserSessionRepository, Option[UserSession]] =
        for {
            userOpt <- UserRepository.getByLogin(authUserDTO.login)
            user <- ZIO.fromOption(userOpt).mapError(e => exceptions.UserNotFound("login", authUserDTO.login))
            s <- ZIO.when(user.passwordHash == authUserDTO.password.bcryptBounded(user.salt)){
                UserSessionRepository.create(user.id)
            }
        } yield s

    /** Check if sessionId is exists in database */
    override def checkSession(checkSessionDTO: CheckSessionDTO): RIO[DataSource with UserSessionRepository, Boolean] =
        for{
            sessionId <- UserSessionId.fromString(checkSessionDTO.sessionId)
            session <- UserSessionRepository.get(sessionId)
            result <- ZIO.from(session.isDefined)
        }    yield result

    override def deleteUser(userId: String): ZIO[DataSource with UserRepository, Throwable, Unit] = for{
        uid <- parseUserId(userId)
        _ <- UserRepository.delete(uid)
    } yield ()
}

object AuthServiceLive{
    def layer: ULayer[AuthServiceLive] = ZLayer.succeed(AuthServiceLive())

    def validateUser(userDTO: CreateUserDTO): ZValidation[Nothing, Throwable, Unit] = for {
      birthday <- Validation.fromTry(Try(DateHelper.stringToLocalDate(userDTO.birthdate))) //validate date format
        _ <- if (LocalDate.now().getYear - birthday.getYear <= 100) Validation.succeed() else Validation.fail(DataValidationException("User can't be older then 100 years"))
    } yield ()
}
