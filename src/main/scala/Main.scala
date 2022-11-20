import App.appEnv
import zio._

object Main extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
      App.server
          .provideLayer(appEnv)
}