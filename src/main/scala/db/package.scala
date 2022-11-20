//import com.typesafe.config.Config
import com.typesafe.config.{Config, ConfigFactory}
import configuration.ApplicationConfig
import com.zaxxer.hikari.HikariDataSource
import io.getquill._
import io.getquill.context.ZioJdbc
import io.getquill.util.LoadConfig
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.{ClassLoaderResourceAccessor, CompositeResourceAccessor, FileSystemResourceAccessor}
import zio.config.magnolia.descriptor
import zio.config.{read, ReadError}
import zio.config.typesafe.TypesafeConfigSource
import zio.{RIO, URIO, ZIO, ZLayer, _}
import zio.macros.accessible

package object db {

    type DataSource = javax.sql.DataSource

    class PgCtx extends PostgresZioJdbcContext(NamingStrategy(Escape, Literal))

    object Ctx extends PgCtx

    private def makeDataSource(resourceBaseName: String) = ZLayer.fromZIO(for {
        config <- ZIO.from(ConfigFactory.load(resourceBaseName).getConfig("db"))
        jdbcContextConf <- ZIO.succeed(JdbcContextConfig(config).dataSource)
        ds <- ZIO.succeed(jdbcContextConf)
    } yield ds)

    lazy val zioTestDS: ZLayer[Any, Throwable, DataSource] = makeDataSource("application.test.conf")

    lazy val zioLiveDS: ZLayer[Any, Throwable, DataSource] = makeDataSource("application.conf")

    trait LiquibaseService {
        def performMigration: RIO[Liquibase, Unit]

        def performMigrationClean: RIO[Liquibase, Unit]

        def performMigrationWithDropAll: RIO[Liquibase, Unit]
    }

    final case class LiquibaseServiceLive() extends LiquibaseService {
        override def performMigration: RIO[Liquibase, Unit] = LiquibaseService.liquibase.map(_.update("dev"))

        override def performMigrationClean: RIO[Liquibase, Unit] = for {
            liquibase <- LiquibaseService.liquibase
            _ <- ZIO.from(liquibase.clearCheckSums())
            _ <- ZIO.from(liquibase.update("dev"))
        } yield ()

        override def performMigrationWithDropAll: RIO[Liquibase, Unit] = for {
            liquibase <- LiquibaseService.liquibase
            _ <- ZIO.from(liquibase.clearCheckSums())
            _ <- ZIO.from(liquibase.dropAll())
            _ <- ZIO.from(liquibase.update("dev"))
        } yield ()
    }

    object LiquibaseServiceLive {
        def layer: ULayer[LiquibaseServiceLive] = ZLayer.succeed(LiquibaseServiceLive())

        def liquibaseLayer: ZLayer[Any with Scope with DataSource with ApplicationConfig, Throwable, Liquibase] =
            ZLayer.fromZIO(
              for {
                  config <- zio.config.getConfig[ApplicationConfig]
                  liquibase <- mkLiquibase(config)
              } yield liquibase
            )

        def mkLiquibase(config: ApplicationConfig): ZIO[Any with Scope with DataSource, Throwable, Liquibase] = for {
            ds <- ZIO.environment[DataSource].map(_.get)
            fileAccessor <- ZIO.from(new FileSystemResourceAccessor())
            classLoader <- ZIO.from(classOf[LiquibaseService].getClassLoader)
            classLoaderAccessor <- ZIO.from(new ClassLoaderResourceAccessor(classLoader))
            fileOpener <- ZIO.from(new CompositeResourceAccessor(fileAccessor, classLoaderAccessor))
            jdbcConn <- ZIO.acquireRelease(ZIO.from(new JdbcConnection(ds.getConnection)))(c => ZIO.succeed(c.close()))
            liqui <- ZIO.from(new Liquibase(config.liquibase.changeLog, fileOpener, jdbcConn))
        } yield liqui

    }

    object LiquibaseService {
        def performMigration: ZIO[Liquibase with LiquibaseService, Throwable, Unit] =
            ZIO.serviceWithZIO[LiquibaseService](_.performMigration)

        def performMigrationClean: ZIO[Liquibase with LiquibaseService, Throwable, Unit] =
            ZIO.serviceWithZIO[LiquibaseService](_.performMigrationClean)

        def performMigrationWithDropAll: ZIO[Liquibase with LiquibaseService, Throwable, Unit] =
            ZIO.serviceWithZIO[LiquibaseService](_.performMigrationWithDropAll)

        def liquibase: URIO[Liquibase, Liquibase] = ZIO.service[Liquibase]

        val live: ULayer[LiquibaseService] = LiquibaseServiceLive.layer
        val liquibaseLayer: ZLayer[Any with Scope with DataSource with ApplicationConfig, Throwable, Liquibase] =
            LiquibaseServiceLive.liquibaseLayer
    }
}
