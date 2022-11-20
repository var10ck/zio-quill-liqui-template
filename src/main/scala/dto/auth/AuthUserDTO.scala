package dto.auth
import zio.json.JsonCodec

case class AuthUserDTO (login:String, password: String)

object AuthUserDTO{
    implicit val codec: JsonCodec[AuthUserDTO] = zio.json.DeriveJsonCodec.gen[AuthUserDTO]
}
