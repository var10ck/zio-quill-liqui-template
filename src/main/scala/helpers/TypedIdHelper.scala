package helpers

import dao.entities.auth.UserId
import exceptions.InvalidIdException
import zio.ZIO

object TypedIdHelper {

    def parseUserId(id: String): ZIO[Any, InvalidIdException, UserId] =
        UserId.fromString(id).orElseFail(InvalidIdException("User"))

}
