package dto.auth

import dao.entities.auth.UserSession

case class SessionCreatedDTO (sessionId: String)

object SessionCreatedDTO{
    implicit val codec: zio.json.JsonCodec[SessionCreatedDTO] = zio.json.DeriveJsonCodec.gen[SessionCreatedDTO]

    def apply(userSession: UserSession): SessionCreatedDTO = SessionCreatedDTO(userSession.id.toString)
}
