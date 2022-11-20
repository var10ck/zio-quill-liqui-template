package dao.repositories.auth
import dao.entities.auth.{Role, RoleId, RolePermission, UserId, UserRoles}
import io.getquill.context.ZioJdbc.QIO
import zio.metrics.Metric

final case class RoleRepositoryLive() extends RoleRepository {

    import db.Ctx._

    /** Creates a new Role. */
    override def create(name: String): QIO[Role] =
        for {
            role <- Role.make(name)
            _ <- run(query[Role].insertValue(lift(role)))
            _ <- Metric.counter("role.created").increment
        } yield role

    /** Deletes an existing Role. */
    override def delete(roleId: RoleId): QIO[Unit] = run(query[Role].filter(_.id == lift(roleId)).delete).unit

    /** Get role by id */
    override def get(roleId: RoleId): QIO[Option[Role]] = run(query[Role].filter(_.id == lift(roleId)))
        .map(_.headOption)

    /** Update role name */
    override def update(id: RoleId, name: String): QIO[Unit] = run(
      query[Role].filter(_.id == lift(id)).update(_.name -> lift(name))
    ).unit

    /** Add role to User */
    override def addRoleToUser(userId: UserId, roleId: RoleId): QIO[Unit] = run(
      query[UserRoles].insertValue(lift(UserRoles(userId, roleId)))
    ).unit

    /** Remove role from User */
    override def removeRoleFromUser(userId: UserId, roleId: RoleId): QIO[Unit] = run(
      query[UserRoles].filter(ur => ur.roleId == lift(roleId) && ur.userId == lift(userId)).delete
    ).unit

    /** Add permission to Role */
    override def addPermissionToRole(roleId: RoleId, permission: String): QIO[Unit] = run(
      query[RolePermission].insertValue(lift(RolePermission(roleId, permission)))
    ).unit

    /** Remove permission from Role */
    override def removePermissionFromRole(roleId: RoleId, permission: String): QIO[Unit] = run(
      query[RolePermission].filter(up => up.roleId == lift(roleId) && up.permission == lift(permission)).delete
    ).unit
}
