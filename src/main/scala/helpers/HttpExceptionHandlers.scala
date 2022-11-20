package helpers

import exceptions._
import zhttp.http.{Response, Status}

object HttpExceptionHandlers {

    val basicAuthExceptionHandler: PartialFunction[Throwable, Response] = {
        case _: HeaderNotSetException =>
            Response.text("Header userSessionId is not set").setStatus(Status.Unauthorized)
        case _: UserUnauthorizedException => Response.text("session not found").setStatus(Status.BadRequest)
        case _: NotEnoughRightsException =>
            Response.text("User is not author of object").setStatus(Status.BadRequest)
    }

    val lastResortHandler: PartialFunction[Throwable, Response] = { case e =>
        Response.text(e.getMessage).setStatus(Status.InternalServerError)
    }

    val bodyParsingExceptionHandler: PartialFunction[Throwable, Response] = { case e: BodyParsingException =>
        Response.text(e.getMessage).setStatus(Status.BadRequest)
    }

}
