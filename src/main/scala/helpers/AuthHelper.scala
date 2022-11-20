package helpers

import dao.entities.auth.{User, UserSessionId}
import dao.repositories.auth.UserSessionRepository
import exceptions.BodyParsingException
import zhttp.http.{Http, Request}
import zio.ZIO
import zio.json.{DecoderOps, JsonDecoder}

import javax.sql.DataSource
import scala.reflect.ClassTag

object AuthHelper {

    /** Extracts userSessionId from header and tries to parse http body as DTOType. Can fail with:
      *
      *   - HeaderNotSetException if userSessionId is not set
      *
      *   - UserUnauthorizedException session not found
      *
      * @param f
      *   function that will be applied to found user and parsed dto
      */
    def withUserContextZIO[R, A](request: Request)(
        f: User => ZIO[R, Throwable, A]): ZIO[R with DataSource with UserSessionRepository, Throwable, A] =
        for {
            sessionId <- ZIO
                .fromOption(request.headers.headerValue("userSessionId"))
                .orElseFail(exceptions.HeaderNotSetException("userSessionId"))
            userSessionId <- UserSessionId.fromString(sessionId)
            userOpt <- UserSessionRepository.getUser(userSessionId)
            user <- ZIO.fromOption(userOpt).orElseFail(exceptions.UserUnauthorizedException())
            result <- f(user)
        } yield result

    def withUserContextHttp[R, A](request: Request)(f: User => ZIO[R, Throwable, A]): Http[R with DataSource with UserSessionRepository, Throwable, Any, A] = Http.fromZIO {
        for {
            sessionId <- ZIO
                .fromOption(request.headers.headerValue("userSessionId"))
                .orElseFail(exceptions.HeaderNotSetException("userSessionId"))
            userSessionId <- UserSessionId.fromString(sessionId)
            userOpt <- UserSessionRepository.getUser(userSessionId)
            user <- ZIO.fromOption(userOpt).orElseFail(exceptions.UserUnauthorizedException())
            result <- f(user)
        } yield result
    }

    /** Extracts userSessionId from header and tries to parse http body as DTOType. Can fail with:
      *
      *   - HeaderNotSetException if userSessionId is not set
      *
      *   - UserUnauthorizedException session not found
      *
      *   - BodyParsingException if JSON-body is invalid
      *
      * @param f
      *   function that will be applied to found user and parsed dto
      * @tparam DTOType
      *   type of DTO, must have JsonDecoder[DTOType] instance
      */
    def withUserContextAndDtoZIO[DTOType, R, A](request: Request)(f: (User, DTOType) => ZIO[R, Throwable, A])(implicit
        decoder: JsonDecoder[DTOType]): ZIO[R with DataSource with UserSessionRepository, Throwable, A] =
        for {
            sessionId <- ZIO
                .fromOption(request.headers.headerValue("userSessionId"))
                .orElseFail(exceptions.HeaderNotSetException("userSessionId"))
            userSessionId <- UserSessionId.fromString(sessionId)
            userOpt <- UserSessionRepository.getUser(userSessionId)
            user <- ZIO.fromOption(userOpt).orElseFail(exceptions.UserUnauthorizedException())
            requestBody <- request.body.asString
            dto <- ZIO
                .fromEither(requestBody.fromJson[DTOType])
                .orElseFail(BodyParsingException())
            result <- f(user, dto)
        } yield result
}
