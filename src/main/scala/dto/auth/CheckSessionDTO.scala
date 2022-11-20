package dto.auth
import zio.json.JsonCodec

case class CheckSessionDTO (sessionId: String)

object CheckSessionDTO{
    implicit val codec: JsonCodec[CheckSessionDTO] = zio.json.DeriveJsonCodec.gen[CheckSessionDTO]
}
