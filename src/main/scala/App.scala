import api._
import configuration.ApplicationConfig
import dao.repositories.auth.{UserRepository, UserSessionRepository}
import db.{DataSource, LiquibaseService, LiquibaseServiceLive, zioLiveDS}
import liquibase.Liquibase
import services._
import zio.{ExitCode, Scope, ZLayer}

object App {

    type AppEnvironment = ApplicationConfig
        with DataSource with LiquibaseService with UserRepository with UserSessionRepository with AuthService
        with Liquibase with Scope

    val appEnv: ZLayer[Any with Scope, Throwable, AppEnvironment] =
        ApplicationConfig.live >+> zioLiveDS >+> LiquibaseService.live >+> UserRepository.live >+>
            UserSessionRepository.live >+> AuthService.live >+> LiquibaseServiceLive.liquibaseLayer >+>
            Scope.default


    val httpApp = AuthApi.api

    val serverConfig = zhttp.service.Server
        .app(httpApp)
        .withObjectAggregator(Int.MaxValue)

    val server = {
        for {
            config <- zio.config.getConfig[ApplicationConfig]
            _ <- LiquibaseService.performMigration *>
                serverConfig.withPort(config.api.port).startDefault
        } yield ExitCode.success
    }
}
