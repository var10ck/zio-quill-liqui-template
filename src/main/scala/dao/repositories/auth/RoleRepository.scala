package dao.repositories.auth
import dao.entities.auth.{Role, RoleId, UserId}
import io.getquill.context.ZioJdbc.QIO
import zio.ZIO

import java.sql.SQLException
import javax.sql.DataSource

trait RoleRepository {

    /** Creates a new Role. */
    def create(name: String): QIO[Role]

    /** Deletes an existing Role. */
    def delete(roleId: RoleId): QIO[Unit]

    /** Get role by id */
    def get(roleId: RoleId): QIO[Option[Role]]

    /** Update role name */
    def update(id: RoleId, name: String): QIO[Unit]

    /** Add role to User */
    def addRoleToUser(userId: UserId, roleId: RoleId): QIO[Unit]

    /** Remove role from User */
    def removeRoleFromUser(userId: UserId, roleId: RoleId): QIO[Unit]

    /** Add permission to Role */
    def addPermissionToRole(roleId: RoleId, permission: String): QIO[Unit]

    /** Remove permission from Role */
    def removePermissionFromRole(roleId: RoleId, permission: String): QIO[Unit]
}

object RoleRepository {

    /** Creates a new Role. */
    def create(name: String): ZIO[DataSource with RoleRepository, SQLException, Role] =
        ZIO.serviceWithZIO[RoleRepository](_.create(name))

    /** Deletes an existing Role. */
    def delete(roleId: RoleId): ZIO[DataSource with RoleRepository, SQLException, Unit] =
        ZIO.serviceWithZIO[RoleRepository](_.delete(roleId))

    /** Get role by id */
    def get(roleId: RoleId): ZIO[DataSource with RoleRepository, SQLException, Option[Role]] =
        ZIO.serviceWithZIO[RoleRepository](_.get(roleId))

    /** Update role name */
    def update(id: RoleId, name: String): ZIO[DataSource with RoleRepository, SQLException, Unit] =
        ZIO.serviceWithZIO[RoleRepository](_.update(id, name))

    /** Add role to User */
    def addRoleToUser(userId: UserId, roleId: RoleId): ZIO[DataSource with RoleRepository, SQLException, Unit] =
        ZIO.serviceWithZIO[RoleRepository](_.addRoleToUser(userId, roleId))

    /** Remove role from User */
    def removeRoleFromUser(userId: UserId, roleId: RoleId): ZIO[DataSource with RoleRepository, SQLException, Unit] =
        ZIO.serviceWithZIO[RoleRepository](_.removeRoleFromUser(userId, roleId))

    /** Add permission to Role */
    def addPermissionToRole(
        roleId: RoleId,
        permission: String): ZIO[DataSource with RoleRepository, SQLException, Unit] =
        ZIO.serviceWithZIO[RoleRepository](_.addPermissionToRole(roleId, permission))

    /** Remove permission from Role */
    def removePermissionFromRole(
        roleId: RoleId,
        permission: String): ZIO[DataSource with RoleRepository, SQLException, Unit] =
        ZIO.serviceWithZIO[RoleRepository](_.removePermissionFromRole(roleId, permission))
}
