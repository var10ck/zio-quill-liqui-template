package dao.entities.auth
import zio.json.JsonCodec

case class UserRoles (userId: UserId, roleId: RoleId)

object UserRoles{
    implicit val codec: JsonCodec[UserRoles] = zio.json.DeriveJsonCodec.gen[UserRoles]
}
