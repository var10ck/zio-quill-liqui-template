package dao.entities.auth
import zio.json.JsonCodec

case class RolePermission(roleId: RoleId, permission: String)

object RolePermission{
    implicit val codec: JsonCodec[RolePermission] = zio.json.DeriveJsonCodec.gen[RolePermission]
}
