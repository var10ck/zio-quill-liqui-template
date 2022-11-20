package dto.auth
import zio.json.JsonCodec

case class DeleteUserDTO (userId: String)

object DeleteUserDTO{
    implicit val codec: JsonCodec[DeleteUserDTO] = zio.json.DeriveJsonCodec.gen[DeleteUserDTO]
}
