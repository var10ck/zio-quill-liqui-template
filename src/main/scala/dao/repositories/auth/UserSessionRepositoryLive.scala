package dao.repositories.auth

import dao.entities.auth.{User, UserId, UserSession, UserSessionId}
import io.getquill.context.ZioJdbc.QIO
import zio.{ULayer, ZLayer}

import java.time.LocalDateTime

final case class UserSessionRepositoryLive() extends UserSessionRepository {

    import db.Ctx._

    override def create(userId: UserId, validFrom: LocalDateTime, validUntil: LocalDateTime): QIO[UserSession] =
        for {
            session <- UserSession.make(userId, validFrom, validUntil)
            _ <- run(query[UserSession].insertValue(lift(session)))
        } yield session

    override def delete(userSessionId: UserSessionId): QIO[Unit] =
        run(query[UserSession].filter(_.id == lift(userSessionId)).delete).unit

    override def get(userSessionId: UserSessionId): QIO[Option[UserSession]] =
        run(query[UserSession].filter(_.id == lift(userSessionId))).map(_.headOption)

    /** find all user's sessions */
    override def findForUser(userId: UserId): QIO[List[UserSession]] =
        run(query[UserSession].filter(_.userId == lift(userId)).sortBy(_.validUntil))

    /** get UserId by UserSessionId */
    override def getUserId(userSessionId: UserSessionId): QIO[Option[UserId]] =
        run(query[UserSession].filter(_.id == lift(userSessionId))).map(_.headOption.map(_.userId))

    /** get User by UserSessionId */
    override def getUser(userSessionId: UserSessionId): QIO[Option[User]] =
        run(
          query[UserSession]
              .filter(_.id == lift(userSessionId))
              .join(query[User])
              .on(_.userId == _.id)
              .map { case (_, user) => user }
        ).map(_.headOption)
}

object UserSessionRepositoryLive {
    val layer: ULayer[UserSessionRepositoryLive] = ZLayer.succeed(UserSessionRepositoryLive())
}
