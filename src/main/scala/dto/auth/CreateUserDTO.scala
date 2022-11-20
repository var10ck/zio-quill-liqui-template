package dto.auth
import zio.json.JsonCodec

case class CreateUserDTO (login: String, password: String, firstName: String, lastName: String, birthdate: String)

object CreateUserDTO{
    implicit val codec: JsonCodec[CreateUserDTO] = zio.json.DeriveJsonCodec.gen[CreateUserDTO]
}
