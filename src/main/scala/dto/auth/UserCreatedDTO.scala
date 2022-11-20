package dto.auth
import zio.json.JsonCodec

case class UserCreatedDTO (userId:String, firstName: String, lastName: String, birthdate: java.time.LocalDate)

object UserCreatedDTO{
    implicit val codec: JsonCodec[UserCreatedDTO] = zio.json.DeriveJsonCodec.gen[UserCreatedDTO]
}